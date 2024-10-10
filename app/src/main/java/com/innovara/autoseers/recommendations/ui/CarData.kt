package com.innovara.autoseers.recommendations.ui

data class CarListData(
    val carList: Map<String, List<String>>
)

val carListData = CarListData(
    carList = mapOf(
        "Toyota" to listOf(
            "Camry",
            "Camry Hybrid",      // Hybrid/Eco
            "Corolla",
            "Corolla Hybrid",    // Hybrid/Eco
            "Rav4",              // Crossover
            "Rav4 Hybrid",       // Hybrid/Eco Crossover
            "Corolla Cross",     // Crossover
            "Corolla Cross Hybrid", // Hybrid/Eco Crossover
            "Highlander",
            "Highlander Hybrid", // Hybrid/Eco SUV
            "Sienna Hybrid",     // Hybrid/Eco Minivan
            "Tundra",
            "Tacoma",
            "4Runner",
            "Supra",             // Sports
            "Celica",            // Sports
            "MR2",               // Sports
            "GR86",              // Sports
            "Prius",             // Hybrid/Eco
            "Prius Prime",       // Hybrid/Eco
            "Yaris",             // Hatchback
            "Corolla Hatchback", // Hatchback
            "BZ4X"               // EV SUV
        ).sorted(),
        "Honda" to listOf(
            "Civic",             // Sedan
            "Civic Hatchback",   // Hatchback
            "Civic Type R",      // Sports
            "Accord",
            "Accord Hybrid",     // Hybrid/Eco Sedan
            "CR-V",              // Crossover
            "CR-V Hybrid",       // Hybrid/Eco Crossover
            "HR-V",              // Crossover
            "Pilot",
            "Insight",           // Hybrid/Eco Sedan
            "Clarity",           // Hybrid/Eco Sedan (EV version available)
            "Fit",               // Hatchback
            "Ridgeline",         // Truck
            "S2000",             // Sports
            "NSX"                // Sports
        ).sorted(),
        "Ford" to listOf(
            "F-150",
            "F-150 Hybrid",      // Hybrid/Eco Truck
            "F-150 Lightning",   // EV Truck
            "Explorer",
            "Explorer ST",       // Sports SUV
            "Escape",            // Crossover
            "Escape Hybrid",     // Hybrid/Eco Crossover
            "Mustang",           // Muscle
            "Mustang Mach-E",    // EV Crossover
            "Shelby GT500",      // Muscle
            "Edge",              // Crossover
            "Bronco",
            "Bronco Sport",      // Crossover
            "Taurus SHO",        // Sports Sedan
            "Fusion Hybrid",     // Hybrid/Eco Sedan
            "Focus RS",          // Sports Hatchback
            "Focus",             // Hatchback
            "Maverick Hybrid"    // Hybrid/Eco Truck
        ).sorted(),
        "Chevrolet" to listOf(
            "Silverado",
            "Silverado Hybrid",  // Hybrid/Eco Truck
            "Malibu",
            "Tahoe",
            "Equinox",           // Crossover
            "Blazer",            // Crossover
            "Traverse",          // Crossover
            "Suburban",
            "Impala",
            "Camaro",            // Muscle
            "Corvette",          // Sports
            "SS",                // Muscle
            "Monte Carlo",       // Sports/Muscle
            "Chevelle",          // Muscle
            "Bolt EV",           // EV Hatchback
            "Volt",              // Hybrid/Eco Hatchback
            "Spark",             // Hatchback
            "Spark EV"           // EV Hatchback
        ).sorted(),
        "Nissan" to listOf(
            "Altima",
            "Sentra",
            "Rogue",             // Crossover
            "Rogue Hybrid",      // Hybrid/Eco Crossover
            "Murano",            // Crossover
            "Maxima",
            "370Z",              // Sports
            "GT-R",              // Sports
            "240SX",             // Sports
            "300ZX",             // Sports
            "Nismo Juke",        // Sports Crossover
            "Leaf"               // EV
        ).sorted(),
        "Dodge" to listOf(
            "Ram 1500",
            "Ram 1500 Hybrid",   // Hybrid/Eco Truck
            "Charger",           // Muscle
            "Charger SRT",       // Muscle
            "Challenger",        // Muscle
            "Challenger SRT",    // Muscle
            "Durango",
            "Durango SRT",       // Sports SUV
            "Viper",             // Sports
            "Magnum",            // Crossover
            "Dart GT",           // Sports Sedan
            "Neon SRT-4",        // Sports Sedan
            "Caliber",           // Hatchback
            "Journey"            // Crossover
        ).sorted(),
        "Jeep" to listOf(
            "Wrangler",
            "Wrangler 4xe",      // Hybrid/Eco SUV
            "Cherokee",          // Crossover
            "Grand Cherokee",
            "Grand Cherokee 4xe",// Hybrid/Eco SUV
            "Compass",           // Crossover
            "Patriot",           // Crossover
            "Renegade",          // Crossover
            "Gladiator"          // Truck
        ).sorted(),
        "Subaru" to listOf(
            "Outback",           // Crossover
            "Forester",          // Crossover
            "Impreza",           // Hatchback
            "Legacy",
            "Crosstrek",         // Crossover
            "WRX",               // Sports
            "WRX STI",           // Sports
            "BRZ",               // Sports
            "Crosstrek Hybrid"   // Hybrid/Eco Crossover
        ).sorted(),
        "Hyundai" to listOf(
            "Elantra",
            "Elantra GT",        // Hatchback
            "Sonata",
            "Sonata Hybrid",     // Hybrid/Eco Sedan
            "Tucson",            // Crossover
            "Tucson Hybrid",     // Hybrid/Eco Crossover
            "Santa Fe",          // Crossover
            "Santa Fe Hybrid",   // Hybrid/Eco Crossover
            "Kona Electric",     // EV Crossover
            "Venue",             // Crossover
            "Veloster",          // Hatchback
            "Veloster N",        // Sports
            "Genesis Coupe",     // Sports
            "Ioniq Hybrid",      // Hybrid/Eco
            "Ioniq 5"            // EV Crossover
        ).sorted(),
        "BMW" to listOf(
            "3 Series",
            "5 Series",
            "X3",                // Crossover
            "X3 Hybrid",         // Hybrid/Eco Crossover
            "X5",                // Crossover
            "X5 Hybrid",         // Hybrid/Eco SUV
            "X1",                // Crossover
            "X2",                // Crossover
            "7 Series",
            "M3",                // Sports
            "M5",                // Sports
            "Z4",                // Sports
            "M2",                // Sports
            "8 Series",          // Sports
            "i3",                // EV Hatchback
            "i8",                // Hybrid/Eco Sports
            "X5 xDrive45e"       // Hybrid/Eco SUV
        ).sorted(),
        "Mercedes-Benz" to listOf(
            "C-Class",
            "E-Class",
            "S-Class",
            "GLC",               // Crossover
            "GLC Hybrid",        // Hybrid/Eco Crossover
            "GLE",               // Crossover
            "GLE Hybrid",        // Hybrid/Eco SUV
            "GLA",               // Crossover
            "AMG GT",            // Sports
            "SL",                // Sports
            "C63 AMG",           // Sports
            "E63 AMG",           // Sports
            "SLS AMG",           // Sports
            "EQC"                // EV SUV
        ).sorted(),
        "Volkswagen" to listOf(
            "Jetta",
            "Passat",
            "Golf",              // Hatchback
            "GTI",               // Hatchback/Sports
            "Golf R",            // Hatchback/Sports
            "Beetle",            // Hatchback
            "Scirocco",          // Sports
            "Corrado",           // Sports
            "Tiguan",            // Crossover
            "Atlas",             // Crossover
            "ID.4",              // EV Crossover
            "e-Golf"             // EV Hatchback
        ).sorted()
    )
)

var minYear = 1949
val years = generateSequence { (minYear++).takeIf { it < 2026 } }.toList()