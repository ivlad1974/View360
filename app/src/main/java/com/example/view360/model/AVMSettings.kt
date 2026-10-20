package com.example.view360.model

enum class VehicleGear(val symbol: String, val label: String) {
    PARK("P", "Park"),
    REVERSE("R", "Reverse"),
    NEUTRAL("N", "Neutral"),
    DRIVE("D", "Drive")
}

enum class CameraViewMode(val label: String, val shortDesc: String) {
    FRONT("Front", "Wide-Angle Front View"),
    REAR("Rear", "Reversing Assistance"),
    LEFT("Left", "Left Blind Spot & Curb"),
    RIGHT("Right", "Right Blind Spot & Curb"),
    ORBIT_3D("3D Orbit", "Free 360° Vehicle Orbit"),
    QUAD_SPLIT("4-Way", "Quad Mosaic Split Screen")
}

enum class TurnSignalState {
    OFF, LEFT, RIGHT, HAZARD
}

data class UltrasonicRadarState(
    // 4 front sensors distance in cm (0 to 150 cm, 0 = closest/alert, 150+ = clear)
    val frontLeft: Int = 120,
    val frontCenterLeft: Int = 95,
    val frontCenterRight: Int = 110,
    val frontRight: Int = 135,
    // 4 rear sensors distance in cm
    val rearLeft: Int = 75,
    val rearCenterLeft: Int = 45,
    val rearCenterRight: Int = 50,
    val rearRight: Int = 80,
    // Side sensors
    val sideLeftFront: Int = 110,
    val sideLeftRear: Int = 90,
    val sideRightFront: Int = 100,
    val sideRightRear: Int = 120,
    val isMuted: Boolean = false
)

data class AVMPreferences(
    val dynamicGuidelinesEnabled: Boolean = true,
    val fixedGuidelinesEnabled: Boolean = true,
    val radarOverlayEnabled: Boolean = true,
    val radarAudioAlerts: Boolean = true,
    val bsdEnabled: Boolean = true, // Blind Spot Detection
    val bsdSpeedLimitKmh: Int = 30,
    val dowEnabled: Boolean = true, // Door Open Warning
    val startupAnimationEnabled: Boolean = true,
    val borderBlurEnabled: Boolean = false,
    val meshBalanceEnabled: Boolean = true,
    val brightness: Float = 1.0f,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f
)
