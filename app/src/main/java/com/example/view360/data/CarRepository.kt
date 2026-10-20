package com.example.view360.data

import androidx.compose.ui.graphics.Color
import com.example.view360.model.CarCategory
import com.example.view360.model.CarModelItem
import com.example.view360.model.CarPaint
import com.example.view360.model.VehicleDimension
import com.example.view360.ui.theme.PaintBlack
import com.example.view360.ui.theme.PaintBlue
import com.example.view360.ui.theme.PaintGold
import com.example.view360.ui.theme.PaintGreen
import com.example.view360.ui.theme.PaintRed
import com.example.view360.ui.theme.PaintSilver
import com.example.view360.ui.theme.PaintWhite

object CarRepository {

    val defaultPaintList = listOf(
        CarPaint("Glacier White", PaintWhite, 0.6f),
        CarPaint("Onyx Black", PaintBlack, 0.8f),
        CarPaint("Titanium Silver", PaintSilver, 0.7f),
        CarPaint("Deep Sea Blue", PaintBlue, 0.65f),
        CarPaint("Crimson Red", PaintRed, 0.75f),
        CarPaint("Emerald Green", PaintGreen, 0.6f),
        CarPaint("Cyber Amber", PaintGold, 0.7f)
    )

    val vehicleModels = listOf(
        CarModelItem(
            id = "sedan_a",
            name = "A-Class Sedan",
            brand = "Generic AVM",
            category = CarCategory.SEDAN,
            assetPath = "cars/SedanA.png",
            dimensions = VehicleDimension(4682, 1823, 2689, 1003, 1432),
            defaultPaint = defaultPaintList[0]
        ),
        CarModelItem(
            id = "suv_a",
            name = "A-Class SUV",
            brand = "Generic AVM",
            category = CarCategory.SUV,
            assetPath = "cars/SuvA.png",
            dimensions = VehicleDimension(4753, 1893, 2908, 1107, 738),
            defaultPaint = defaultPaintList[3]
        ),
        CarModelItem(
            id = "hatchback_a",
            name = "A-Class Hatchback",
            brand = "Generic AVM",
            category = CarCategory.HATCHBACK,
            assetPath = "cars/HatchbackA.png",
            dimensions = VehicleDimension(4236, 1785, 2628, 964, 643),
            defaultPaint = defaultPaintList[4]
        ),
        CarModelItem(
            id = "buick_gl8",
            name = "Executive MPV (GL8)",
            brand = "Buick",
            category = CarCategory.MPV,
            assetPath = "cars/Buick_GL8.png",
            dimensions = VehicleDimension(5230, 1920, 3018, 1003, 1209),
            defaultPaint = defaultPaintList[2]
        ),
        CarModelItem(
            id = "bmw_5",
            name = "5-Series Luxury Sedan",
            brand = "BMW",
            category = CarCategory.SEDAN,
            assetPath = "mall/BMW_5.png",
            dimensions = VehicleDimension(5038, 1886, 3024, 930, 1110),
            defaultPaint = defaultPaintList[1]
        ),
        CarModelItem(
            id = "benz_g",
            name = "G-Class Offroad 4x4",
            brand = "Mercedes-Benz",
            category = CarCategory.SUV,
            assetPath = "mall/Benz_G_2022.png",
            dimensions = VehicleDimension(4882, 1850, 2850, 901, 1131),
            defaultPaint = defaultPaintList[1]
        ),
        CarModelItem(
            id = "commercial_truck",
            name = "Heavy Cargo 10M",
            brand = "Futon",
            category = CarCategory.COMMERCIAL,
            assetPath = "mall/Futon_Truck10M.png",
            dimensions = VehicleDimension(9570, 2300, 6000, 1785, 1785),
            defaultPaint = defaultPaintList[4]
        ),
        CarModelItem(
            id = "transit_bus",
            name = "City Transit Coach",
            brand = "Bus",
            category = CarCategory.COMMERCIAL,
            assetPath = "mall/Bus.png",
            dimensions = VehicleDimension(11980, 2550, 6000, 2990, 2990),
            defaultPaint = defaultPaintList[0]
        )
    )
}
