package net.irext.ircontrol.compose.ui.screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.irext.decode.sdk.utils.Constants
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.bean.RemoteControl
import net.irext.ircontrol.compose.controller.ArduinoRemote
import net.irext.ircontrol.compose.controller.ControlResult
import net.irext.ircontrol.compose.controller.EmitterEvent
import net.irext.ircontrol.compose.controller.PhoneRemote
import net.irext.ircontrol.compose.controller.AcControlState
import net.irext.ircontrol.compose.controller.RemoteController
import net.irext.ircontrol.compose.controller.ControlCommand
import net.irext.ircontrol.compose.controller.RemoteCategory
import net.irext.ircontrol.compose.data.RemoteControlRepository
import net.irext.ircontrol.compose.utils.readBytesOrNull
import net.irext.ircontrol.compose.utils.remoteBinFile

/**
 * Filename:       ControlViewModel.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Loads remote data, manages emitter connection, and dispatches control commands.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire
 */

class ControlViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val repository = RemoteControlRepository()
    private val phoneRemote = PhoneRemote(appContext)
    private val arduinoRemote = ArduinoRemote()
    private val remoteController = RemoteController(phoneRemote, arduinoRemote)

    private val _uiState = MutableStateFlow(
        ControlUiState(emitterIp = application.getString(R.string.default_ip)),
    )
    val uiState: StateFlow<ControlUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ControlEvent>()
    val events: SharedFlow<ControlEvent> = _events.asSharedFlow()

    private var currentRemoteControl: RemoteControl? = null
    private var currentRemoteId: Long = -1L

    init {
        viewModelScope.launch {
            arduinoRemote.events.collect { event ->
                when (event) {
                    EmitterEvent.Connected -> onEmitterConnected()
                    EmitterEvent.Disconnected -> onEmitterDisconnected()
                    is EmitterEvent.Response -> onEmitterResponse(event.raw)
                }
            }
        }
    }

    fun loadRemote(remoteId: Long) {
        currentRemoteId = remoteId
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val remote = repository.get(remoteId)
            currentRemoteControl = remote

            if (remote == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val ret = phoneRemote.openBinary(
                appContext.remoteBinFile(remote.remoteMap.orEmpty()).absolutePath,
                remote.categoryId,
                remote.subCategory,
            )
            Log.d(TAG, "binary opened : $ret")

            _uiState.update {
                it.copy(
                    title = buildRemoteTitle(remote),
                    isLoading = false,
                    category = RemoteCategory.fromInt(remote.categoryId),
                )
            }
        }
    }

    fun onCommand(command: ControlCommand) {
        viewModelScope.launch(Dispatchers.IO) {
            val remote = currentRemoteControl ?: return@launch
            val acState = if (remote.categoryId == Constants.CategoryID.AIR_CONDITIONER.value) {
                val next = reduceAcState(command, _uiState.value.acState)
                _uiState.update { it.copy(acState = next) }
                next
            } else null
            processControlResult(remoteController.send(remote, command, acState))
        }
    }

    private fun reduceAcState(command: ControlCommand, state: AcControlState): AcControlState {
        return when (command) {
            ControlCommand.Power -> {
                val nextPower = if (state.power == Constants.ACPower.POWER_ON.value) {
                    Constants.ACPower.POWER_OFF.value
                } else {
                    Constants.ACPower.POWER_ON.value
                }
                state.copy(power = nextPower, changeWindDir = 0)
            }
            ControlCommand.Plus, ControlCommand.AcTempPlus -> {
                val nextTemp = (state.temperature + 1).coerceAtMost(Constants.ACTemperature.TEMP_30.value)
                state.copy(temperature = nextTemp, changeWindDir = 0)
            }
            ControlCommand.Minus, ControlCommand.AcTempMinus -> {
                val nextTemp = (state.temperature - 1).coerceAtLeast(Constants.ACTemperature.TEMP_16.value)
                state.copy(temperature = nextTemp, changeWindDir = 0)
            }
            ControlCommand.Right, ControlCommand.AcModeSwitch -> {
                val nextMode = (state.mode + 1) % 5
                state.copy(mode = nextMode, changeWindDir = 0)
            }
            ControlCommand.Up, ControlCommand.AcWindSpeed -> {
                val nextSpeed = (state.windSpeed + 1) % 4
                state.copy(windSpeed = nextSpeed, changeWindDir = 0)
            }
            ControlCommand.Down, ControlCommand.AcWindSwing -> {
                val nextSwing = if (state.swing == Constants.ACSwing.SWING_ON.value) {
                    Constants.ACSwing.SWING_OFF.value
                } else {
                    Constants.ACSwing.SWING_ON.value
                }
                state.copy(swing = nextSwing, changeWindDir = 0)
            }
            ControlCommand.Ok, ControlCommand.AcWindFix -> {
                state.copy(changeWindDir = 1)
            }
            else -> state
        }
    }

    fun onEmitterIpChange(value: String) {
        _uiState.update { it.copy(emitterIp = value) }
    }

    fun onConnectClick() {
        val state = _uiState.value
        if (state.isEmitterConnected) disconnectEmitter() else connectEmitter(state.emitterIp)
    }

    fun close() {
        viewModelScope.launch(Dispatchers.IO) { arduinoRemote.disconnect() }
        phoneRemote.closeBinary()
        _uiState.update { it.copy(isEmitterConnected = false, isLoading = false) }
    }

    override fun onCleared() {
        close()
    }

    private fun connectEmitter(ip: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            arduinoRemote.connect(ip)
        }
    }

    private fun disconnectEmitter() {
        viewModelScope.launch(Dispatchers.IO) {
            arduinoRemote.disconnect()
            _uiState.update { it.copy(isEmitterConnected = false, isLoading = false) }
        }
    }

    private fun onEmitterConnected() {
        _uiState.update { it.copy(isEmitterConnected = true, isLoading = false) }
    }

    private fun onEmitterDisconnected() {
        _uiState.update { it.copy(isEmitterConnected = false, isLoading = false) }
        viewModelScope.launch {
            _events.emit(ControlEvent.Toast(appContext.getString(R.string.connect_disconnected)))
        }
    }

    private fun onEmitterResponse(response: String) {
        when {
            response.startsWith(ArduinoRemote.E_RESPONSE_HELLO) -> processEHello()
            response.startsWith(ArduinoRemote.E_RESPONSE_BIN) -> processEBin()
            response.startsWith(ArduinoRemote.E_RESPONSE_CTRL) -> Unit
            response.startsWith(ArduinoRemote.E_INDICATION_SUCCESS) ||
                response.startsWith(ArduinoRemote.E_INDICATION_FAILED) -> processControlResult(response)
            else -> Log.e(TAG, "unexpected response : $response")
        }
    }

    private fun processEHello() {
        viewModelScope.launch(Dispatchers.IO) { arduinoRemote.sendHello() }
    }

    private fun processEBin() {
        viewModelScope.launch(Dispatchers.IO) {
            val remote = currentRemoteControl ?: repository.get(currentRemoteId) ?: return@launch
            currentRemoteControl = remote
            val binContent = appContext.remoteBinFile(remote.remoteMap.orEmpty()).readBytesOrNull()
            if (binContent == null) {
                _events.emit(ControlEvent.Toast(appContext.getString(R.string.file_could_not_open)))
                return@launch
            }
            arduinoRemote.sendBin(binContent, remote.categoryId, remote.subCategory)
        }
    }

    private fun processControlResult(result: ControlResult) {
        viewModelScope.launch {
            when (result) {
                ControlResult.Success -> _events.emit(ControlEvent.Toast(appContext.getString(R.string.decode_and_send_success)))
                ControlResult.Failed -> _events.emit(ControlEvent.Toast(appContext.getString(R.string.decode_and_send_failed)))
                ControlResult.PendingEmitterResult -> Unit
            }
        }
    }

    private fun processControlResult(response: String) {
        viewModelScope.launch {
            val message = if (response.startsWith(ArduinoRemote.E_INDICATION_SUCCESS)) {
                R.string.decode_and_send_success
            } else {
                R.string.decode_and_send_failed
            }
            _events.emit(ControlEvent.Toast(appContext.getString(message)))
        }
    }

    private fun buildRemoteTitle(remote: RemoteControl): String {
        return if (Constants.CategoryID.STB.value == remote.categoryId) {
            remote.cityName + remote.operatorName + remote.categoryName + " - " + remote.remote
        } else {
            remote.brandName + remote.categoryName + " - " + remote.remote
        }
    }

    private companion object {
        private const val TAG = "ControlViewModel"
    }
}
