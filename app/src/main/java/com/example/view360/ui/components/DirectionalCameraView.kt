package com.example.view360.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.model.AVMPreferences
import com.example.view360.model.CameraViewMode
import com.example.view360.model.CarModelItem
import com.example.view360.model.CarPaint
import com.example.view360.model.TurnSignalState
import com.example.view360.model.UltrasonicRadarState
import com.example.view360.model.VehicleGear
import com.example.view360.ui.theme.AlertRed
import com.example.view360.ui.theme.AutomotiveBackground
import com.example.view360.ui.theme.CautionYellow
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.ui.theme.SafeGreen

@Composable
fun DirectionalCameraView(
    mode: CameraViewMode,
    gear: VehicleGear,
    steeringAngleDeg: Float,
    speedKmh: Float,
    turnSignal: TurnSignalState,
    carModel: CarModelItem,
    paint: CarPaint,
    radarState: UltrasonicRadarState,
    preferences: AVMPreferences,
    orbitYaw: Float,
    orbitPitch: Float,
    onOrbitChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(AutomotiveBackground, RoundedCornerShape(12.dp))
    ) {
        when (mode) {
            CameraViewMode.FRONT -> FrontCameraFeed(steeringAngleDeg, speedKmh, preferences)
            CameraViewMode.REAR -> RearCameraFeed(steeringAngleDeg, radarState, preferences)
            CameraViewMode.LEFT -> SideCameraFeed(isLeft = true, turnSignal, preferences)
            CameraViewMode.RIGHT -> SideCameraFeed(isLeft = false, turnSignal, preferences)
            CameraViewMode.ORBIT_3D -> Orbit3DView(carModel, paint, orbitYaw, orbitPitch, onOrbitChange)
            CameraViewMode.QUAD_SPLIT -> QuadSplitCameraView()
        }

        // Camera Info HUD Badge
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(SafeGreen, RoundedCornerShape(4.dp))
            )
            Text(
                text = "${mode.label.uppercase()} CAM",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "1080P@30FPS",
                color = CyberCyan,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun FrontCameraFeed(
    steeringAngleDeg: Float,
    speedKmh: Float,
    preferences: AVMPreferences
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Road & Sky Simulation
        val horizonY = h * 0.42f
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF0D1524), Color(0xFF16253B))),
            topLeft = Offset.Zero,
            size = Size(w, horizonY)
        )
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF182230), Color(0xFF0F1520))),
            topLeft = Offset(0f, horizonY),
            size = Size(w, h - horizonY)
        )

        // Road perspective lines
        val laneDash = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
        val cx = w / 2f
        drawLine(Color.White.copy(0.4f), Offset(cx, horizonY), Offset(cx - w * 0.45f, h), strokeWidth = 3f)
        drawLine(Color.White.copy(0.4f), Offset(cx, horizonY), Offset(cx + w * 0.45f, h), strokeWidth = 3f)
        drawLine(CautionYellow.copy(0.7f), Offset(cx, horizonY), Offset(cx, h), strokeWidth = 2.5f, pathEffect = laneDash)

        // Dynamic Front Trajectory
        if (preferences.dynamicGuidelinesEnabled) {
            val curve = (steeringAngleDeg / 540f) * (w * 0.22f)
            val trackW = w * 0.28f

            // Safe, warning, danger distance bars
            drawLine(SafeGreen, Offset(cx - trackW * 0.6f + curve * 0.5f, h * 0.62f), Offset(cx + trackW * 0.6f + curve * 0.5f, h * 0.62f), strokeWidth = 3f)
            drawLine(CautionYellow, Offset(cx - trackW * 0.75f + curve * 0.8f, h * 0.76f), Offset(cx + trackW * 0.75f + curve * 0.8f, h * 0.76f), strokeWidth = 3.5f)
            drawLine(AlertRed, Offset(cx - trackW * 0.9f + curve, h * 0.90f), Offset(cx + trackW * 0.9f + curve, h * 0.90f), strokeWidth = 4f)

            // Dynamic curving side rails
            val leftRail = Path().apply {
                moveTo(cx - trackW * 0.5f + curve * 0.3f, horizonY + 20f)
                cubicTo(
                    cx - trackW * 0.6f + curve * 0.5f, h * 0.62f,
                    cx - trackW * 0.8f + curve * 0.8f, h * 0.78f,
                    cx - trackW + curve, h
                )
            }
            drawPath(leftRail, CyberCyan.copy(0.85f), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

            val rightRail = Path().apply {
                moveTo(cx + trackW * 0.5f + curve * 0.3f, horizonY + 20f)
                cubicTo(
                    cx + trackW * 0.6f + curve * 0.5f, h * 0.62f,
                    cx + trackW * 0.8f + curve * 0.8f, h * 0.78f,
                    cx + trackW + curve, h
                )
            }
            drawPath(rightRail, CyberCyan.copy(0.85f), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
        }

        // Car Bumper HUD Arc
        drawArc(
            color = Color(0xFF1E2838),
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.4f, h - 35f),
            size = Size(w * 0.8f, 70f),
            style = Stroke(width = 6f)
        )
    }
}

