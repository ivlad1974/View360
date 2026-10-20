package com.example.view360.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.model.CameraCalibOffset
import com.example.view360.ui.theme.AlertRed
import com.example.view360.ui.theme.AutomotiveBackground
import com.example.view360.ui.theme.AutomotiveBorder
import com.example.view360.ui.theme.AutomotiveSurface
import com.example.view360.ui.theme.AutomotiveSurfaceVariant
import com.example.view360.ui.theme.CautionYellow
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.ui.theme.ElectricBlue
import com.example.view360.ui.theme.SafeGreen
import com.example.view360.viewmodel.AVMUiState
import com.example.view360.viewmodel.AVMViewModel

@Composable
fun CalibrationScreen(
    uiState: AVMUiState,
    viewModel: AVMViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCameraId by remember { mutableIntStateOf(1) } // 1: Front, 2: Right, 3: Rear, 4: Left
    val activeCalib = uiState.cameraCalibs[selectedCameraId] ?: CameraCalibOffset(selectedCameraId, "Camera")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("calibration_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: Calibration Visualizer Canvas (Checkerboard & Crosshairs)
        Card(
            colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AutomotiveBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calibration Grid & Stitching Seams",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${activeCalib.cameraName} (ID: $selectedCameraId)",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(AutomotiveBackground, RoundedCornerShape(10.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                        // Draw Vehicle footprint center
                        drawRoundRect(
                            color = Color(0xFF1E2838),
                            topLeft = Offset(cx - 30f, cy - 50f),
                            size = Size(60f, 100f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                        )

                        // 8 Calibration Checkerboard target markers around car
                        val targets = listOf(
                            Offset(cx - 90f, cy - 75f), Offset(cx + 90f, cy - 75f), // Front
                            Offset(cx - 110f, cy - 20f), Offset(cx + 110f, cy - 20f), // Side front
                            Offset(cx - 110f, cy + 20f), Offset(cx + 110f, cy + 20f), // Side rear
                            Offset(cx - 90f, cy + 75f), Offset(cx + 90f, cy + 75f)  // Rear
                        )

                        targets.forEach { pos ->
                            drawRect(CautionYellow, topLeft = Offset(pos.x - 8f, pos.y - 8f), size = Size(16f, 16f), style = Stroke(2f))
                            drawRect(CautionYellow, topLeft = Offset(pos.x - 8f, pos.y - 8f), size = Size(8f, 8f))
                            drawRect(CautionYellow, topLeft = Offset(pos.x, pos.y), size = Size(8f, 8f))
                        }

                        // Diagonal Seam Blending lines
                        drawLine(CyberCyan.copy(0.4f), Offset(cx, cy), Offset(0f, 0f), strokeWidth = 2f, pathEffect = dash)
                        drawLine(CyberCyan.copy(0.4f), Offset(cx, cy), Offset(size.width, 0f), strokeWidth = 2f, pathEffect = dash)
                        drawLine(CyberCyan.copy(0.4f), Offset(cx, cy), Offset(0f, size.height), strokeWidth = 2f, pathEffect = dash)
                        drawLine(CyberCyan.copy(0.4f), Offset(cx, cy), Offset(size.width, size.height), strokeWidth = 2f, pathEffect = dash)

                        // Camera Calibration Crosshair with active offsets
                        val crossX = cx + activeCalib.offsetX * 1.5f
                        val crossY = when (selectedCameraId) {
                            1 -> cy - 65f + activeCalib.offsetY * 1.5f
                            2 -> cy + activeCalib.offsetY * 1.5f
                            3 -> cy + 65f + activeCalib.offsetY * 1.5f
                            else -> cy + activeCalib.offsetY * 1.5f
                        }

                        drawLine(AlertRed, Offset(crossX - 16f, crossY), Offset(crossX + 16f, crossY), strokeWidth = 2f)
                        drawLine(AlertRed, Offset(crossX, crossY - 16f), Offset(crossX, crossY + 16f), strokeWidth = 2f)
                        drawCircle(AlertRed, radius = 10f, center = Offset(crossX, crossY), style = Stroke(1.5f))
                    }
                }
            }
        }

        // Section 2: Camera Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                1 to "Front (CH1)",
                2 to "Right (CH2)",
                3 to "Rear (CH3)",
                4 to "Left (CH4)"
            ).forEach { (id, label) ->
                val isSelected = selectedCameraId == id
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCameraId = id },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = AutomotiveSurface,
                        labelColor = Color.White
                    ),
                    modifier = Modifier.testTag("calib_tab_cam_$id")
                )
            }
        }

        // Section 3: Fine Alignment Nudges
        Card(
            colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AutomotiveBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Offset Adjustments (X: ${activeCalib.offsetX.toInt()}px, Y: ${activeCalib.offsetY.toInt()}px)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                // Nudge Buttons: Left/Right (X) and Up/Down (Y)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("X Offset:", color = CyberCyan, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        NudgeButton("-10") { viewModel.updateCalib(selectedCameraId, -10f, 0f, 0f, 0f, 0f) }
                        NudgeButton("-1") { viewModel.updateCalib(selectedCameraId, -1f, 0f, 0f, 0f, 0f) }
                        NudgeButton("+1") { viewModel.updateCalib(selectedCameraId, 1f, 0f, 0f, 0f, 0f) }
                        NudgeButton("+10") { viewModel.updateCalib(selectedCameraId, 10f, 0f, 0f, 0f, 0f) }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Y Offset:", color = CyberCyan, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        NudgeButton("-10") { viewModel.updateCalib(selectedCameraId, 0f, -10f, 0f, 0f, 0f) }
                        NudgeButton("-1") { viewModel.updateCalib(selectedCameraId, 0f, -1f, 0f, 0f, 0f) }
                        NudgeButton("+1") { viewModel.updateCalib(selectedCameraId, 0f, 1f, 0f, 0f, 0f) }
                        NudgeButton("+10") { viewModel.updateCalib(selectedCameraId, 0f, 10f, 0f, 0f, 0f) }
                    }
                }

                // Sliders: Pitch, Yaw, FOV
                SliderRow("Pitch Angle: ${activeCalib.pitchDeg.toInt()}°", activeCalib.pitchDeg, -30f..30f) {
                    viewModel.updateCalib(selectedCameraId, 0f, 0f, it - activeCalib.pitchDeg, 0f, 0f)
                }

                SliderRow("Yaw Angle: ${activeCalib.yawDeg.toInt()}°", activeCalib.yawDeg, -40f..40f) {
                    viewModel.updateCalib(selectedCameraId, 0f, 0f, 0f, it - activeCalib.yawDeg, 0f)
                }

                SliderRow("FOV Scale: ${String.format("%.2f", activeCalib.fovScale)}x", activeCalib.fovScale, 0.7f..1.5f) {
                    viewModel.updateCalib(selectedCameraId, 0f, 0f, 0f, 0f, it - activeCalib.fovScale)
                }
            }
        }

        // Section 4: Clean Leak & Seam Blending (CleanLeakActivity)
        Card(
            colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AutomotiveBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Clean Leak & Overlap Seam Tuning", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                SliderRow("Seam Blending Width: ${String.format("%.1f", uiState.seamConfig.blendWidth)}x", uiState.seamConfig.blendWidth, 0.5f..2.5f) {
                    viewModel.updateSeamConfig(uiState.seamConfig.copy(blendWidth = it))
                }

                SliderRow("Blind Spot Margin: ${uiState.seamConfig.cleanLeakMargin.toInt()} px", uiState.seamConfig.cleanLeakMargin, -20f..20f) {
                    viewModel.updateSeamConfig(uiState.seamConfig.copy(cleanLeakMargin = it))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mesh Balance Optimization", color = Color.White.copy(0.8f), fontSize = 12.sp)
                    Switch(
                        checked = uiState.seamConfig.meshBalance,
                        onCheckedChange = { viewModel.updateSeamConfig(uiState.seamConfig.copy(meshBalance = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = ElectricBlue)
                    )
                }
            }
        }

        // Section 5: Calibration Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.updateCalib(selectedCameraId, 0f, 0f, 0f, 0f, 0f)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                modifier = Modifier.weight(1f).testTag("save_calib_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Calib", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = { viewModel.resetCalib(selectedCameraId) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                modifier = Modifier.weight(1f).testTag("reset_calib_button")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset Cam", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun NudgeButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = AutomotiveSurfaceVariant),
        shape = RoundedCornerShape(6.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column {
        Text(text = label, color = Color.White.copy(0.8f), fontSize = 11.sp)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = ElectricBlue, inactiveTrackColor = AutomotiveBorder),
            modifier = Modifier.height(24.dp)
        )
    }
}
