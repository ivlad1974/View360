package com.example.view360.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.model.DvrRecordItem
import com.example.view360.model.DvrRecordType
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
fun DVRScreen(
    uiState: AVMUiState,
    viewModel: AVMViewModel,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showFormatDialog by remember { mutableStateOf(false) }
    var selectedVideoForPlay by remember { mutableStateOf<DvrRecordItem?>(null) }

    val filteredRecords = remember(selectedFilter, uiState.dvrRecords) {
        when (selectedFilter) {
            "LOCKED" -> uiState.dvrRecords.filter { it.isLocked || it.recordType == DvrRecordType.EVENT_LOCK }
            "LOOP" -> uiState.dvrRecords.filter { it.recordType == DvrRecordType.NORMAL }
            "PARK" -> uiState.dvrRecords.filter { it.recordType == DvrRecordType.PARKING }
            else -> uiState.dvrRecords
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("dvr_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: 4-Channel Live Recording Status Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AutomotiveBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FiberManualRecord,
                                contentDescription = "Recording",
                                tint = if (uiState.dvrRecording) AlertRed else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.dvrRecording) "4-CH SYNC RECORDING ACTIVE" else "DVR PAUSED",
                                color = if (uiState.dvrRecording) AlertRed else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleDvrRecording() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.dvrRecording) AutomotiveSurfaceVariant else SafeGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_dvr_record_button")
                        ) {
                            Text(if (uiState.dvrRecording) "Pause REC" else "Start REC", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel resolution chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DvrChannelBadge("CH1 FRONT", "1080P@30")
                        DvrChannelBadge("CH2 REAR", "1080P@30")
                        DvrChannelBadge("CH3 LEFT", "720P@30")
                        DvrChannelBadge("CH4 RIGHT", "720P@30")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Storage Usage
                    Text(
                        text = "SD Card Storage: 48.2 GB / 64.0 GB Used (Loop Overwrite ON)",
                        color = Color.White.copy(0.7f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = ElectricBlue,
                        trackColor = AutomotiveBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showFormatDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = AlertRed),
                            modifier = Modifier.testTag("format_storage_button")
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Format SD Card", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 2: Filter Tabs (All, Locked, Loop, Parking, Snapshots)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All (${uiState.dvrRecords.size})",
                    "LOCKED" to "Emergency",
                    "LOOP" to "Normal",
                    "PARK" to "Parking",
                    "SNAPS" to "Snaps (${uiState.dvrSnapshots.size})"
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = AutomotiveSurface,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.testTag("dvr_filter_${key.lowercase()}")
                    )
                }
            }
        }

        // Section 3: Records List or Snapshots
        if (selectedFilter == "SNAPS") {
            items(uiState.dvrSnapshots) { snap ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AutomotiveBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(AutomotiveSurfaceVariant, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyberCyan)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(snap.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${snap.timestamp} • ${snap.cameraChannel}", color = Color.White.copy(0.6f), fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredRecords) { record ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        width = if (record.isLocked) 1.5.dp else 1.dp,
                        color = if (record.isLocked) AlertRed.copy(0.6f) else AutomotiveBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedVideoForPlay = record }
                        .testTag("dvr_record_${record.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(AutomotiveSurfaceVariant, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = CyberCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = record.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (record.isLocked) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = AlertRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${record.timestamp} • ${record.durationSec}s • ${record.cameraChannel}",
                                color = Color.White.copy(0.6f),
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleEmergencyLock() },
                            modifier = Modifier.testTag("lock_toggle_${record.id}")
                        ) {
                            Icon(
                                if (record.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Toggle Lock",
                                tint = if (record.isLocked) AlertRed else Color.White.copy(0.6f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Video Player Simulation Modal
    if (selectedVideoForPlay != null) {
        val record = selectedVideoForPlay!!
        AlertDialog(
            onDismissRequest = { selectedVideoForPlay = null },
            title = {
                Text(
                    text = "DVR Player: ${record.title}",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("4-Channel Playback Preview\n${record.timestamp}", color = CyberCyan, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Duration: ${record.durationSec} sec", color = Color.White.copy(0.7f), fontSize = 11.sp)
                        Text(if (record.isLocked) "EMERGENCY PROTECTED" else "NORMAL LOOP", color = if (record.isLocked) AlertRed else SafeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedVideoForPlay = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text("Close", color = Color.Black)
                }
            },
            containerColor = AutomotiveSurface
        )
    }

    // Format SD Card Confirmation Dialog
    if (showFormatDialog) {
        AlertDialog(
            onDismissRequest = { showFormatDialog = false },
            title = { Text("Format SD Card?", color = Color.White) },
            text = { Text("All recordings and snapshots will be permanently deleted. This cannot be undone.", color = Color.White.copy(0.8f)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.formatStorage()
                        showFormatDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Format Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFormatDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = AutomotiveSurface
        )
    }
}

@Composable
private fun DvrChannelBadge(name: String, res: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(AutomotiveSurfaceVariant, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = name, color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = res, color = Color.White.copy(0.7f), fontSize = 8.sp)
    }
}