@Composable
private fun RearCameraFeed(
    steeringAngleDeg: Float,
    radarState: UltrasonicRadarState,
    preferences: AVMPreferences
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val horizonY = h * 0.38f

            // Parking Ground Perspective
            drawRect(
                brush = Brush.verticalGradient(listOf(Color(0xFF101926), Color(0xFF1A2638))),
                topLeft = Offset.Zero,
                size = Size(w, horizonY)
            )
            drawRect(
                brush = Brush.verticalGradient(listOf(Color(0xFF1F2B3E), Color(0xFF0F1520))),
                topLeft = Offset(0f, horizonY),
                size = Size(w, h - horizonY)
            )

            // Dynamic Reverse Parking Rails
            val curve = -(steeringAngleDeg / 540f) * (w * 0.22f)
            val trackW = w * 0.26f

            // Distance Trajectory markers (0.5m Red, 1.5m Yellow, 3.0m Green)
            drawLine(SafeGreen, Offset(cx - trackW * 0.6f + curve * 0.4f, h * 0.58f), Offset(cx + trackW * 0.6f + curve * 0.4f, h * 0.58f), strokeWidth = 3f)
            drawLine(CautionYellow, Offset(cx - trackW * 0.75f + curve * 0.7f, h * 0.72f), Offset(cx + trackW * 0.75f + curve * 0.7f, h * 0.72f), strokeWidth = 3.5f)
            drawLine(AlertRed, Offset(cx - trackW * 0.9f + curve, h * 0.88f), Offset(cx + trackW * 0.9f + curve, h * 0.88f), strokeWidth = 4.5f)

            // Reversing Rails
            val leftRail = Path().apply {
                moveTo(cx - trackW * 0.5f + curve * 0.2f, horizonY + 15f)
                cubicTo(
                    cx - trackW * 0.6f + curve * 0.4f, h * 0.58f,
                    cx - trackW * 0.8f + curve * 0.7f, h * 0.74f,
                    cx - trackW + curve, h
                )
            }
            drawPath(leftRail, CautionYellow.copy(0.9f), style = Stroke(width = 4f, cap = StrokeCap.Round))

            val rightRail = Path().apply {
                moveTo(cx + trackW * 0.5f + curve * 0.2f, horizonY + 15f)
                cubicTo(
                    cx + trackW * 0.6f + curve * 0.4f, h * 0.58f,
                    cx + trackW * 0.8f + curve * 0.7f, h * 0.74f,
                    cx + trackW + curve, h
                )
            }
            drawPath(rightRail, CautionYellow.copy(0.9f), style = Stroke(width = 4f, cap = StrokeCap.Round))

            // Rear bumper edge
            drawArc(
                color = Color(0xFF1E2838),
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(cx - w * 0.4f, h - 35f),
                size = Size(w * 0.8f, 70f),
                style = Stroke(width = 6f)
            )
        }

        // Proximity alert banner if object is close in reverse
        val minRearDist = minOf(radarState.rearCenterLeft, radarState.rearCenterRight)
        if (minRearDist <= 50) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .background(AlertRed.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Obstacle Alert", tint = Color.White, modifier = Modifier.size(18.dp))
                Text(
                    text = "OBSTACLE REAR: ${minRearDist} cm - STOP!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SideCameraFeed(
    isLeft: Boolean,
    turnSignal: TurnSignalState,
    preferences: AVMPreferences
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Side pavement and road perspective
            drawRect(
                brush = Brush.horizontalGradient(
                    if (isLeft) listOf(Color(0xFF0F1520), Color(0xFF1B273A))
                    else listOf(Color(0xFF1B273A), Color(0xFF0F1520))
                )
            )

            // Curb guide line (helps driver parallel park without scratching rims!)
            val curbX1 = if (isLeft) w * 0.35f else w * 0.65f
            val curbX2 = if (isLeft) w * 0.15f else w * 0.85f
            drawLine(
                color = CautionYellow,
                start = Offset(curbX1, 0f),
                end = Offset(curbX2, h),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Wheel blind spot distance ticks
            for (i in 1..4) {
                val y = h * (i * 0.2f)
                val x = if (isLeft) w * (0.35f - i * 0.05f) else w * (0.65f + i * 0.05f)
                drawLine(
                    color = CyberCyan.copy(alpha = 0.8f),
                    start = Offset(x - 20f, y),
                    end = Offset(x + 20f, y),
                    strokeWidth = 2f
                )
            }
        }

        // Blind Spot Warning Banner when turning
        val isWarning = (isLeft && turnSignal == TurnSignalState.LEFT) || (!isLeft && turnSignal == TurnSignalState.RIGHT)
        if (isWarning && preferences.bsdEnabled) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(CautionYellow.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = "BSD Warning", tint = Color.Black)
                Text(
                    text = "BLIND SPOT MONITOR ACTIVE",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun Orbit3DView(
    carModel: CarModelItem,
    paint: CarPaint,
    orbitYaw: Float,
    orbitPitch: Float,
    onOrbitChange: (Float, Float) -> Unit
) {
    val context = LocalContext.current
    val carBitmap = remember(carModel.assetPath) {
        try {
            val inputStream = context.assets.open(carModel.assetPath)
            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newYaw = orbitYaw + dragAmount.x * 0.5f
                    val newPitch = (orbitPitch - dragAmount.y * 0.3f).coerceIn(15f, 75f)
                    onOrbitChange(newYaw, newPitch)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Draw circular 3D platform with cyber grid
            val platformRadius = size.width * 0.38f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyberCyan.copy(0.12f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = platformRadius
                ),
                radius = platformRadius,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = CyberCyan.copy(0.4f),
                radius = platformRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 2f)
            )

            // Radial azimuth degree ticks
            for (angle in 0 until 360 step 30) {
                rotate(angle.toFloat(), Offset(cx, cy)) {
                    drawLine(
                        color = CyberCyan.copy(0.25f),
                        start = Offset(cx, cy - platformRadius + 12f),
                        end = Offset(cx, cy - platformRadius),
                        strokeWidth = 2f
                    )
                }
            }

            // Ground shadow
            drawOval(
                color = Color.Black.copy(0.7f),
                topLeft = Offset(cx - 100f, cy + 30f),
                size = Size(200f, 60f)
            )
        }

        // Render Car Model Image (or fallback)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (carBitmap != null) {
                Image(
                    bitmap = carBitmap,
                    contentDescription = carModel.name,
                    modifier = Modifier.size(240.dp)
                )
            } else {
                Text(
                    text = carModel.name,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        // Hint HUD
        Text(
            text = "Drag to Rotate 360° | Yaw: ${orbitYaw.toInt()}° Pitch: ${orbitPitch.toInt()}°",
            color = Color.White.copy(0.6f),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun QuadSplitCameraView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            SingleChannelFeed("FRONT", Color(0xFF0F2027), Modifier.weight(1f))
            SingleChannelFeed("REAR", Color(0xFF203A43), Modifier.weight(1f))
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            SingleChannelFeed("LEFT", Color(0xFF2C5364), Modifier.weight(1f))
            SingleChannelFeed("RIGHT", Color(0xFF141E30), Modifier.weight(1f))
        }
    }
}

@Composable
private fun SingleChannelFeed(name: String, bg: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(1.dp)
            .background(bg)
            .padding(6.dp)
    ) {
        Text(
            text = name,
            color = CyberCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = "REC ●",
            color = AlertRed,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}
