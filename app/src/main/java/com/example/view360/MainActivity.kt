package com.example.view360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PanoramaPhotosphere
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.model.VehicleGear
import com.example.view360.ui.screens.AVMScreen
import com.example.view360.ui.screens.CalibrationScreen
import com.example.view360.ui.screens.CarShopScreen
import com.example.view360.ui.screens.DVRScreen
import com.example.view360.ui.screens.SettingsScreen
import com.example.view360.ui.theme.AlertRed
import com.example.view360.ui.theme.AutomotiveBackground
import com.example.view360.ui.theme.AutomotiveBorder
import com.example.view360.ui.theme.AutomotiveSurface
import com.example.view360.ui.theme.AutomotiveSurfaceVariant
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.ui.theme.SafeGreen
import com.example.view360.ui.theme.View360Theme
import com.example.view360.viewmodel.AVMUiState
import com.example.view360.viewmodel.AVMViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MainNavDestination(val label: String, val icon: ImageVector) {
    SURROUND_VIEW("360 AVM", Icons.Default.PanoramaPhotosphere),
    CAR_SHOP("Garage", Icons.Default.DirectionsCar),
    DVR_PLAYBACK("4-CH DVR", Icons.Default.Videocam),
    CALIBRATION("Calibration", Icons.Default.Build),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: AVMViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            View360Theme {
                MainAppContent(viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AVMViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var currentDestination by remember { mutableStateOf(MainNavDestination.SURROUND_VIEW) }
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = currentDestination != MainNavDestination.SURROUND_VIEW) {
        currentDestination = MainNavDestination.SURROUND_VIEW
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CockpitTopBar(uiState)
        },
        bottomBar = {
            CockpitBottomBar(
                currentDestination = currentDestination,
                onNavigate = { currentDestination = it }
            )
        },
        containerColor = AutomotiveBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                MainNavDestination.SURROUND_VIEW -> AVMScreen(uiState, viewModel)
                MainNavDestination.CAR_SHOP -> CarShopScreen(uiState, viewModel)
                MainNavDestination.DVR_PLAYBACK -> DVRScreen(uiState, viewModel)
                MainNavDestination.CALIBRATION -> CalibrationScreen(uiState, viewModel)
                MainNavDestination.SETTINGS -> SettingsScreen(uiState, viewModel)
            }
        }
    }
}

@Composable
private fun CockpitTopBar(uiState: AVMUiState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cockpit_top_bar"),
        color = AutomotiveSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, AutomotiveBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand & App Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(CyberCyan, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PanoramaPhotosphere,
                        contentDescription = "Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "VIEW 360",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Around View Monitor v2.22",
                        color = CyberCyan,
                        fontSize = 9.sp
                    )
                }
            }

            // Central Telemetry HUD: Gear + Model Name + Speed
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gear Indicator
                Box(
                    modifier = Modifier
                        .background(
                            when (uiState.currentGear) {
                                VehicleGear.REVERSE -> AlertRed
                                VehicleGear.DRIVE -> SafeGreen
                                else -> CyberCyan
                            },
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = uiState.currentGear.symbol,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = uiState.currentCarModel.name,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )

                Text(
                    text = "${uiState.speedKmh.toInt()} km/h",
                    color = SafeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Right Status Indicators: 4-CAM online + DVR REC
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 4 Camera status dots (F, R, L, R)
                Row(
                    modifier = Modifier
                        .background(AutomotiveSurfaceVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("4-CAM", color = Color.White.copy(0.7f), fontSize = 9.sp)
                    listOf("F", "R", "L", "R").forEach {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SafeGreen)
                        )
                    }
                }

                // DVR REC badge
                if (uiState.dvrRecording) {
                    Row(
                        modifier = Modifier
                            .background(AlertRed.copy(0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AlertRed)
                        )
                        Text("REC", color = AlertRed, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CockpitBottomBar(
    currentDestination: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar(
        containerColor = AutomotiveSurface,
        tonalElevation = 4.dp,
        modifier = Modifier.testTag("cockpit_bottom_navigation")
    ) {
        MainNavDestination.values().forEach { destination ->
            val isSelected = currentDestination == destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = destination.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CyberCyan,
                    indicatorColor = CyberCyan,
                    unselectedIconColor = Color.White.copy(0.6f),
                    unselectedTextColor = Color.White.copy(0.6f)
                ),
                modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
            )
        }
    }
}
