package com.example.view360.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.view360.data.CarRepository
import com.example.view360.model.CarCategory
import com.example.view360.model.CarModelItem
import com.example.view360.model.CarPaint
import com.example.view360.model.LicensePlateStyle
import com.example.view360.ui.theme.AutomotiveBackground
import com.example.view360.ui.theme.AutomotiveBorder
import com.example.view360.ui.theme.AutomotiveSurface
import com.example.view360.ui.theme.AutomotiveSurfaceVariant
import com.example.view360.ui.theme.CyberCyan
import com.example.view360.ui.theme.ElectricBlue
import com.example.view360.viewmodel.AVMUiState
import com.example.view360.viewmodel.AVMViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CarShopScreen(
    uiState: AVMUiState,
    viewModel: AVMViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<CarCategory?>(null) }
    var plateInput by remember(uiState.licensePlate) { mutableStateOf(uiState.licensePlate) }
    val context = LocalContext.current

    val filteredCars = remember(selectedCategory) {
        if (selectedCategory == null) CarRepository.vehicleModels
        else CarRepository.vehicleModels.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("car_shop_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Hero Showcase Card of Current Car
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AutomotiveBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiState.currentCarModel.name,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.currentCarModel.brand} • ${uiState.currentCarModel.category.displayName}",
                                color = CyberCyan,
                                fontSize = 12.sp
                            )
                        }

                        // Rendered License Plate Badge
                        LicensePlateBadge(
                            plateText = uiState.licensePlate,
                            plateStyle = uiState.licensePlateStyle
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vehicle asset image preview
                    val carBitmap = remember(uiState.currentCarModel.assetPath) {
                        try {
                            val stream = context.assets.open(uiState.currentCarModel.assetPath)
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(AutomotiveSurfaceVariant, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (carBitmap != null) {
                            Image(
                                bitmap = carBitmap,
                                contentDescription = uiState.currentCarModel.name,
                                modifier = Modifier.size(170.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dimension specs badge
                    val dims = uiState.currentCarModel.dimensions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SpecItem("Length", "${dims.lengthMm} mm")
                        SpecItem("Width", "${dims.widthMm} mm")
                        SpecItem("Wheelbase", "${dims.wheelBaseMm} mm")
                        SpecItem("Front O/H", "${dims.frontOverhangMm} mm")
                        SpecItem("Rear O/H", "${dims.rearOverhangMm} mm")
                    }
                }
            }
        }

        // Section 2: Paint Color Palette
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AutomotiveBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Vehicle Paint & Finish: ${uiState.currentPaint.name}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(CarRepository.defaultPaintList) { paint ->
                            val isSelected = uiState.currentPaint.name == paint.name
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(paint.color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) CyberCyan else Color.White.copy(0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.selectPaint(paint) }
                                    .testTag("paint_${paint.name.replace(" ", "_").lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (paint.color == Color(0xFFF5F5F5) || paint.color == Color.White) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Custom License Plate
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AutomotiveSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AutomotiveBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "License Plate Customizer",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = plateInput,
                        onValueChange = {
                            plateInput = it
                            viewModel.updateLicensePlate(it, uiState.licensePlateStyle)
                        },
                        label = { Text("Plate Number") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = AutomotiveBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("license_plate_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Plate Style selector
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LicensePlateStyle.values().forEach { style ->
                            val isSelected = uiState.licensePlateStyle == style
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateLicensePlate(plateInput, style) },
                                label = { Text(style.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan,
                                    selectedLabelColor = Color.Black,
                                    containerColor = AutomotiveSurfaceVariant,
                                    labelColor = Color.White
                                ),
                                modifier = Modifier.testTag("plate_style_${style.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Vehicle Model Catalog
        item {
            Column {
                Text(
                    text = "Vehicle Models Library",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.White,
                                containerColor = AutomotiveSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                    items(CarCategory.values()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.White,
                                containerColor = AutomotiveSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Models list items
        items(filteredCars) { car ->
            val isSelected = uiState.currentCarModel.id == car.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AutomotiveSurfaceVariant else AutomotiveSurface
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) CyberCyan else AutomotiveBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectCarModel(car) }
                    .testTag("car_card_${car.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val thumb = remember(car.assetPath) {
                        try {
                            val stream = context.assets.open(car.assetPath)
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(AutomotiveBackground, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (thumb != null) {
                            Image(bitmap = thumb, contentDescription = car.name, modifier = Modifier.size(64.dp))
                        } else {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = CyberCyan)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = car.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${car.brand} • ${car.category.displayName}", color = CyberCyan, fontSize = 11.sp)
                        Text(
                            text = "${car.dimensions.lengthMm} × ${car.dimensions.widthMm} mm",
                            color = Color.White.copy(0.6f),
                            fontSize = 10.sp
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .background(CyberCyan, CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LicensePlateBadge(plateText: String, plateStyle: LicensePlateStyle) {
    Surface(
        color = Color(plateStyle.bgHex),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.5f))
    ) {
        Text(
            text = plateText.ifEmpty { "粵A·360BV" },
            color = Color(plateStyle.textHex),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun SpecItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.White.copy(0.5f), fontSize = 10.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
