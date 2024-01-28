package com.sandorln.design.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sandorln.design.R

val warhavenFont = FontFamily(
    Font(R.font.warhaven_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.warhaven_regular, FontWeight.Normal, FontStyle.Normal),
)

val beaufortforFont = FontFamily(
    Font(R.font.beaufortfor_lol_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.beaufortfor_lol_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.beaufortfor_lol_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.beaufortfor_lol_light, FontWeight.Light, FontStyle.Normal),
)

object TextStyles {
    val Title01 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Colors.BasicWhite)
    val Title02 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Colors.BasicWhite)
    val Title03 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Colors.BasicWhite)
    val Title04 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Colors.BasicWhite)

    val SubTitle01 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Colors.BasicWhite)
    val SubTitle02 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Colors.BasicWhite)
    val SubTitle03 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Colors.BasicWhite)

    val Body01 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Colors.BasicWhite)
    val Body02 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = Colors.BasicWhite)
    val Body03 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Medium, fontSize = 11.sp, color = Colors.BasicWhite)
    val Body04 = TextStyle(fontFamily = warhavenFont, fontWeight = FontWeight.Medium, fontSize = 9.sp, color = Colors.BasicWhite)
}
