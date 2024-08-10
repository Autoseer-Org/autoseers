package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.innovara.autoseers.R

val fontFamily = FontFamily(
    Font(R.font.poppins_black, weight = FontWeight.Black),
    Font(R.font.poppins_blackitalic, weight = FontWeight.Black, style = FontStyle.Italic),
    Font(R.font.poppins_bold, weight = FontWeight.Bold),
    Font(R.font.poppins_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.poppins_medium, weight = FontWeight.Medium),
    Font(R.font.poppins_regular, weight = FontWeight.Normal),
    Font(R.font.poppins_light, weight = FontWeight.Light),
    Font(R.font.poppins_thin, weight = FontWeight.Thin),
    Font(R.font.poppins_extraboiditalic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
    Font(R.font.poppins_mediumitalic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.poppins_extralightitalic),
)

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = fontFamily,
        fontSize = 42.sp,
        fontWeight = FontWeight.Bold
    ),
    bodySmall = TextStyle(
        fontFamily = fontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(fontFamily = fontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold),
    labelSmall = TextStyle(
        fontFamily = fontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Light
    ),
    headlineSmall = TextStyle(
        fontFamily = fontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
)


