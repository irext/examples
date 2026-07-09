package net.irext.ircontrol.compose.controller

/**
 * Filename:       ControlCommand.kt
 * Created:        Date: 2026-07-09
 *
 * Description:    Defines remote control command constants used by controllers and UI.
 *
 * Revision log:
 * 2026-07-09: created by shdmfire and strawmanbobi
 */

enum class RemoteCategory(val rawValue: Int) {
    NONE(0),
    AC(1),
    TV(2),
    STB(3),
    NETBOX(4),
    IPTV(5),
    DVD(6),
    FAN(7),
    PROJECTOR(8),
    STEREO(9),
    LIGHT(10),
    BSTB(11),
    CLEANING_ROBOT(12),
    AIRCLEANER(13),
    DYSON(14);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.rawValue == value } ?: NONE
    }
}

enum class ControlCommand {
    // Common / Media / Navigation
    Power,
    Mute,
    Up,
    Down,
    Left,
    Right,
    Ok,
    VolumePlus,
    VolumeMinus,
    Plus,
    Minus,
    Back,
    Input,
    Menu,
    Home,
    Settings,
    PageUp,
    PageDown,

    // Media playback (DVD, etc.)
    Play,
    Pause,
    Eject,
    Rewind,
    FastForward,

    // Air Conditioner (AC) / Dyson
    AcModeSwitch,
    AcTempPlus,
    AcTempMinus,
    AcWindSpeed,
    AcWindSwing,
    AcWindFix,

    // Fan / Dyson
    WindPlus,
    WindMinus,
    WindSpeed,
    WindType,
    Swing,

    // Projector
    ZoomIn,
    ZoomOut,

    // Light (Bulb)
    BulbColor0,
    BulbColor1,
    BulbColor2,
    BulbColor3,
    BulbColor4,
    BulbBrightPlus,
    BulbBrightMinus,
    BulbBrightPowerOn,
    BulbBrightPowerOff,
    BulbBrightRainbow,

    // Cleaning Robot
    RobotForward,
    RobotBackward,
    RobotLeft,
    RobotRight,
    RobotStart,
    RobotStop,
    RobotAuto,
    RobotSpot,
    RobotSpeed,
    RobotTimer,
    RobotCharge,
    RobotPreserve,

    // Air Cleaner
    AirCleanerIon,
    AirCleanerAuto,
    AirCleanerWindSpeed,
    AirCleanerModeSwitch,
    AirCleanerTimer,
    AirCleanerLight,
    AirCleanerForce,

    // Dyson specifics
    DysonWindSpeedPlus,
    DysonWindSpeedMinus,
    DysonTimerPlus,
    DysonTimerMinus,
    DysonAuto,
    DysonTempPlus,
    DysonTempMinus,
    DysonSwing,
    DysonDiffusion,
    DysonFav,
    DysonTimer,
    DysonSleep,
    DysonCool,
}
