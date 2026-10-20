package com.example.view360.model

import androidx.compose.ui.graphics.Color

enum class CarCategory(val displayName: String) {
    SEDAN("Sedan"),
    SUV("SUV"),
    HATCHBACK("Hatchback"),
    MPV("MPV / Van"),
    COMMERCIAL("Commercial / Truck")
}

enum class LicensePlateStyle(val displayName: String, val bgHex: Long, val textHex: Long) {
    BLUE("Standard Blue", 0xFF1565C0, 0xFFFFFFFF),
    GREEN("New Energy (EV)", 0xFF2E7D32, 0xFF000000),
    YELLOW("Commercial Yellow", 0xFFF57F17, 0xFF000000),
    BLACK("Consular Black", 0xFF1E222A, 0xFFFFFFFF),
    WHITE("Official White", 0xFFFFFFFF, 0xFF000000)
}

data class VehicleDimension(
    val lengthMm: Int,
    val widthMm: Int,
    val wheelBaseMm: Int,
    val frontOverhangMm: Int,
    val rearOverhangMm: Int,
    val maxSteerAngleDeg: Float = 540f,
    val maxWheelAngleDeg: Float = 40f
)

data class CarPaint(
    val name: String,
    val color: Color,
    val reflectionFactor: Float = 0.5f,
    val metallic: Boolean = true
)

data class CarModelItem(
    val id: String,
    val name: String,
    val brand: String,
    val category: CarCategory,
    val assetPath: String, // e.g. "cars/SedanA.png" or "mall/BMW_5.png"
    val dimensions: VehicleDimension,
    val defaultPaint: CarPaint
)
