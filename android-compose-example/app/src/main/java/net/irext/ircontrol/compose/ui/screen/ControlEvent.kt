package net.irext.ircontrol.compose.ui.screen


/**
 * Filename:       ControlEvent.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Defines one-time events emitted by the control screen view model.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */

sealed interface ControlEvent {
    data class Toast(val message: String) : ControlEvent
}
