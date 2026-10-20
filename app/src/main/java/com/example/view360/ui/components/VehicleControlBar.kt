package com.example.view360.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import com.example.view360.model.TurnSignalState
import com.example.view360.model.VehicleGear
import com.example.view360.ui.theme.AlertRed
import com.example.view360.ui.theme.AutomotiveBorder
import com.example.view360.ui.theme.AutomotiveSurface
import com.example.view360.ui.theme.AutomotiveSurfaceVariant
import com.example.view360.ui.theme.CautionYellow
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.ui.theme.ElectricBlue
import com.example.view360.ui.theme.SafeGreen

@Composable
fun VehicleControlBar(
    currentGear: VehicleGear,
    onGearChange: (VehicleGear) -> Unit,
    turnSignal: TurnSignalState,
    onTurnSignalChange: (TurnSignalState) -> Unit,
    steeringAngleDeg: Float,
    onSteeringAngleChange: (Float) -> Unit,
    speedKmh: Float,
    onSpeedChange: (Float) -> Unit,
    leftDoorOpen: Boolean,
    onToggleLeftDoor: () -> Unit,
    rightDoorOpen: Boolean,
    onToggleRightDoor: () -> Unit,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onTakeSnapshot: () -> Unit,
    onEmergencyLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vehicle_control_bar"),
        color = AutomotiveSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AutomotiveBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Gear Selector (P-R-N-D) & Turn Signals & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gear Selector (P, R, N, D)
                Row(
                    modifier = Modifier
                        .background(AutomotiveSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VehicleGear.values().forEach { gear ->
                        val isSelected = currentGear == gear
                        val bg = when {
                            isSelected && gear == VehicleGear.REVERSE -> AlertRed
                            isSelected && gear == VehicleGear.DRIVE -> SafeGreen
                            isSelected -> CyberCyan
                            else -> Color.Transparent
                        }
                        val textColor = if (isSelected) Color.Black else Color.White

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(bg)
                                .clickable { onGearChange(gear) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("gear_${gear.symbol.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = gear.symbol,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Turn Signals: Left, Hazard, Right
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Signal
                    FilledTonalButton(
                        onClick = {
                            onTurnSignalChange(if (turnSignal == TurnSignalState.LEFT) TurnSignalState.OFF else TurnSignalState.LEFT)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (turnSignal == TurnSignalState.LEFT) CautionYellow else AutomotiveSurfaceVariant
                        ),
                        modifier = Modifier.testTag("turn_signal_left")
                    ) {
                        Text("◀ L", color = if (turnSignal == TurnSignalState.LEFT) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Hazard Signal
                    FilledTonalButton(
                        onClick = {
                            onTurnSignalChange(if (turnSignal == TurnSignalState.HAZARD) TurnSignalState.OFF else TurnSignalState.HAZARD)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (turnSignal == TurnSignalState.HAZARD) AlertRed else AutomotiveSurfaceVariant
                        ),
                        modifier = Modifier.testTag("turn_signal_hazard")
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Hazard Warning", tint = if (turnSignal == TurnSignalState.HAZARD) Color.White else AlertRed, modifier = Modifier.size(16.dp))
                    }

                    // Right Signal
                    FilledTonalButton(
                        onClick = {
                            onTurnSignalChange(if (turnSignal == TurnSignalState.RIGHT) TurnSignalState.OFF else TurnSignalState.RIGHT)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (turnSignal == TurnSignalState.RIGHT) CautionYellow else AutomotiveSurfaceVariant
                        ),
                        modifier = Modifier.testTag("turn_signal_right")
                    ) {
                        Text("R ▶", color = if (turnSignal == TurnSignalState.RIGHT) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Quick Tools: Radar Sound, Snapshot, Lock
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .background(AutomotiveSurfaceVariant, CircleShape)
                            .size(36.dp)
                            .testTag("radar_mute_button")
                    ) {
                        Icon(
                            if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Radar Sound",
                            tint = if (isMuted) AlertRed else CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onTakeSnapshot,
                        modifier = Modifier
                            .background(AutomotiveSurfaceVariant, CircleShape)
                            .size(36.dp)
                            .testTag("dvr_snapshot_button")
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Snapshot",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onEmergencyLock,
                        modifier = Modifier
                            .background(AutomotiveSurfaceVariant, CircleShape)
                            .size(36.dp)
                            .testTag("dvr_lock_button")
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Lock Clip",
                            tint = AlertRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Row 2: Steering Angle & Speed Interactive Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Steer Angle Slider
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Steering: ${steeringAngleDeg.toInt()}°",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Reset (0°)",
                            color = Color.White.copy(0.7f),
                            fontSize = 10.sp,
                            modifier = Modifier.clickable { onSteeringAngleChange(0f) }
                        )
                    }
                    Slider(
                        value = steeringAngleDeg,
                        onValueChange = onSteeringAngleChange,
                        valueRange = -540f..540f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = ElectricBlue,
                            inactiveTrackColor = AutomotiveBorder
                        ),
                        modifier = Modifier.height(26.dp).testTag("steering_slider")
                    )
                }

                // Speed Slider
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Speed: ${speedKmh.toInt()} km/h",
                            color = if (speedKmh > 30f) CautionYellow else SafeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Stop",
                                color = AlertRed,
                                fontSize = 10.sp,
                                modifier = Modifier.clickable { onSpeedChange(0f) }
                            )
                            Text(
                                text = "Park (15)",
                                color = SafeGreen,
                                fontSize = 10.sp,
                                modifier = Modifier.clickable { onSpeedChange(15f) }
                            )
                        }
                    }
                    Slider(
                        value = speedKmh,
                        onValueChange = onSpeedChange,
                        valueRange = 0f..120f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (speedKmh > 30f) CautionYellow else SafeGreen,
                            activeTrackColor = if (speedKmh > 30f) CautionYellow else SafeGreen,
                            inactiveTrackColor = AutomotiveBorder
                        ),
                        modifier = Modifier.height(26.dp).testTag("speed_slider")
                    )
                }

                // Door Toggles
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DoorToggleButton(
                        label = "L Door",
                        isOpen = leftDoorOpen,
                        onClick = onToggleLeftDoor,
                        tag = "toggle_left_door"
                    )
                    DoorToggleButton(
                        label = "R Door",
                        isOpen = rightDoorOpen,
                        onClick = onToggleRightDoor,
                        tag = "toggle_right_door"
                    )
                }
            }
        }
    }
}

@Composable
private fun DoorToggleButton(
    label: String,
    isOpen: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isOpen) AlertRed.copy(0.2f) else AutomotiveSurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(tag)
    ) {
        Text(
            text = if (isOpen) "$label (OPEN)" else label,
            color = if (isOpen) AlertRed else Color.White.copy(0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
