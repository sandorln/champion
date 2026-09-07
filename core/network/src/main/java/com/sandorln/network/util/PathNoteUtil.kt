package com.sandorln.network.util

import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.model.patchnote.NetworkPatchNoteType
import com.sandorln.network.model.patchnote.NetworkPatchNoteType.Champion
import org.jsoup.nodes.Element

fun Element.toNetworkPatchNoteList(
    networkPatchNoteType: NetworkPatchNoteType
): List<NetworkPatchNoteData> {
    return runCatching {
        val results = mutableListOf<NetworkPatchNoteData>()
        val container = this.selectFirst("#patch-notes-container")
            ?: this.selectFirst(".article-content")
            ?: this

        var isTargetSection = false
        for (element in container.children()) {
            val tagName = element.tagName()

            if (tagName == "header" || tagName == "h2") {
                val headerText = if (tagName == "header") {
                    element.select("h2").text()
                } else {
                    element.text()
                }

                if (headerText.isNotBlank()) {
                    isTargetSection = headerText.contains(networkPatchNoteType.patchName)
                }
                continue
            }

            if (isTargetSection && element.hasClass("content-border")) {
                val titleElement = element.selectFirst("h3, h4") ?: continue
                val title = titleElement.text().trim()

                var imgUrl = element.select(".reference-link img").attr("abs:src")
                if (imgUrl.isEmpty()) {
                    imgUrl = titleElement.select("img").attr("abs:src")
                }
                if (imgUrl.isEmpty()) {
                    imgUrl = element.select("img").first()?.attr("abs:src") ?: ""
                }

                val summaryBuilder = StringBuilder()
                val foundSummaryP = element.select("p.summary").text().trim()

                if (networkPatchNoteType == Champion && foundSummaryP.isNotEmpty()) {
                    summaryBuilder.append(foundSummaryP)
                } else {
                    val contentElements = element.select("h4, ul")
                    contentElements.forEach { sibling ->
                        if (sibling.tagName() == "h4" && sibling.text().isNotBlank()) {
                            val h4Text = sibling.text().trim()

                            if (h4Text != title) summaryBuilder.append("\n\n[${h4Text}]")
                        } else if (sibling.tagName() == "ul") {
                            sibling.select("li").forEach { li ->
                                summaryBuilder.append("\n- ${li.text().trim()}")
                            }
                        }
                    }

                    if (summaryBuilder.isEmpty()) {
                        summaryBuilder.append(element.select("blockquote").text().trim())
                    }
                }

                if (title.isNotEmpty()) {
                    results.add(
                        NetworkPatchNoteData(
                            title = title,
                            imageUrl = imgUrl,
                            summary = summaryBuilder.toString().trim()
                        )
                    )
                }
            }
        }

        return results
    }.onFailure {
        it.printStackTrace()
    }.getOrDefault(emptyList())
}

fun String.getPatchNoteUrl(): String {
    val (major1, minor1, _) = this.split('.').map { it.toInt() }
    if (major1 < 10) return ""

    val urlBuilder = StringBuilder("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-")
    return when {
        major1 >= 16 -> urlBuilder.append("${major1 + 10}-$minor1-notes/")
        major1 == 15 && (1..2).contains(minor1) -> urlBuilder.append("${major1 + 10}-s1-$minor1-notes/")
        major1 == 15 && 3 == minor1 -> urlBuilder.append("2025-s1-3-notes/")
        major1 >= 15 -> urlBuilder.append("${major1 + 10}-${minor1.toString().padStart(2, '0')}-notes/")
        else -> urlBuilder.append("$major1-$minor1-notes/")
    }.toString()
}