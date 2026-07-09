package net.irext.ircontrol.compose.controller

import android.content.Context
import android.util.Log
import net.irext.decode.sdk.IRDecode
import net.irext.decode.sdk.bean.ACStatus

/**
 * Filename:       PhoneRemote.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Sends decoded IR commands through the phone IR emitter.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

class PhoneRemote(
    context: Context,
    private val irTransmitter: IrTransmitter = IrTransmitter(context),
) {

    private val irDecode: IRDecode = IRDecode.getInstance()

    fun openBinary(remoteBinFilePath: String, category: Int, subCategory: Int): Int {
        return irDecode.openFile(category, subCategory, remoteBinFilePath)
    }

    fun control(category: Int, subCategory: Int, command: ControlCommand, acState: AcControlState? = null): ControlResult {
        val acStatus = acState?.toACStatus() ?: ACStatus()
        val inputKeyCode = command.toDecodeKeyCode(category)
        val decoded = irDecode.decodeBinary(inputKeyCode, acStatus) ?: return ControlResult.Failed

        Log.d(TAG, "IR control decoded: ${decoded.joinToString(",")}")
        return if (irTransmitter.transmit(decoded)) ControlResult.Success else ControlResult.Failed
    }

    fun closeBinary() {
        irDecode.closeBinary()
    }

    private companion object {
        private val TAG = PhoneRemote::class.java.simpleName
    }
}
