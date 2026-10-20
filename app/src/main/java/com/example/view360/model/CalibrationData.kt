package com.example.view360.model

data class CameraCalibOffset(
    val cameraId: Int, // 1: Front, 2: Right, 3: Rear, 4: Left
    val cameraName: String,
    val offsetX: Float = 0.0f,
    val offsetY: Float = 0.0f,
    val pitchDeg: Float = 0.0f,
    val yawDeg: Float = 0.0f,
    val fovScale: Float = 1.0f
)

data class StitchingSeamConfig(
    val blendWidth: Float = 1.0f,
    val cleanLeakMargin: Float = 0.0f,
    val sideViewOverlap: Float = 1.38f,
    val meshBalance: Boolean = true
)
