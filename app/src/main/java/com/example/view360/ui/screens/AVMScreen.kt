package com.example.view360.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.model.CameraViewMode
import com.example.view360.ui.components.BirdviewCanvas
import com.example.view360.ui.components.DirectionalCameraView
import com.example.view360.ui.components.VehicleControlBar
import com.example.view360.ui.theme.AutomotiveBorder
import com.example.view360.ui.theme.AutomotiveSurface
import com.example.view360.ui.theme.CautionYellow
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.viewmodel.AVMUiState
import com.example.view360.viewmodel.AVMViewModel

@Composable
fun AVMScreen(
    uiState: AVMUiState,
    viewModel: AVMViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top: Camera Mode Selector Tabs
        CameraModeTabBar(
            selectedMode = uiState.cameraViewMode,
            onModeSelect = { viewModel.setCameraViewMode(it) }
        )

        // Middle: Dual-Pane AVM Display (360 Overhead on left, Directional Cam on right)
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val isWide = maxWidth >= 600.dp
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left Pane: 360 Birdview (40% width)
                    BirdviewCanvas(
                        carModel = uiState.currentCarModel,
                        paint = uiState.currentPaint,
                        licensePlate = uiState.licensePlate,
                        gear = uiState.currentGear,
                        steeringAngleDeg = uiState.steeringAngleDeg,
                        turnSignal = uiState.turnSignal,
                        radarState = uiState.radarState,
                        preferences = uiState.preferences,
                        leftDoorOpen = uiState.leftDoorOpen,
                        rightDoorOpen = uiState.rightDoorOpen,
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight()
                    )

                    // Right Pane: Directional Camera Feed (58% width)
                    DirectionalCameraView(
                        mode = uiState.cameraViewMode,
                        gear = uiState.currentGear,
                        steeringAngleDeg = uiState.steeringAngleDeg,
                        speedKmh = uiState.speedKmh,
                        turnSignal = uiState.turnSignal,
                        carModel = uiState.currentCarModel,
                        paint = uiState.currentPaint,
                        radarState = uiState.radarState,
                        preferences = uiState.preferences,
                        orbitYaw = uiState.orbitYaw,
                        orbitPitch = uiState.orbitPitch,
                        onOrbitChange = { yaw, pitch -> viewModel.setOrbitAngles(yaw, pitch) },
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight()
                    )
                }
            } else {
                // Stack vertically for compact screens
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BirdviewCanvas(
                        carModel = uiState.currentCarModel,
                        paint = uiState.currentPaint,
                        licensePlate = uiState.licensePlate,
                        gear = uiState.currentGear,
                        steeringAngleDeg = uiState.steeringAngleDeg,
                        turnSignal = uiState.turnSignal,
                        radarState = uiState.radarState,
                        preferences = uiState.preferences,
                        leftDoorOpen = uiState.leftDoorOpen,
                        rightDoorOpen = uiState.rightDoorOpen,
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxWidth()
                    )

                    DirectionalCameraView(
                        mode = uiState.cameraViewMode,
                        gear = uiState.currentGear,
                        steeringAngleDeg = uiState.steeringAngleDeg,
                        speedKmh = uiState.speedKmh,
                        turnSignal = uiState.turnSignal,
                        carModel = uiState.currentCarModel,
                        paint = uiState.currentPaint,
                        radarState = uiState.radarState,
                        preferences = uiState.preferences,
                        orbitYaw = uiState.orbitYaw,
                        orbitPitch = uiState.orbitPitch,
                        onOrbitChange = { yaw, pitch -> viewModel.setOrbitAngles(yaw, pitch) },
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxWidth()
                    )
                }
            }

            // High Speed Safety Overlay (if speed exceeds threshold 30 km/h)
            if (uiState.speedKmh > uiState.preferences.bsdSpeedLimitKmh) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(AutomotiveSurface, RoundedCornerShape(12.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "SPEED > ${uiState.preferences.bsdSpeedLimitKmh} KM/H",
                            color = CautionYellow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AVM Birdview Standby (Driving Mode Active)",
                            color = Color.White.copy(0.8f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Bottom: Vehicle Telemetry Control Bar
        VehicleControlBar(
            currentGear = uiState.currentGear,
            onGearChange = { viewModel.setGear(it) },
            turnSignal = uiState.turnSignal,
            onTurnSignalChange = { viewModel.setTurnSignal(it) },
            steeringAngleDeg = uiState.steeringAngleDeg,
            onSteeringAngleChange = { viewModel.setSteeringAngle(it) },
            speedKmh = uiState.speedKmh,
            onSpeedChange = { viewModel.setVehicleSpeed(it) },
            leftDoorOpen = uiState.leftDoorOpen,
            onToggleLeftDoor = { viewModel.toggleLeftDoor() },
            rightDoorOpen = uiState.rightDoorOpen,
            onToggleRightDoor = { viewModel.toggleRightDoor() },
            isMuted = uiState.radarState.isMuted,
            onToggleMute = { viewModel.toggleRadarMute() },
            onTakeSnapshot = { viewModel.takeSnapshot() },
            onEmergencyLock = { viewModel.toggleEmergencyLock() }
        )
    }
}

@Composable
private fun CameraModeTabBar(
    selectedMode: CameraViewMode,
    onModeSelect: (CameraViewMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AutomotiveSurface, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CameraViewMode.values().forEach { mode ->
            val isSelected = selectedMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) CyberCyan else Color.Transparent)
                    .clickable { onModeSelect(mode) }
                    .padding(vertical = 8.dp)
                    .testTag("tab_camera_${mode.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.label,
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}
