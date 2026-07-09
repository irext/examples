package net.irext.ircontrol.compose.controller

import net.irext.decode.sdk.utils.Constants

/**
 * Filename:       RemoteCommandMapper.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Maps UI commands to SDK remote control command values.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

fun ControlCommand.toDecodeKeyCode(category: Int): Int {
    if (Constants.CategoryID.AIR_CONDITIONER.value == category) {
        return when (this) {
            ControlCommand.Power -> Constants.ACFunction.FUNCTION_SWITCH_POWER.value
            ControlCommand.Up, ControlCommand.AcWindSpeed -> Constants.ACFunction.FUNCTION_SWITCH_WIND_SPEED.value
            ControlCommand.Down, ControlCommand.AcWindSwing -> Constants.ACFunction.FUNCTION_SWITCH_WIND_DIR.value
            ControlCommand.Right, ControlCommand.AcModeSwitch -> Constants.ACFunction.FUNCTION_CHANGE_MODE.value
            ControlCommand.Ok, ControlCommand.AcWindFix -> Constants.ACFunction.FUNCTION_SWITCH_SWING.value
            ControlCommand.Plus, ControlCommand.AcTempPlus -> Constants.ACFunction.FUNCTION_TEMPERATURE_UP.value
            ControlCommand.Minus, ControlCommand.AcTempMinus -> Constants.ACFunction.FUNCTION_TEMPERATURE_DOWN.value
            else -> -1
        }
    }

    return when (category) {
        Constants.CategoryID.TV.value -> when (this) {
            ControlCommand.Power -> 0   // KEY_TV_POWER
            ControlCommand.Mute -> 1    // KEY_TV_MUTE
            ControlCommand.Up -> 2      // KEY_TV_UP
            ControlCommand.Down -> 3    // KEY_TV_DOWN
            ControlCommand.Left -> 4    // KEY_TV_LEFT
            ControlCommand.Right -> 5   // KEY_TV_RIGHT
            ControlCommand.Ok -> 6      // KEY_TV_OK
            ControlCommand.VolumePlus, ControlCommand.Plus -> 7  // KEY_TV_VOL_PLUS
            ControlCommand.VolumeMinus, ControlCommand.Minus -> 8 // KEY_TV_VOL_MINUS
            ControlCommand.Back -> 9    // KEY_TV_BACK
            ControlCommand.Input -> 10  // KEY_TV_INPUT
            ControlCommand.Menu -> 11   // KEY_TV_MENU
            ControlCommand.Home -> 12   // KEY_TV_HOME
            ControlCommand.Settings -> 13 // KEY_TV_SETTINGS
            else -> -1
        }
        Constants.CategoryID.STB.value, Constants.CategoryID.BSTB.value -> when (this) {
            ControlCommand.Power -> 0   // KEY_STB_POWER
            ControlCommand.Mute -> 1    // KEY_STB_MUTE
            ControlCommand.Up -> 2      // KEY_STB_UP
            ControlCommand.Down -> 3    // KEY_STB_DOWN
            ControlCommand.Left -> 4    // KEY_STB_LEFT
            ControlCommand.Right -> 5   // KEY_STB_RIGHT
            ControlCommand.Ok -> 6      // KEY_STB_OK
            ControlCommand.VolumePlus, ControlCommand.Plus -> 7  // KEY_STB_VOL_PLUS
            ControlCommand.VolumeMinus, ControlCommand.Minus -> 8 // KEY_STB_VOL_MINUS
            ControlCommand.Back -> 9    // KEY_STB_BACK
            ControlCommand.Input -> 10  // KEY_STB_INPUT
            ControlCommand.Menu -> 11   // KEY_STB_MENU
            ControlCommand.PageUp -> 12 // KEY_STB_PAGE_UP
            ControlCommand.PageDown -> 13 // KEY_STB_PAGE_DOWN
            else -> -1
        }
        Constants.CategoryID.NET_BOX.value -> when (this) {
            ControlCommand.Power -> 0   // KEY_NETBOX_POWER
            ControlCommand.Up -> 1      // KEY_NETBOX_UP
            ControlCommand.Down -> 2    // KEY_NETBOX_DOWN
            ControlCommand.Left -> 3    // KEY_NETBOX_LEFT
            ControlCommand.Right -> 4   // KEY_NETBOX_RIGHT
            ControlCommand.Ok -> 5      // KEY_NETBOX_OK
            ControlCommand.VolumePlus, ControlCommand.Plus -> 6  // KEY_NETBOX_VOL_PLUS
            ControlCommand.VolumeMinus, ControlCommand.Minus -> 7 // KEY_NETBOX_VOL_MINUS
            ControlCommand.Back -> 8    // KEY_NETBOX_BACK
            ControlCommand.Menu -> 9    // KEY_NETBOX_MENU
            ControlCommand.Home -> 10   // KEY_NETBOX_HOME
            else -> -1
        }
        Constants.CategoryID.IPTV.value -> when (this) {
            ControlCommand.Power -> 0   // KEY_IPTV_POWER
            ControlCommand.Mute -> 1    // KEY_IPTV_MUTE
            ControlCommand.Up -> 2      // KEY_IPTV_UP
            ControlCommand.Down -> 3    // KEY_IPTV_DOWN
            ControlCommand.Left -> 4    // KEY_IPTV_LEFT
            ControlCommand.Right -> 5   // KEY_IPTV_RIGHT
            ControlCommand.Ok -> 6      // KEY_IPTV_OK
            ControlCommand.VolumePlus, ControlCommand.Plus -> 7  // KEY_IPTV_VOL_PLUS
            ControlCommand.VolumeMinus, ControlCommand.Minus -> 8 // KEY_IPTV_VOL_MINUS
            ControlCommand.Back -> 9    // KEY_IPTV_BACK
            ControlCommand.Input -> 10  // KEY_IPTV_INPUT
            ControlCommand.Menu -> 11   // KEY_IPTV_MENU
            ControlCommand.PageUp -> 12 // KEY_IPTV_PAGE_UP
            ControlCommand.PageDown -> 13 // KEY_IPTV_PAGE_DOWN
            else -> -1
        }
        Constants.CategoryID.DVD.value -> when (this) {
            ControlCommand.Power -> 0   // KEY_DVD_POWER
            ControlCommand.Up -> 1      // KEY_DVD_UP
            ControlCommand.Down -> 2    // KEY_DVD_DOWN
            ControlCommand.Left -> 3    // KEY_DVD_LEFT
            ControlCommand.Right -> 4   // KEY_DVD_RIGHT
            ControlCommand.Ok -> 5      // KEY_DVD_OK
            ControlCommand.VolumePlus, ControlCommand.Plus -> 6  // KEY_DVD_VOL_PLUS
            ControlCommand.VolumeMinus, ControlCommand.Minus -> 7 // KEY_DVD_VOL_MINUS
            ControlCommand.Play -> 8    // KEY_DVD_PLAY
            ControlCommand.Pause -> 9   // KEY_DVD_PAUSE
            ControlCommand.Eject -> 10  // KEY_DVD_EJECT
            ControlCommand.Rewind -> 11 // KEY_DVD_REWIND
            ControlCommand.FastForward -> 12 // KEY_DVD_FASTFORWARD
            ControlCommand.Menu -> 13   // KEY_DVD_MENU
            else -> -1
        }
        Constants.CategoryID.FAN.value -> when (this) {
            ControlCommand.Power -> 0      // KEY_FAN_POWER
            ControlCommand.VolumePlus, ControlCommand.WindPlus, ControlCommand.Plus -> 6  // KEY_FAN_WIND_PLUS
            ControlCommand.VolumeMinus, ControlCommand.WindMinus, ControlCommand.Minus -> 7 // KEY_FAN_WIND_MUNIS
            ControlCommand.Swing -> 8      // KEY_FAN_SWING
            ControlCommand.WindSpeed -> 9  // KEY_FAN_WIND_SPEED
            ControlCommand.WindType -> 10  // KEY_FAN_WIND_TYPE
            else -> -1
        }
        Constants.CategoryID.PROJECTOR.value -> when (this) {
            ControlCommand.Power -> 0   // KEY_PROJECTOR_POWER
            ControlCommand.Up -> 1      // KEY_PROJECTOR_UP
            ControlCommand.Down -> 2    // KEY_PROJECTOR_DOWN
            ControlCommand.Left -> 3    // KEY_PROJECTOR_LEFT
            ControlCommand.Right -> 4   // KEY_PROJECTOR_RIGHT
            ControlCommand.Ok -> 5      // KEY_PROJECTOR_OK
            ControlCommand.VolumePlus, ControlCommand.Plus -> 6  // KEY_PROJECTOR_VOL_PLUS
            ControlCommand.VolumeMinus, ControlCommand.Minus -> 7 // KEY_PROJECTOR_VOL_MINUS
            ControlCommand.ZoomOut -> 8 // KEY_PROJECTOR_ZOOM_OUT
            ControlCommand.Menu -> 9    // KEY_PROJECTOR_MENU
            ControlCommand.ZoomIn -> 10 // KEY_PROJECTOR_ZOOM_IN
            ControlCommand.Back -> 11   // KEY_PROJECTOR_BACK
            else -> -1
        }
        Constants.CategoryID.STEREO.value -> when (this) {
            ControlCommand.Power -> 0   // KEY_STEREO_POWER
            ControlCommand.Up -> 1      // KEY_STEREO_UP
            ControlCommand.Down -> 2    // KEY_STEREO_DOWN
            ControlCommand.Left -> 3    // KEY_STEREO_LEFT
            ControlCommand.Right -> 4   // KEY_STEREO_RIGHT
            ControlCommand.Ok -> 5      // KEY_STEREO_OK
            ControlCommand.VolumePlus, ControlCommand.Plus -> 6  // KEY_STEREO_VOL_PLUS
            ControlCommand.VolumeMinus, ControlCommand.Minus -> 7 // KEY_STEREO_VOL_MINUS
            ControlCommand.Mute -> 8    // KEY_STEREO_MUTE
            ControlCommand.Menu -> 9    // KEY_STEREO_MENU
            else -> -1
        }
        Constants.CategoryID.LIGHT.value -> when (this) {
            ControlCommand.Power -> 0            // KEY_BULB_POWER
            ControlCommand.BulbColor1 -> 1       // KEY_BULB_COLOR_1
            ControlCommand.BulbColor2 -> 2       // KEY_BULB_COLOR_2
            ControlCommand.BulbColor3 -> 3       // KEY_BULB_COLOR_3
            ControlCommand.BulbColor4 -> 4       // KEY_BULB_COLOR_4
            ControlCommand.BulbColor0 -> 5       // KEY_BULB_COLOR_0
            ControlCommand.VolumePlus, ControlCommand.BulbBrightPlus, ControlCommand.Plus -> 6  // KEY_BULB_BRIGHT_PLUS
            ControlCommand.VolumeMinus, ControlCommand.BulbBrightMinus, ControlCommand.Minus -> 7 // KEY_BULB_BRIGHT_MINUS
            ControlCommand.BulbBrightPowerOn -> 8 // KEY_BULB_BRIGHT_POWER_ON
            ControlCommand.BulbBrightRainbow -> 9 // KEY_BULB_BRIGHT_RAINBOW
            ControlCommand.BulbBrightPowerOff -> 10 // KEY_BULB_BRIGHT_POWER_OFF
            else -> -1
        }
        Constants.CategoryID.CLEANING_ROBOT.value -> when (this) {
            ControlCommand.Power -> 0          // KEY_CLEANROBOT_POWER
            ControlCommand.RobotForward -> 1   // KEY_CLEANROBOT_FOWWARD
            ControlCommand.RobotBackward -> 2  // KEY_CLEANROBOT_BACKWARD
            ControlCommand.RobotLeft -> 3      // KEY_CLEANROBOT_LEFT
            ControlCommand.RobotRight -> 4     // KEY_CLEANROBOT_RIGHT
            ControlCommand.RobotStart -> 5     // KEY_CLEANROBOT_START
            ControlCommand.RobotStop -> 6      // KEY_CLEANROBOT_STOP
            ControlCommand.RobotAuto -> 8      // KEY_CLEANROBOT_AUTO
            ControlCommand.RobotSpot -> 9      // KEY_CLEANROBOT_SPOT
            ControlCommand.RobotSpeed -> 10    // KEY_CLEANROBOT_SPEED
            ControlCommand.RobotTimer -> 11    // KEY_CLEANROBOT_TIMER
            ControlCommand.RobotCharge -> 12   // KEY_CLEANROBOT_CHARGE
            ControlCommand.RobotPreserve -> 13 // KEY_CLEANROBOT_PRESERVE
            else -> -1
        }
        Constants.CategoryID.AIR_CLEANER.value -> when (this) {
            ControlCommand.Power -> 0                // KEY_AIRCLEANER_POWER
            ControlCommand.AirCleanerIon -> 5        // KEY_AIRCLEANER_ION
            ControlCommand.AirCleanerAuto -> 8       // KEY_AIRCLEANER_AUTO
            ControlCommand.AirCleanerWindSpeed -> 9  // KEY_AIRCLEANER_WIND_SPEED
            ControlCommand.AirCleanerModeSwitch -> 10 // KEY_AIRCLEANER_MODE_SWITCH
            ControlCommand.AirCleanerTimer -> 11     // KEY_AIRCLEANER_TIMER
            ControlCommand.AirCleanerLight -> 12     // KEY_AIRCLEANER_LIGHT
            ControlCommand.AirCleanerForce -> 13     // KEY_AIRCLEANER_FORCE
            else -> -1
        }
        Constants.CategoryID.DYSON_SERIES.value -> when (this) {
            ControlCommand.Power -> 0                 // KEY_DYSON_POWER
            ControlCommand.DysonWindSpeedPlus -> 1    // KEY_DYSON_WIND_SPEED_PLUS
            ControlCommand.DysonWindSpeedMinus -> 2   // KEY_DYSON_WIND_SPEED_MINUS
            ControlCommand.DysonTimerMinus -> 3       // KEY_DYSON_TIMER_MINUS
            ControlCommand.DysonTimerPlus -> 4        // KEY_DYSON_TIMER_PLUS
            ControlCommand.DysonAuto -> 5             // KEY_DYSON_AUTO
            ControlCommand.DysonTempPlus -> 6         // KEY_DYSON_TEMP_PLUS
            ControlCommand.DysonTempMinus -> 7        // KEY_DYSON_TEMP_MINUS
            ControlCommand.DysonSwing -> 8            // KEY_DYSON_SWING
            ControlCommand.DysonDiffusion -> 9        // KEY_DYSON_DIFFUSION
            ControlCommand.DysonFav -> 10             // KEY_DYSON_FAV
            ControlCommand.DysonTimer -> 11           // KEY_DYSON_TIMER
            ControlCommand.DysonSleep -> 12           // KEY_DYSON_SLEEP
            ControlCommand.DysonCool -> 13            // KEY_DYSON_COOL
            else -> -1
        }
        else -> -1
    }
}
