package com.sandorln.item.util

import com.sandorln.model.data.item.ItemData

fun ItemData.getUniqueStatusList(): Pair<String, String> {
    var unique = description.substringAfter("</stats>")
    while (unique.startsWith("</stats>")) {
        unique = unique.substringAfter("</stats>")
    }

    unique = unique
        .replace("<li>", "<br>")
        .replace("</mainText>", "")

    while (unique.startsWith("<br>")) {
        unique = unique.substringAfter("<br>")
    }

    return name to unique
}

fun ItemData.getStatusList(): List<Triple<String, Int, String>> {
    val (major1, minor1, _) = version.split('.').map { it.toInt() }
    val hasAttentionTag = major1 > 10 || (major1 >= 10 && minor1 > 22)

    return description
        .substringAfter("<stats>")
        .substringBefore("</stats>")
        .split("<br>")
        .map { status ->
            val statusTitle: String
            val valuePartition: Pair<String, String>
            if (hasAttentionTag) {
                statusTitle = status
                    .substringBefore("<attention>")
                    .trim()

                valuePartition = status
                    .substringBefore("</attention>")
                    .substringAfter("<attention>")
                    .trim()
                    .partition { (48..57).contains(it.code) }
            } else {
                statusTitle = status
                    .substringBefore("+")
                    .trim()

                valuePartition = status
                    .substringAfter("+")
                    .trim()
                    .partition { (48..57).contains(it.code) }
            }

            val value = valuePartition.first
            val suffix = valuePartition.second

            Triple(statusTitle, value.toIntOrNull() ?: 0, suffix)
        }
        .filter {
            it.first.isNotEmpty() && it.second > 0
        }
}