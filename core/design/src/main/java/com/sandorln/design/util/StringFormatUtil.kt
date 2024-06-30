package com.sandorln.design.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private val serverSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun String.before(): String =
    kotlin.runCatching {
        val before = serverSimpleDateFormat.parse(this) ?: return ""
        val now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
        val diffInMillis = now.time - before.time

        val second = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        val minute = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)

        when {
            second < 60 -> "방금 전"
            minute < 60 -> "${minute}분 전"
            hours < 24 -> "${hours}시간 전"
            else -> SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN).format(before)
        }
    }.fold(
        onSuccess = { it },
        onFailure = { "Error" }
    )

val thousandDotDecimalFormat = DecimalFormat("#,###")
