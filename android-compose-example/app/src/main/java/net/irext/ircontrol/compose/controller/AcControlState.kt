package net.irext.ircontrol.compose.controller

import net.irext.decode.sdk.bean.ACStatus
import net.irext.decode.sdk.utils.Constants

/**
 * Filename:       ControlUiState.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    defines the  AcControlState  data class representing the state parameters of an air conditioner remote controller
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */

data class AcControlState(
    val power: Int = Constants.ACPower.POWER_OFF.value,
    val mode: Int = Constants.ACMode.MODE_COOL.value, // Default mode is COOL as per plan
    val temperature: Int = Constants.ACTemperature.TEMP_24.value,
    val windSpeed: Int = Constants.ACWindSpeed.SPEED_AUTO.value,
    val swing: Int = Constants.ACSwing.SWING_ON.value,
    val display: Int = 0,
    val sleep: Int = 0,
    val timer: Int = 0,
    val changeWindDir: Int = 0,
)

fun AcControlState.toACStatus() = ACStatus(
    power,
    mode,
    temperature,
    windSpeed,
    swing,
    display,
    sleep,
    timer,
    changeWindDir
)
