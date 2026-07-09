package net.irext.ircontrol.compose.controller

import net.irext.ircontrol.compose.bean.RemoteControl

/**
 * Filename:       RemoteController.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Defines common remote controller interfaces, categories, and results.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

class RemoteController(
    private val phoneRemote: PhoneRemote,
    private val arduinoRemote: ArduinoRemote,
) {
    suspend fun send(
        remoteControl: RemoteControl,
        command: ControlCommand,
        acState: AcControlState? = null,
    ): ControlResult {
        return if (arduinoRemote.status.value == EmitterStatus.Working) {
            arduinoRemote.control(
                remoteControl.categoryId,
                remoteControl.subCategory,
                command,
                acState,
            )
        } else {
            phoneRemote.control(
                remoteControl.categoryId,
                remoteControl.subCategory,
                command,
                acState,
            )
        }
    }
}

enum class ControlResult {
    Success,
    Failed,
    PendingEmitterResult,
}
