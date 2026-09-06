package com.sandorln.network.util

import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.model.patchnote.NetworkPatchNoteType
import com.sandorln.network.model.patchnote.NetworkPatchNoteType.Champion
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

private fun extractRichTextBodies(jsonString: String): String {
    return runCatching {
        val root = Json.parseToJsonElement(jsonString)
        val bodies = mutableListOf<String>()

        fun traverse(element: JsonElement) {
            when (element) {
                is JsonObject -> {
                    for ((key, value) in element) {
                        if (key == "richText" && value is JsonObject) {
                            val body = value["body"]?.jsonPrimitive?.contentOrNull
                            if (!body.isNullOrBlank()) {
                                bodies.add(body)
                            }
                        } else {
                            traverse(value)
                        }
                    }
                }
                is JsonArray -> {
                    for (item in element) {
                        traverse(item)
                    }
                }
                else -> {}
            }
        }

        traverse(root)
        bodies.joinToString("\n")
    }.getOrDefault("")
}

fun Element.toNetworkPatchNoteList(
    networkPatchNoteType: NetworkPatchNoteType
): List<NetworkPatchNoteData> {
    return runCatching {
        val results = mutableListOf<NetworkPatchNoteData>()

        // 1. Next.js SSR 데이터가 있는 경우 추출하여 파싱된 Root Element 사용
        val nextDataScript = this.selectFirst("script#__NEXT_DATA__")
        val effectiveRoot: Element = if (nextDataScript != null) {
            val jsonText = nextDataScript.data()
            val richHtml = extractRichTextBodies(jsonText)
            if (richHtml.isNotBlank()) {
                Jsoup.parse(richHtml)
            } else {
                this
            }
        } else {
            this
        }

        val container = effectiveRoot.selectFirst("#patch-notes-container")
            ?: effectiveRoot.selectFirst(".article-content")
            ?: effectiveRoot.selectFirst("body")
            ?: effectiveRoot

        var isTargetSection = false
        var isOtherMode = false
        val excludedModeKeywords = listOf("아레나", "무작위 총력전", "칼바람", "클래식", "버그 수정", "앞으로 나올", "스킨")

        for (element in container.children()) {
            val tagName = element.tagName()

            if (tagName == "header" || tagName == "h2") {
                val headerText = if (tagName == "header") {
                    element.select("h2").text()
                } else {
                    element.text()
                }

                if (headerText.isNotBlank()) {
                    if (excludedModeKeywords.any { headerText.contains(it) }) {
                        isOtherMode = true
                        isTargetSection = false
                        continue
                    }

                    if (isOtherMode) {
                        if (headerText.contains("소환사의 협곡")) {
                            isOtherMode = false
                        } else {
                            continue
                        }
                    }

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
                if (imgUrl.isEmpty()) {
                    imgUrl = element.select("img").first()?.attr("src") ?: ""
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
                    if (summaryBuilder.isEmpty() && foundSummaryP.isNotEmpty()) {
                        summaryBuilder.append(foundSummaryP)
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

fun String.getPatchNoteUrlCandidates(): List<String> {
    val versionParts = this.split('.').mapNotNull { it.toIntOrNull() }
    if (versionParts.size < 2) return emptyList()

    val major = versionParts[0]
    val minor = versionParts[1]
    if (major < 10) return emptyList()

    val candidates = mutableListOf<String>()

    if (major == 15 && (1..2).contains(minor)) {
        candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-${major + 10}-s1-$minor-notes/")
        candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/league-of-legends-patch-${major + 10}-s1-$minor-notes/")
        return candidates
    }

    if (major == 15 && minor == 3) {
        candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-2025-s1-3-notes/")
        candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/league-of-legends-patch-2025-s1-3-notes/")
        return candidates
    }

    val displayMajor = if (major >= 15) major + 10 else major
    val minorStr = minor.toString()
    val minorPadded = minorStr.padStart(2, '0')

    // 1. league-of-legends-patch-{major}-{minor}-notes/
    candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/league-of-legends-patch-$displayMajor-$minorStr-notes/")
    if (minorStr != minorPadded) {
        candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/league-of-legends-patch-$displayMajor-$minorPadded-notes/")
    }

    // 2. patch-{major}-{minor}-notes/
    candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-$displayMajor-$minorStr-notes/")
    if (minorStr != minorPadded) {
        candidates.add("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-$displayMajor-$minorPadded-notes/")
    }

    return candidates.distinct()
}

fun String.getPatchNoteUrl(): String {
    return this.getPatchNoteUrlCandidates().firstOrNull() ?: ""
}

fun fetchPatchNoteList(
    version: String,
    type: NetworkPatchNoteType
): List<NetworkPatchNoteData> {
    val candidates = version.getPatchNoteUrlCandidates()
    for (url in candidates) {
        val result = runCatching {
            Jsoup.connect(url)
                .timeout(10_000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .get()
                .toNetworkPatchNoteList(type)
        }.getOrNull()

        if (!result.isNullOrEmpty()) {
            return result
        }
    }
    return emptyList()
}