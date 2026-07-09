package net.irext.ircontrol.compose.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import net.irext.decode.sdk.utils.Constants
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.controller.AcControlState
import net.irext.ircontrol.compose.controller.ControlCommand
import net.irext.ircontrol.compose.controller.RemoteCategory
import net.irext.ircontrol.compose.ui.theme.IRControlTheme

/**
 * Filename:       ControlScreen.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Renders remote control panels and emitter connection controls.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire
 */

@Composable
fun ControlScreen(
    remoteId: Long,
    onBack: () -> Unit,
    viewModel: ControlViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context by rememberUpdatedState(LocalContext.current)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ControlEvent.Toast -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(remoteId, viewModel) {
        viewModel.loadRemote(remoteId)
    }

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.close()
        }
    }

    ControlContent(
        state = state,
        onBack = onBack,
        onCommand = viewModel::onCommand,
        onEmitterIpChange = viewModel::onEmitterIpChange,
        onConnectClick = viewModel::onConnectClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlContent(
    state: ControlUiState,
    onBack: () -> Unit,
    onCommand: (ControlCommand) -> Unit,
    onEmitterIpChange: (String) -> Unit,
    onConnectClick: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ControlTitle(
                title = state.title,
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dynamic control panel based on category
            when (state.category) {
                RemoteCategory.AC -> {
                    AcControlPanel(
                        acState = state.acState,
                        onCommand = onCommand,
                    )
                }
                RemoteCategory.DYSON -> {
                    DysonControlPanel(onCommand = onCommand)
                }
                RemoteCategory.TV,
                RemoteCategory.STB,
                RemoteCategory.NETBOX,
                RemoteCategory.IPTV,
                RemoteCategory.DVD,
                RemoteCategory.PROJECTOR,
                RemoteCategory.STEREO,
                RemoteCategory.BSTB -> {
                    MediaControlPanel(category = state.category, onCommand = onCommand)
                }
                RemoteCategory.FAN,
                RemoteCategory.LIGHT,
                RemoteCategory.CLEANING_ROBOT,
                RemoteCategory.AIRCLEANER -> {
                    GridControlPanel(category = state.category, onCommand = onCommand)
                }
                else -> {
                    Box(
                        modifier = Modifier.height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.loading_remote),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Emitter connection panel at the bottom
            EmitterConnectionPanel(
                ip = state.emitterIp,
                connected = state.isEmitterConnected,
                isLoading = state.isLoading,
                onIpChange = onEmitterIpChange,
                onConnectClick = onConnectClick,
            )
        }
    }
}

// ---------------- Reusable Capsule Button Group ----------------
@Composable
private fun CapsuleButtonGroup(
    leftIcon: ImageVector,
    leftClick: () -> Unit,
    centerText: String,
    rightIcon: ImageVector,
    rightClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 32.dp,
    iconSize: Dp = 18.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    isExpanded: Boolean = false
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(if (isExpanded) 24.dp else 16.dp)
            )
            .padding(
                horizontal = if (isExpanded) 8.dp else 6.dp,
                vertical = if (isExpanded) 4.dp else 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isExpanded) Arrangement.SpaceBetween else Arrangement.spacedBy(4.dp)
    ) {
        FilledTonalIconButton(
            onClick = leftClick,
            modifier = Modifier.size(buttonSize),
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.Transparent)
        ) {
            Icon(
                imageVector = leftIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        }
        
        if (isExpanded) {
            Spacer(modifier = Modifier.weight(1f))
        }
        
        Text(
            text = centerText,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (isExpanded) {
            Spacer(modifier = Modifier.weight(1f))
        }
        
        FilledTonalIconButton(
            onClick = rightClick,
            modifier = Modifier.size(buttonSize),
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.Transparent)
        ) {
            Icon(
                imageVector = rightIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MediaControlPanel(
    category: RemoteCategory,
    onCommand: (ControlCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showHome = category in listOf(RemoteCategory.TV, RemoteCategory.NETBOX, RemoteCategory.DVD)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Power (Top-Left)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onCommand(ControlCommand.Power) },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = stringResource(R.string.label_power),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // D-Pad Directional Keypad
        DPad(
            onCommand = onCommand,
            size = 200.dp,
            okSize = 72.dp,
            okIconSize = 28.dp,
            arrowSize = 48.dp,
            arrowIconSize = 32.dp
        )

        // Volume & Mute Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hasMute = category in listOf(RemoteCategory.TV, RemoteCategory.STB, RemoteCategory.IPTV, RemoteCategory.STEREO, RemoteCategory.BSTB)

            CapsuleButtonGroup(
                leftIcon = Icons.Default.Remove,
                leftClick = { onCommand(ControlCommand.Minus) },
                centerText = stringResource(R.string.label_vol),
                rightIcon = Icons.Default.Add,
                rightClick = { onCommand(ControlCommand.Plus) },
                modifier = if (hasMute) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                buttonSize = 40.dp,
                iconSize = 24.dp,
                textStyle = MaterialTheme.typography.labelMedium,
                isExpanded = true
            )

            if (hasMute) {
                FilledTonalIconButton(
                    onClick = { onCommand(ControlCommand.Mute) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeMute,
                        contentDescription = stringResource(R.string.label_mute),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Channel Page Row
        if (category in listOf(RemoteCategory.STB, RemoteCategory.IPTV, RemoteCategory.BSTB)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CapsuleButtonGroup(
                    leftIcon = Icons.Default.KeyboardArrowDown,
                    leftClick = { onCommand(ControlCommand.PageDown) },
                    centerText = stringResource(R.string.label_ch_pg),
                    rightIcon = Icons.Default.KeyboardArrowUp,
                    rightClick = { onCommand(ControlCommand.PageUp) },
                    modifier = Modifier.fillMaxWidth(),
                    buttonSize = 40.dp,
                    iconSize = 24.dp,
                    textStyle = MaterialTheme.typography.labelMedium,
                    isExpanded = true
                )
            }
        }

        // Navigation Capsule & Menu Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hasMenu = showHome && category != RemoteCategory.DVD

            CapsuleButtonGroup(
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                leftClick = { onCommand(ControlCommand.Back) },
                centerText = stringResource(R.string.label_nav),
                rightIcon = if (showHome) Icons.Default.Home else Icons.Default.Menu,
                rightClick = {
                    if (showHome) onCommand(ControlCommand.Home) else onCommand(ControlCommand.Menu)
                },
                modifier = if (hasMenu) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                buttonSize = 40.dp,
                iconSize = 24.dp,
                textStyle = MaterialTheme.typography.labelMedium,
                isExpanded = true
            )

            if (hasMenu) {
                FilledTonalButton(
                    onClick = { onCommand(ControlCommand.Menu) },
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.label_menu), style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Playback Row
        if (category == RemoteCategory.DVD) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CapsuleButtonGroup(
                    leftIcon = Icons.Default.PlayArrow,
                    leftClick = { onCommand(ControlCommand.Play) },
                    centerText = stringResource(R.string.label_play),
                    rightIcon = Icons.Default.Stop,
                    rightClick = { onCommand(ControlCommand.Pause) },
                    modifier = Modifier.fillMaxWidth(),
                    buttonSize = 40.dp,
                    iconSize = 24.dp,
                    textStyle = MaterialTheme.typography.labelMedium,
                    isExpanded = true
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AcControlPanel(
    acState: AcControlState,
    onCommand: (ControlCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    val powerText = if (acState.power == Constants.ACPower.POWER_ON.value) "ON" else "OFF"
    val modeText = when (acState.mode) {
        Constants.ACMode.MODE_COOL.value -> "COOL"
        Constants.ACMode.MODE_HEAT.value -> "HEAT"
        Constants.ACMode.MODE_AUTO.value -> "AUTO"
        Constants.ACMode.MODE_FAN.value -> "FAN"
        else -> "DRY"
    }
    val leftText = "$powerText · $modeText"

    val speedText = when (acState.windSpeed) {
        Constants.ACWindSpeed.SPEED_AUTO.value -> "AUTO"
        Constants.ACWindSpeed.SPEED_LOW.value -> "LOW"
        Constants.ACWindSpeed.SPEED_MEDIUM.value -> "MID"
        else -> "HIGH"
    }
    val swingText = if (acState.swing == Constants.ACSwing.SWING_ON.value) "SWING" else "FIXED"
    val rightText = "$speedText · $swingText"

    val tempText = "${acState.temperature + 16}℃"

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Virtual Screen Display
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (acState.power == Constants.ACPower.POWER_ON.value) 1.0f else 0.6f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = leftText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = rightText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = tempText,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Temperature Capsule
        CapsuleButtonGroup(
            leftIcon = Icons.Default.Remove,
            leftClick = { onCommand(ControlCommand.AcTempMinus) },
            centerText = stringResource(R.string.label_temp),
            rightIcon = Icons.Default.Add,
            rightClick = { onCommand(ControlCommand.AcTempPlus) },
            modifier = Modifier.fillMaxWidth(),
            buttonSize = 40.dp,
            iconSize = 24.dp,
            textStyle = MaterialTheme.typography.labelMedium,
            isExpanded = true
        )

        // AC Control Keys wrapping gracefully
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val keys = listOf(
                GridKeyItem(Icons.Default.PowerSettingsNew, stringResource(R.string.label_power), ControlCommand.Power),
                GridKeyItem(Icons.Default.Refresh, stringResource(R.string.label_mode), ControlCommand.AcModeSwitch),
                GridKeyItem(Icons.Default.Speed, stringResource(R.string.label_speed), ControlCommand.AcWindSpeed),
                GridKeyItem(Icons.Default.Sync, stringResource(R.string.label_swing), ControlCommand.AcWindSwing),
                GridKeyItem(Icons.Default.Check, stringResource(R.string.label_wind_fix), ControlCommand.AcWindFix)
            )

            keys.forEach { key ->
                ElevatedCard(
                    onClick = { onCommand(key.command) },
                    modifier = Modifier
                        .width(80.dp)
                        .height(62.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (key.command == ControlCommand.Power) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = key.icon,
                            contentDescription = key.label,
                            tint = if (key.command == ControlCommand.Power) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = key.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DysonControlPanel(
    onCommand: (ControlCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Dyson Screen View
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_dyson_cool_heat),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.label_speed_temp_dual_ctrl),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Speed & Temp adjustment Capsules
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CapsuleButtonGroup(
                leftIcon = Icons.Default.Remove,
                leftClick = { onCommand(ControlCommand.DysonWindSpeedMinus) },
                centerText = stringResource(R.string.label_spd),
                rightIcon = Icons.Default.Add,
                rightClick = { onCommand(ControlCommand.DysonWindSpeedPlus) },
                modifier = Modifier.fillMaxWidth(),
                buttonSize = 40.dp,
                iconSize = 24.dp,
                textStyle = MaterialTheme.typography.labelMedium,
                isExpanded = true
            )

            CapsuleButtonGroup(
                leftIcon = Icons.Default.Remove,
                leftClick = { onCommand(ControlCommand.DysonTempMinus) },
                centerText = stringResource(R.string.label_tmp),
                rightIcon = Icons.Default.Add,
                rightClick = { onCommand(ControlCommand.DysonTempPlus) },
                modifier = Modifier.fillMaxWidth(),
                buttonSize = 40.dp,
                iconSize = 24.dp,
                textStyle = MaterialTheme.typography.labelMedium,
                isExpanded = true
            )
        }

        // Functions in flow grid style
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val keys = listOf(
                GridKeyItem(Icons.Default.PowerSettingsNew, stringResource(R.string.label_power), ControlCommand.Power),
                GridKeyItem(Icons.Default.Refresh, stringResource(R.string.label_auto), ControlCommand.DysonAuto),
                GridKeyItem(Icons.Default.Sync, stringResource(R.string.label_swing), ControlCommand.DysonSwing),
                GridKeyItem(Icons.Default.Star, stringResource(R.string.label_cool), ControlCommand.DysonCool),
                GridKeyItem(Icons.Default.Settings, stringResource(R.string.label_sleep), ControlCommand.DysonSleep),
                GridKeyItem(Icons.Default.Info, stringResource(R.string.label_diffuse), ControlCommand.DysonDiffusion)
            )

            keys.forEach { key ->
                ElevatedCard(
                    onClick = { onCommand(key.command) },
                    modifier = Modifier
                        .width(80.dp)
                        .height(62.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (key.command == ControlCommand.Power) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = key.icon,
                            contentDescription = key.label,
                            tint = if (key.command == ControlCommand.Power) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = key.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

private data class GridKeyItem(
    val icon: ImageVector,
    val label: String,
    val command: ControlCommand,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GridControlPanel(
    category: RemoteCategory,
    onCommand: (ControlCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keys = when (category) {
        RemoteCategory.FAN -> listOf(
            GridKeyItem(Icons.Default.PowerSettingsNew, stringResource(R.string.label_power), ControlCommand.Power),
            GridKeyItem(Icons.Default.Speed, stringResource(R.string.label_wind_speed), ControlCommand.WindSpeed),
            GridKeyItem(Icons.Default.Refresh, stringResource(R.string.label_wind_type), ControlCommand.WindType),
            GridKeyItem(Icons.Default.Sync, stringResource(R.string.label_swing), ControlCommand.Swing),
            GridKeyItem(Icons.Default.Add, stringResource(R.string.label_wind_plus), ControlCommand.WindPlus),
            GridKeyItem(Icons.Default.Remove, stringResource(R.string.label_wind_minus), ControlCommand.WindMinus)
        )
        RemoteCategory.LIGHT -> listOf(
            GridKeyItem(Icons.Default.PowerSettingsNew, stringResource(R.string.label_power), ControlCommand.Power),
            GridKeyItem(Icons.Default.Add, stringResource(R.string.label_bright_plus), ControlCommand.BulbBrightPlus),
            GridKeyItem(Icons.Default.Remove, stringResource(R.string.label_bright_minus), ControlCommand.BulbBrightMinus),
            GridKeyItem(Icons.Default.Home, stringResource(R.string.label_bright_on), ControlCommand.BulbBrightPowerOn),
            GridKeyItem(Icons.Default.Menu, stringResource(R.string.label_bright_off), ControlCommand.BulbBrightPowerOff),
            GridKeyItem(Icons.Default.Star, stringResource(R.string.label_rainbow), ControlCommand.BulbBrightRainbow),
            GridKeyItem(Icons.Default.Check, stringResource(R.string.label_color_0), ControlCommand.BulbColor0),
            GridKeyItem(Icons.Default.Check, stringResource(R.string.label_color_1), ControlCommand.BulbColor1),
            GridKeyItem(Icons.Default.Check, stringResource(R.string.label_color_2), ControlCommand.BulbColor2),
            GridKeyItem(Icons.Default.Check, stringResource(R.string.label_color_3), ControlCommand.BulbColor3),
            GridKeyItem(Icons.Default.Check, stringResource(R.string.label_color_4), ControlCommand.BulbColor4)
        )
        RemoteCategory.CLEANING_ROBOT -> listOf(
            GridKeyItem(Icons.Default.PowerSettingsNew, stringResource(R.string.label_power), ControlCommand.Power),
            GridKeyItem(Icons.Default.PlayArrow, stringResource(R.string.label_start), ControlCommand.RobotStart),
            GridKeyItem(Icons.Default.Stop, stringResource(R.string.label_stop), ControlCommand.RobotStop),
            GridKeyItem(Icons.Default.ArrowUpward, stringResource(R.string.label_forward), ControlCommand.RobotForward),
            GridKeyItem(Icons.Default.ArrowDownward, stringResource(R.string.label_backward), ControlCommand.RobotBackward),
            GridKeyItem(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.label_left), ControlCommand.RobotLeft),
            GridKeyItem(Icons.AutoMirrored.Filled.ArrowForward, stringResource(R.string.label_right), ControlCommand.RobotRight),
            GridKeyItem(Icons.Default.Sync, stringResource(R.string.label_auto), ControlCommand.RobotAuto),
            GridKeyItem(Icons.Default.Check, stringResource(R.string.label_spot), ControlCommand.RobotSpot),
            GridKeyItem(Icons.Default.Speed, stringResource(R.string.label_speed), ControlCommand.RobotSpeed),
            GridKeyItem(Icons.Default.Settings, stringResource(R.string.label_timer), ControlCommand.RobotTimer),
            GridKeyItem(Icons.Default.Home, stringResource(R.string.label_charge), ControlCommand.RobotCharge),
            GridKeyItem(Icons.Default.Info, stringResource(R.string.label_preserve), ControlCommand.RobotPreserve)
        )
        RemoteCategory.AIRCLEANER -> listOf(
            GridKeyItem(Icons.Default.PowerSettingsNew, stringResource(R.string.label_power), ControlCommand.Power),
            GridKeyItem(Icons.Default.Star, stringResource(R.string.label_ion), ControlCommand.AirCleanerIon),
            GridKeyItem(Icons.Default.Sync, stringResource(R.string.label_auto), ControlCommand.AirCleanerAuto),
            GridKeyItem(Icons.Default.Speed, stringResource(R.string.label_wind_speed), ControlCommand.AirCleanerWindSpeed),
            GridKeyItem(Icons.Default.Refresh, stringResource(R.string.label_mode_switch), ControlCommand.AirCleanerModeSwitch),
            GridKeyItem(Icons.Default.Settings, stringResource(R.string.label_timer), ControlCommand.AirCleanerTimer),
            GridKeyItem(Icons.Default.Home, stringResource(R.string.label_light), ControlCommand.AirCleanerLight),
            GridKeyItem(Icons.Default.Info, stringResource(R.string.label_force), ControlCommand.AirCleanerForce)
        )
        else -> emptyList()
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { key ->
            ElevatedCard(
                onClick = { onCommand(key.command) },
                modifier = Modifier
                    .width(80.dp)
                    .height(62.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (key.command == ControlCommand.Power) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = key.icon,
                        contentDescription = key.label,
                        tint = if (key.command == ControlCommand.Power) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = key.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlTitle(
    title: String,
    onBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.button_back),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}

@Composable
private fun DPad(
    onCommand: (ControlCommand) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    okSize: Dp = 56.dp,
    okIconSize: Dp = 20.dp,
    arrowSize: Dp = 36.dp,
    arrowIconSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        FilledTonalButton(
            onClick = { onCommand(ControlCommand.Ok) },
            modifier = Modifier.size(okSize),
            shape = CircleShape,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.button_ok),
                modifier = Modifier.size(okIconSize)
            )
        }

        IconButton(
            onClick = { onCommand(ControlCommand.Up) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(arrowSize)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.button_up),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(arrowIconSize)
            )
        }

        IconButton(
            onClick = { onCommand(ControlCommand.Down) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(arrowSize)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.button_down),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(arrowIconSize)
            )
        }

        IconButton(
            onClick = { onCommand(ControlCommand.Left) },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(arrowSize)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.button_left),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(arrowIconSize)
            )
        }

        IconButton(
            onClick = { onCommand(ControlCommand.Right) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(arrowSize)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.button_right),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(arrowIconSize)
            )
        }
    }
}

@Composable
private fun EmitterConnectionPanel(
    ip: String,
    connected: Boolean,
    isLoading: Boolean,
    onIpChange: (String) -> Unit,
    onConnectClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LedIndicator(connected = connected)

                Text(
                    text = if (connected) "Connected" else "Disconnected",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (connected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = ip,
                    onValueChange = onIpChange,
                    label = { Text(stringResource(R.string.emitter_ip)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                FilledIconButton(
                    onClick = onConnectClick,
                    enabled = !isLoading,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (connected) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (connected) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (connected) Icons.Default.LinkOff else Icons.Default.Link,
                            contentDescription = stringResource(R.string.connect),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LedIndicator(connected: Boolean) {
    val transition = rememberInfiniteTransition(label = "LedGlow")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    Box(
        modifier = Modifier
            .size(10.dp)
            .alpha(if (connected) 1.0f else alpha)
            .background(
                color = if (connected) Color(0xFF4CAF50) else Color(0xFFF44336),
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun ControlContentPreview() {
    IRControlTheme {
        ControlContent(
            state = ControlUiState(
                title = "IR Remote Control",
                emitterIp = "192.168.1.10",
                isEmitterConnected = true,
                category = RemoteCategory.TV
            ),
            onBack = {},
            onCommand = {},
            onEmitterIpChange = {},
            onConnectClick = {},
        )
    }
}
