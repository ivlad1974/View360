package com.example.view360.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.model.AVMPreferences
import com.example.view360.model.CarModelItem
import com.example.view360.model.CarPaint
import com.example.view360.model.TurnSignalState
import com.example.view360.model.UltrasonicRadarState
import com.example.view360.model.VehicleGear
import com.example.view360.ui.theme.AlertRed
import com.example.view360.ui.theme.AutomotiveBackground
import com.example.view360.ui.theme.CautionYellow
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.ui.theme.RadarDanger
import com.example.view360.ui.theme.RadarSafe
import com.example.view360.ui.theme.RadarWarning
import com.example.view360.ui.theme.SafeGreen
import kotlin.math.abs
import kotlin.math.tan

@Composable
fun BirdviewCanvas(
    carModel: CarModelItem,
    paint: CarPaint,
    licensePlate: String,
    gear: VehicleGear,
    steeringAngleDeg: Float,
    turnSignal: TurnSignalState,
    radarState: UltrasonicRadarState,
    preferences: AVMPreferences,
    leftDoorOpen: Boolean,
    rightDoorOpen: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radarPulse"
    )

    Box(
        modifier = modifier
            .background(AutomotiveBackground, RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // 1. Draw Ground Calibration Grid & Distance Rings
            drawGroundGrid(cx, cy)

            // 2. Draw Camera Stitching Blend Lines (from corners)
            drawStitchingLines(cx, cy)

            // 3. Draw Ultrasonic Radar Waves
            if (preferences.radarOverlayEnabled) {
                drawRadarSensors(cx, cy, radarState, pulseAlpha)
            }

            // 4. Draw Dynamic Steering Guide Lines
            if (preferences.dynamicGuidelinesEnabled) {
                drawDynamicTrajectory(cx, cy, gear, steeringAngleDeg)
            }

            // 5. Draw Vehicle Body Top-Down Silhouette
            drawTopDownVehicle(
                cx = cx,
                cy = cy,
                paintColor = paint.color,
                wheelAngleDeg = (steeringAngleDeg / 540f) * 35f,
                leftDoorOpen = leftDoorOpen,
                rightDoorOpen = rightDoorOpen,
                turnSignal = turnSignal
            )
        }

        // HUD Tag
        Text(
            text = "360° TOP VIEW",
            color = CyberCyan.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        )
    }
}

private fun DrawScope.drawGroundGrid(cx: Float, cy: Float) {
    val gridColor = Color(0xFF141E2E)
    val distanceDashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

    // Distance metric rings: 0.5m, 1.0m, 2.0m, 3.0m
    val radii = listOf(90f, 150f, 230f, 320f)
    val ringColors = listOf(AlertRed.copy(0.2f), CautionYellow.copy(0.2f), SafeGreen.copy(0.2f), CyberCyan.copy(0.15f))

    radii.forEachIndexed { index, r ->
        drawRoundRect(
            color = ringColors[index],
            topLeft = Offset(cx - r * 0.75f, cy - r),
            size = Size(r * 1.5f, r * 2f),
            cornerRadius = CornerRadius(24f, 24f),
            style = Stroke(width = 1.5f, pathEffect = distanceDashEffect)
        )
    }

    // Parking slot boundary guide lines
    val slotColor = Color(0xFF1E2C42)
    drawLine(slotColor, Offset(cx - 130f, cy - 340f), Offset(cx - 130f, cy + 340f), strokeWidth = 2f)
    drawLine(slotColor, Offset(cx + 130f, cy - 340f), Offset(cx + 130f, cy + 340f), strokeWidth = 2f)
}

private fun DrawScope.drawStitchingLines(cx: Float, cy: Float) {
    val seamColor = CyberCyan.copy(alpha = 0.18f)
    val seamDash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)

    // Diagonal seams from 4 camera angles
    drawLine(seamColor, Offset(cx, cy), Offset(0f, 0f), strokeWidth = 1.5f, pathEffect = seamDash)
    drawLine(seamColor, Offset(cx, cy), Offset(size.width, 0f), strokeWidth = 1.5f, pathEffect = seamDash)
    drawLine(seamColor, Offset(cx, cy), Offset(0f, size.height), strokeWidth = 1.5f, pathEffect = seamDash)
    drawLine(seamColor, Offset(cx, cy), Offset(size.width, size.height), strokeWidth = 1.5f, pathEffect = seamDash)
}

