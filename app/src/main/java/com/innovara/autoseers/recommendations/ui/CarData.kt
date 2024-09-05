package com.innovara.autoseers.recommendations.ui

data class CarListData(
    val carList: Map<String, List<String>>
)

val carListData = CarListData(
    carList = mapOf(
        "Toyota" to listOf(
            "Camry",
            "Supra",
            "Toyota Crown",
            "Corolla",
            "GR86",
            "GR Corolla",
            "Mirai",
            "Allion",
            "Yaris",
            "Rav4",
            "4Runner",
        ),
        "Hyundai" to listOf(
            "Palisade",
            "Santa Cruz",
            "Venue",
            "Tucson",
            "Hyundai Santa Fe",
            "Elantra",
            "Sonata",
            "Veloster N",
            "NEXO"
        ),
        "Ford" to listOf(
            "Escape",
            "Bronco Sport",
            "Bronco",
            "Explorer",
            "Expeditions",
            "Maverick",
            "Ranger",
            "Transit Connect",
            "F-150",
            "SuperDuty",
            "Mustang"
        ),
        "BMW" to listOf(
            "330e",
            "330i",
            "530i",
            "X1",
            "X2 Coupe SUV",
            "X3 SUV",
            "X3 M SUV",
            "X4 SUV",
            "X4 M Coupe SUV",
            "2 series Gran Coupe",
            "3 Series Sedan",
            "4 Series Gran Coupe",
            "5 Series Sedan",
            "7 Series Sedan",
            "8 Series Gran Sedan",
            "M3",
            "M5 Sedan",
            "M8"
        ),
        "Audi" to listOf(
            "e-tron GT",
            "RS e-tron GT",
            "Audi Q5",
            "Audi Q5",
            "Audi Q7",
            "Audi Q8",
            "A3",
            "S3",
            "A4",
            "A5",
            "A7",
            "A8",
        ),
    )
)

var minYear = 2008
val years = generateSequence { (minYear++).takeIf { it < 2025 } }.toList()