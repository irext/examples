package net.irext.ircontrol.compose.controller

import android.content.Context
import android.hardware.ConsumerIrManager

/**
 * Filename:       IrTransmitter.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Wraps Android consumer IR transmission support.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

class IrTransmitter(
    context: Context,
) {
    private val irManager = context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    fun transmit(decoded: IntArray): Boolean {
        if (decoded.isEmpty()) return false
        val manager = irManager ?: return false
        if (!manager.hasIrEmitter()) return false
        manager.transmit(CarrierFrequency, decoded)
        return true
    }

    private companion object {
        private const val CarrierFrequency = 38_000
    }
}