private fun DrawScope.drawDynamicTrajectory(
    cx: Float,
    cy: Float,
    gear: VehicleGear,
    steeringAngleDeg: Float
) {
    val isReverse = gear == VehicleGear.REVERSE
    val trackWidth = 55f
    val curveBias = (steeringAngleDeg / 540f) * 65f

    // We draw left & right guideline tracks with green (far), yellow (mid), red (stop)
    val direction = if (isReverse) 1f else -1f
    val startY = cy + (direction * 110f)

    // Three depth segments
    val segments = listOf(
        Triple(0f, 60f, AlertRed),        // 0.5m stop zone
        Triple(60f, 130f, CautionYellow), // 1.5m warning zone
        Triple(130f, 210f, SafeGreen)     // 3.0m safe zone
    )

    for ((d1, d2, color) in segments) {
        val y1 = startY + (direction * d1)
        val y2 = startY + (direction * d2)
        val curvature1 = (d1 / 210f) * curveBias
        val curvature2 = (d2 / 210f) * curveBias

        // Left track segment
        drawLine(
            color = color,
            start = Offset(cx - trackWidth + curvature1, y1),
            end = Offset(cx - trackWidth + curvature2, y2),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        // Right track segment
        drawLine(
            color = color,
            start = Offset(cx + trackWidth + curvature1, y1),
            end = Offset(cx + trackWidth + curvature2, y2),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        // Cross bar at segment ends
        drawLine(
            color = color.copy(alpha = 0.7f),
            start = Offset(cx - trackWidth + curvature2, y2),
            end = Offset(cx + trackWidth + curvature2, y2),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawRadarSensors(
    cx: Float,
    cy: Float,
    radar: UltrasonicRadarState,
    pulseAlpha: Float
) {
    fun radarColor(distCm: Int): Color {
        return when {
            distCm <= 40 -> RadarDanger
            distCm <= 80 -> RadarWarning
            else -> RadarSafe
        }
    }

    // Front Radars (Left, C-Left, C-Right, Right)
    val frontY = cy - 105f
    drawRadarArc(cx - 45f, frontY, -110f, radarColor(radar.frontLeft), radar.frontLeft, pulseAlpha)
    drawRadarArc(cx - 15f, frontY - 10f, -95f, radarColor(radar.frontCenterLeft), radar.frontCenterLeft, pulseAlpha)
    drawRadarArc(cx + 15f, frontY - 10f, -85f, radarColor(radar.frontCenterRight), radar.frontCenterRight, pulseAlpha)
    drawRadarArc(cx + 45f, frontY, -70f, radarColor(radar.frontRight), radar.frontRight, pulseAlpha)

    // Rear Radars
    val rearY = cy + 105f
    drawRadarArc(cx - 45f, rearY, 110f, radarColor(radar.rearLeft), radar.rearLeft, pulseAlpha)
    drawRadarArc(cx - 15f, rearY + 10f, 95f, radarColor(radar.rearCenterLeft), radar.rearCenterLeft, pulseAlpha)
    drawRadarArc(cx + 15f, rearY + 10f, 85f, radarColor(radar.rearCenterRight), radar.rearCenterRight, pulseAlpha)
    drawRadarArc(cx + 45f, rearY, 70f, radarColor(radar.rearRight), radar.rearRight, pulseAlpha)
}

private fun DrawScope.drawRadarArc(
    x: Float,
    y: Float,
    angleDeg: Float,
    color: Color,
    distCm: Int,
    pulseAlpha: Float
) {
    val arcRadius = (160 - distCm.coerceIn(20, 150)) * 0.45f
    val isCritical = distCm <= 40
    val alpha = if (isCritical) pulseAlpha else 0.85f

    drawArc(
        color = color.copy(alpha = alpha),
        startAngle = angleDeg - 25f,
        sweepAngle = 50f,
        useCenter = false,
        topLeft = Offset(x - arcRadius, y - arcRadius),
        size = Size(arcRadius * 2f, arcRadius * 2f),
        style = Stroke(width = if (isCritical) 5f else 3f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawTopDownVehicle(
    cx: Float,
    cy: Float,
    paintColor: Color,
    wheelAngleDeg: Float,
    leftDoorOpen: Boolean,
    rightDoorOpen: Boolean,
    turnSignal: TurnSignalState
) {
    val carW = 76f
    val carL = 175f

    // Car shadow
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.6f),
        topLeft = Offset(cx - carW / 2f - 4f, cy - carL / 2f + 4f),
        size = Size(carW + 8f, carL + 4f),
        cornerRadius = CornerRadius(22f, 22f)
    )

    // Wheels (Front Left, Front Right, Rear Left, Rear Right)
    val wheelW = 12f
    val wheelL = 28f
    val frontWheelY = cy - 50f
    val rearWheelY = cy + 50f

    // Front Left Wheel (Steerable)
    rotate(wheelAngleDeg, Offset(cx - carW / 2f, frontWheelY)) {
        drawRoundRect(
            color = Color(0xFF1B2230),
            topLeft = Offset(cx - carW / 2f - wheelW / 2f, frontWheelY - wheelL / 2f),
            size = Size(wheelW, wheelL),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

    // Front Right Wheel (Steerable)
    rotate(wheelAngleDeg, Offset(cx + carW / 2f, frontWheelY)) {
        drawRoundRect(
            color = Color(0xFF1B2230),
            topLeft = Offset(cx + carW / 2f - wheelW / 2f, frontWheelY - wheelL / 2f),
            size = Size(wheelW, wheelL),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

    // Rear Wheels (Fixed)
    drawRoundRect(
        color = Color(0xFF1B2230),
        topLeft = Offset(cx - carW / 2f - wheelW / 2f, rearWheelY - wheelL / 2f),
        size = Size(wheelW, wheelL),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFF1B2230),
        topLeft = Offset(cx + carW / 2f - wheelW / 2f, rearWheelY - wheelL / 2f),
        size = Size(wheelW, wheelL),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Car Body
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                paintColor.copy(alpha = 0.95f),
                paintColor,
                paintColor.copy(alpha = 0.85f)
            )
        ),
        topLeft = Offset(cx - carW / 2f, cy - carL / 2f),
        size = Size(carW, carL),
        cornerRadius = CornerRadius(20f, 20f)
    )

    // Body Outline
    drawRoundRect(
        color = Color.White.copy(alpha = 0.35f),
        topLeft = Offset(cx - carW / 2f, cy - carL / 2f),
        size = Size(carW, carL),
        cornerRadius = CornerRadius(20f, 20f),
        style = Stroke(width = 2f)
    )

    // Roof & Glass (Windshield, Rear Window, Sunroof)
    val glassColor = Color(0xFF0F1722)
    // Windshield
    val windshield = Path().apply {
        moveTo(cx - carW / 2f + 8f, cy - 35f)
        lineTo(cx + carW / 2f - 8f, cy - 35f)
        lineTo(cx + carW / 2f - 14f, cy - 65f)
        lineTo(cx - carW / 2f + 14f, cy - 65f)
        close()
    }
    drawPath(windshield, glassColor)

    // Rear Window
    val rearWindow = Path().apply {
        moveTo(cx - carW / 2f + 10f, cy + 35f)
        lineTo(cx + carW / 2f - 10f, cy + 35f)
        lineTo(cx + carW / 2f - 14f, cy + 60f)
        lineTo(cx - carW / 2f + 14f, cy + 60f)
        close()
    }
    drawPath(rearWindow, glassColor)

    // Sunroof / Panorama glass
    drawRoundRect(
        color = glassColor.copy(alpha = 0.8f),
        topLeft = Offset(cx - carW / 2f + 16f, cy - 25f),
        size = Size(carW - 32f, 50f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Headlights (Cyan glow)
    drawRoundRect(
        color = CyberCyan,
        topLeft = Offset(cx - carW / 2f + 6f, cy - carL / 2f + 2f),
        size = Size(14f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = CyberCyan,
        topLeft = Offset(cx + carW / 2f - 20f, cy - carL / 2f + 2f),
        size = Size(14f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Taillights (Red)
    drawRoundRect(
        color = AlertRed,
        topLeft = Offset(cx - carW / 2f + 6f, cy + carL / 2f - 8f),
        size = Size(16f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = AlertRed,
        topLeft = Offset(cx + carW / 2f - 22f, cy + carL / 2f - 8f),
        size = Size(16f, 6f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Side Mirrors
    val leftMirrorX = cx - carW / 2f - 10f
    val rightMirrorX = cx + carW / 2f + 2f
    val mirrorY = cy - 35f

    drawRoundRect(
        color = paintColor,
        topLeft = Offset(leftMirrorX, mirrorY),
        size = Size(8f, 16f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = paintColor,
        topLeft = Offset(rightMirrorX, mirrorY),
        size = Size(8f, 16f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Blind Spot Warning on side mirrors
    if (turnSignal == TurnSignalState.LEFT || turnSignal == TurnSignalState.HAZARD) {
        drawCircle(
            color = CautionYellow,
            radius = 6f,
            center = Offset(leftMirrorX - 4f, mirrorY + 8f)
        )
    }
    if (turnSignal == TurnSignalState.RIGHT || turnSignal == TurnSignalState.HAZARD) {
        drawCircle(
            color = CautionYellow,
            radius = 6f,
            center = Offset(rightMirrorX + 12f, mirrorY + 8f)
        )
    }

    // Door Open Indications
    if (leftDoorOpen) {
        drawLine(
            color = AlertRed,
            start = Offset(cx - carW / 2f, cy - 20f),
            end = Offset(cx - carW / 2f - 28f, cy - 5f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }
    if (rightDoorOpen) {
        drawLine(
            color = AlertRed,
            start = Offset(cx + carW / 2f, cy - 20f),
            end = Offset(cx + carW / 2f + 28f, cy - 5f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }
}
