package com.sandorln.design.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sandorln.design.R

val pretendardFont = FontFamily(
    Font(R.font.pretendard_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.pretendard_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.pretendard_light, FontWeight.Light, FontStyle.Normal),
)

object TextStyles {
    val Title01 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 34.sp, color = Colors.BasicWhite)
    val Title02 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 28.sp, color = Colors.BasicWhite)
    val Title03 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 26.sp, color = Colors.BasicWhite)
    val Title04 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 22.sp, color = Colors.BasicWhite)

    val SubTitle01 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 26.sp, color = Colors.BasicWhite)
    val SubTitle02 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 22.sp, color = Colors.BasicWhite)
    val SubTitle03 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 20.sp, color = Colors.BasicWhite)

    val Body01 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 22.sp, color = Colors.BasicWhite)
    val Body02 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 20.sp, color = Colors.BasicWhite)
    val Body03 = TextStyle(fontFamily = pretendardFont, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 18.sp, color = Colors.BasicWhite)
}
