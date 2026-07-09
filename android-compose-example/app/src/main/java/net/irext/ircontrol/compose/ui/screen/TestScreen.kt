package net.irext.ircontrol.compose.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.irext.ircontrol.compose.IRApplication
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.controller.PhoneRemote
import net.irext.ircontrol.compose.controller.ControlCommand
import net.irext.ircontrol.compose.ui.navigation.RouteTest
import net.irext.ircontrol.compose.utils.remoteBinFile
import net.irext.ircontrol.compose.utils.writeFrom
import net.irext.webapi.model.RemoteIndex


/**
 * Filename:       TestScreen.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Supports an IR test workflow for iterating through remote indexes.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
private const val TEST_TAG = "TestScreen"
private const val TEST_INDEX_BATCH_SIZE = 40

private sealed class TestState {
    data object Loading : TestState()
    data class Ready(val indexes: List<RemoteIndex>) : TestState()
    data class Error(val message: String) : TestState()
}

@Composable
fun TestScreen(
    route: RouteTest,
    onBack: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as IRApplication
    val context = LocalContext.current
    val appContext = context.applicationContext
    val phoneRemote = remember { PhoneRemote(appContext) }
    val scope = rememberCoroutineScope()
    var currentPos by remember(route) { mutableIntStateOf(0) }
    var state by remember(route) { mutableStateOf<TestState>(TestState.Loading) }

    suspend fun loadAllRemoteIndexes(): List<RemoteIndex> {
        val allIndexes = mutableListOf<RemoteIndex>()
        var from = 0

        while (true) {
            val batch = WebApiHelper.listRemoteIndexes(
                api = app.mWeAPIs,
                categoryId = route.categoryId,
                brandId = route.brandId,
                cityCode = route.cityCode,
                operatorId = route.operatorId,
                withParaData = 0,
                from = from,
                count = TEST_INDEX_BATCH_SIZE,
            )

            allIndexes += batch
            Log.d(TEST_TAG, "load indexes batch: from=$from count=${batch.size} total=${allIndexes.size}")

            if (batch.size < TEST_INDEX_BATCH_SIZE) break
            from += batch.size
        }

        return allIndexes
    }

    fun loadIndexes() {
        scope.launch {
            state = TestState.Loading
            currentPos = 0
            try {
                val indexes = loadAllRemoteIndexes()
                state = if (indexes.isEmpty()) {
                    TestState.Error(context.getString(R.string.no_remote_indexes_found))
                } else {
                    TestState.Ready(indexes)
                }
            } catch (e: Exception) {
                Log.e(TEST_TAG, "load indexes failed", e)
                state = TestState.Error(e.message ?: context.getString(R.string.load_indexes_failed))
            }
        }
    }

    @SuppressLint("LocalContextGetResourceValueCall")
    fun sendPower() {
        val ready = state as? TestState.Ready ?: return
        val index = ready.indexes.getOrNull(currentPos) ?: return

        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val stream = WebApiHelper.downloadBin(app.mWeAPIs, index.remoteMap, index.id)
                    val binFile = appContext.remoteBinFile(index.remoteMap)
                    binFile.writeFrom(stream)

                    val openResult = phoneRemote.openBinary(
                        binFile.absolutePath,
                        index.categoryId,
                        index.subCate,
                    )
                    Log.d(TEST_TAG, "irOpen result=$openResult, file=${binFile.absolutePath}")
                    if (openResult != 0) {
                        throw IllegalStateException(context.getString(R.string.open_ir_binary_failed, openResult))
                    }

                    phoneRemote.control(index.categoryId, index.subCate, ControlCommand.Power)
                }
                Toast.makeText(context, context.getString(R.string.decode_and_send_success), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TEST_TAG, "send power failed", e)
                Toast.makeText(
                    context,
                    e.message ?: context.getString(R.string.decode_and_send_failed),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    LaunchedEffect(route) { loadIndexes() }

    DisposableEffect(Unit) {
        onDispose { phoneRemote.closeBinary() }
    }

    TestScreenContent(
        state = state,
        currentPos = currentPos,
        onBack = onBack,
        onRetry = { loadIndexes() },
        onPowerClick = { sendPower() },
        onPreviousClick = { if (currentPos > 0) currentPos-- },
        onNextClick = {
            val ready = state as? TestState.Ready
            if (ready != null && currentPos < ready.indexes.lastIndex) currentPos++
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestScreenContent(
    state: TestState,
    currentPos: Int,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onPowerClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ir_test_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
        }
    ) { padding ->
        when (state) {
            TestState.Loading -> FullScreenLoading(modifier = Modifier.padding(padding))
            is TestState.Error -> FullScreenError(
                message = state.message,
                onRetry = onRetry,
                modifier = Modifier.padding(padding),
            )
            is TestState.Ready -> {
                val indexes = state.indexes
                val index = indexes.getOrNull(currentPos)

                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = index?.remoteMap ?: stringResource(R.string.no_remote_index),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "${currentPos + 1} / ${indexes.size}",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = onPowerClick,
                        enabled = index != null,
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(),
                    ) {
                        Icon(
                            Icons.Filled.PowerSettingsNew,
                            contentDescription = stringResource(R.string.content_description_power),
                            modifier = Modifier.size(48.dp),
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(onClick = onPreviousClick, enabled = currentPos > 0) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = stringResource(R.string.content_description_previous),
                                modifier = Modifier.size(28.dp),
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Button(onClick = onNextClick, enabled = currentPos < indexes.lastIndex) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = stringResource(R.string.content_description_next),
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
