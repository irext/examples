package net.irext.ircontrol.compose.ui.screen

import net.irext.ircontrol.compose.controller.AcControlState
import net.irext.ircontrol.compose.controller.RemoteCategory

/**
 * Filename:       ControlUiState.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Stores UI state for the remote control screen.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */

data class ControlUiState(
    val title: String = "",
    val emitterIp: String = "",
    val isEmitterConnected: Boolean = false,
    val isLoading: Boolean = false,
    val category: RemoteCategory = RemoteCategory.NONE,
    val acState: AcControlState = AcControlState(),
)
