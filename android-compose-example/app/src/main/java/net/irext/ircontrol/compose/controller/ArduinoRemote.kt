package net.irext.ircontrol.compose.controller

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import net.irext.decode.sdk.bean.ACStatus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Base64

/**
 * Filename:       ArduinoRemote.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Implements socket-based control for an external Arduino IR emitter.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

class ArduinoRemote {

    private val _status = MutableStateFlow(EmitterStatus.Disconnected)
    val status: StateFlow<EmitterStatus> = _status.asStateFlow()

    private val _events = MutableSharedFlow<EmitterEvent>()
    val events: SharedFlow<EmitterEvent> = _events.asSharedFlow()

    private var socket: Socket? = null

    suspend fun connect(ip: String, port: Int = EmitterPort) = withContext(Dispatchers.IO) {
        if (_status.value != EmitterStatus.Disconnected) {
            disconnect()
            return@withContext
        }

        try {
            val newSocket = Socket(ip, port).also { it.keepAlive = true }
            socket = newSocket
            _status.value = EmitterStatus.Connected
            _events.emit(EmitterEvent.Connected)

            val reader = BufferedReader(InputStreamReader(newSocket.getInputStream()))
            while (true) {
                val response = reader.readLine() ?: break
                handleResponse(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection error: ${e.message}")
        } finally {
            socket = null
            _status.value = EmitterStatus.Disconnected
            _events.emit(EmitterEvent.Disconnected)
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        socket?.close()
        socket = null
        _status.value = EmitterStatus.Disconnected
    }

    suspend fun sendHello() = sendLine(A_REQUEST_HELLO)

    suspend fun sendBin(binContent: ByteArray, categoryId: Int, subCategory: Int) {
        val binBase64 = Base64.getEncoder().encodeToString(binContent)
        sendLine("$A_REQUEST_BIN,$categoryId,$subCategory,${binBase64.length},$binBase64")
    }

    suspend fun control(category: Int, subCategory: Int, command: ControlCommand, acState: AcControlState? = null): ControlResult {
        Log.d(TAG, "control, category = $category, subCategory = $subCategory, command = $command")
        val acStatus = acState?.toACStatus() ?: ACStatus()
        val keyCode = command.toDecodeKeyCode(category)
        val commandText = ArduinoControlCommand(keyCode, acStatus).encode()
        sendLine("$A_REQUEST_CTRL,${commandText.length},$commandText")
        return ControlResult.PendingEmitterResult
    }

    private suspend fun handleResponse(response: String) {
        Log.d(TAG, "emitter response: $response")
        if (response.startsWith(E_RESPONSE_CTRL)) {
            _status.value = EmitterStatus.Working
        }
        _events.emit(EmitterEvent.Response(response))
    }

    private suspend fun sendLine(line: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "send to emitter: $line")
        PrintWriter(requireNotNull(socket).getOutputStream(), true).println(line)
    }

    private class ArduinoControlCommand(
        val keyCode: Int,
        val acStatus: ACStatus,
    ) {
        fun encode(): String = Base64.getEncoder().encodeToString(Gson().toJson(this).toByteArray())
    }

    companion object {
        private val TAG = ArduinoRemote::class.java.simpleName

        const val EmitterPort = 8000

        const val A_REQUEST_HELLO = "a_hello"
        const val E_RESPONSE_HELLO = "e_hello"

        const val A_REQUEST_BIN = "a_bin"
        const val E_RESPONSE_BIN = "e_bin"

        const val A_REQUEST_CTRL = "a_control"
        const val E_RESPONSE_CTRL = "e_control"

        const val E_INDICATION_SUCCESS = "e_success"
        const val E_INDICATION_FAILED = "e_failed"
    }
}

enum class EmitterStatus { Disconnected, Connected, Working }

sealed interface EmitterEvent {
    data object Connected : EmitterEvent
    data object Disconnected : EmitterEvent
    data class Response(val raw: String) : EmitterEvent
}
