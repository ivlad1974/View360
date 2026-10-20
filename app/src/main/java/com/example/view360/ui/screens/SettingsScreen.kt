package com.example.view360.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.model.AVMPreferences
import com.example.view360.ui.theme.AlertRed
import com.example.view360.ui.theme.AutomotiveBorder
import com.example.view360.ui.theme.AutomotiveSurface
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.ui.theme.ElectricBlue
import com.example.view360.viewmodel.AVMUiState
import com.example.view360.viewmodel.AVMViewModel

@Composable
fun SettingsScreen(
    uiState: AVMUiState,
    viewModel: AVMViewModel,
    modifier: Modifier = Modifier
) {
    val prefs = uiState.preferences

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: Trajectory Guidelines & Radar Overlay
        SettingsCard(title = "Guidance & Radar Overlays", icon = Icons.Default.Radar) {
            SettingSwitchRow(
                label = "Dynamic Steering Guidelines (FTrack / BTrack)",
                checked = prefs.dynamicGuidelinesEnabled,
                onCheckedChange = { viewModel.updatePreferences { p -> p.copy(dynamicGuidelinesEnabled = it) } },
                tag = "setting_dynamic_guidelines"
            )

            SettingSwitchRow(
                label = "Fixed Distance Auxiliary Lines (0.5m / 1.5m / 3.0m)",
                checked = prefs.fixedGuidelinesEnabled,
                onCheckedChange = { viewModel.updatePreferences { p -> p.copy(fixedGuidelinesEnabled = it) } },
                tag = "setting_fixed_guidelines"
            )

            SettingSwitchRow(
                label = "16-Zone Ultrasonic Radar Obstacle Overlay",
                checked = prefs.radarOverlayEnabled,
                onCheckedChange = { viewModel.updatePreferences { p -> p.copy(radarOverlayEnabled = it) } },
                tag = "setting_radar_overlay"
            )

            SettingSwitchRow(
                label = "Radar Proximity Audio Beep Alerts",
                checked = prefs.radarAudioAlerts,
                onCheckedChange = { viewModel.updatePreferences { p -> p.copy(radarAudioAlerts = it) } },
                tag = "setting_radar_audio"
            )
        }

        // Section 2: Driver Assistance & ADAS (BSD / DOW)
        SettingsCard(title = "Driver Assistance (ADAS)", icon = Icons.Default.Security) {
            SettingSwitchRow(
                label = "Blind Spot Detection (BSD)",
                checked = prefs.bsdEnabled,
                onCheckedChange = { viewModel.updatePreferences { p -> p.copy(bsdEnabled = it) } },
                tag = "setting_bsd_toggle"
            )

            Column {
                Text(
                    text = "BSD Speed Threshold: ${prefs.bsdSpeedLimitKmh} km/h",
                    color = Color.White.copy(0.8f),
                    fontSize = 12.sp
                )
                Slider(
                    value = prefs.bsdSpeedLimitKmh.toFloat(),
                    onValueChange = { viewModel.updatePreferences { p -> p.copy(bsdSpeedLimitKmh = it.toInt()) } },
                    valueRange = 10f..60f,
                    colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = ElectricBlue, inactiveTrackColor = AutomotiveBorder),
                    modifier = Modifier.height(24.dp).testTag("setting_speed_limit_slider")
                )
            }

            SettingSwitchRow(
                label = "Door Open Warning (DOW)",
                checked = prefs.dowEnabled,
                onCheckedChange = { viewModel.updatePreferences { p -> p.copy(dowEnabled = it) } },
                tag = "setting_dow_toggle"
            )

            SettingSwitchRow(
                label = "Startup 3D Vehicle Panoramic Scan",
                checked = prefs.startupAnimationEnabled,
                onCheckedChange = { viewModel.updatePreferences { p -> p.copy(startupAnimationEnabled = it) } },
                tag = "setting_startup_anim"
            )
        }

        // Section 3: Camera Video Display Adjustments
        SettingsCard(title = "Video & Display Adjustments", icon = Icons.Default.DisplaySettings) {
            SettingSliderRow(
                label = "Brightness (${String.format("%.1f", prefs.brightness)}x)",
                value = prefs.brightness,
                range = 0.5f..1.5f,
                onValueChange = { viewModel.updatePreferences { p -> p.copy(brightness = it) } }
            )

            SettingSliderRow(
                label = "Contrast (${String.format("%.1f", prefs.contrast)}x)",
                value = prefs.contrast,
                range = 0.5f..1.5f,
                onValueChange = { viewModel.updatePreferences { p -> p.copy(contrast = it) } }
            )

            SettingSliderRow(
                label = "Saturation (${String.format("%.1f", prefs.saturation)}x)",
                value = prefs.saturation,
                range = 0.5f..1.5f,
                onValueChange = { viewModel.updatePreferences { p -> p.copy(saturation = it) } }
            )
        }

        // Section 4: System Information & Reset
        SettingsCard(title = "System Info", icon = Icons.Default.Info) {
            Text("Software Version: View 360 v2.22.031", color = Color.White, fontSize = 12.sp)
            Text("Architecture: 4-Channel Panoramic AVM + BSD/DOW + DVR", color = CyberCyan, fontSize = 11.sp)
            Text("Calibration Config: /custom/calibinfo.lua (Active)", color = Color.White.copy(0.7f), fontSize = 11.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.updatePreferences { AVMPreferences() }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed.copy(0.8f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("reset_factory_settings")
            ) {
                Text("Restore Factory Settings", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SettingsCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, AutomotiveBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, tag: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White.copy(0.85f), fontSize = 12.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = ElectricBlue),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun SettingSliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
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
