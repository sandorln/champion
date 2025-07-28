package com.sandorln.network.util

import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.model.patchnote.NetworkPatchNoteType
import com.sandorln.network.model.patchnote.NetworkPatchNoteType.Champion
import com.sandorln.network.model.patchnote.NetworkPatchNoteType.Item
import com.sandorln.network.model.patchnote.NetworkPatchNoteType.Rune
import org.jsoup.nodes.Element

private const val PATCH_NOTES_CONTAINER_CLASS_NAME = "patch-notes-container"
private const val HEADER_PRIMARY_CLASS_NAME = "header-primary"
private const val PATCH_CHANGE_BLOCK_CLASS_NAME = "patch-change-block white-stone accent-before"
private const val PATCH_FILTER_CHECK_CLASS_NAME = "reference-link"

private const val PATCH_TITLE_CLASS_NAME = "change-title"
private const val PATCH_SUMMARY_CLASS_NAME = "summary"

fun Element.toNetworkPatchNoteList(
    networkPatchNoteType: NetworkPatchNoteType
): List<NetworkPatchNoteData> {
    return runCatching {
        val patchNoteElement = getElementById(PATCH_NOTES_CONTAINER_CLASS_NAME) ?: throw Exception("패치노트를 찾을 수 없음")
        val firstIndex = patchNoteElement
            .children()
            .indexOfFirst { it.className() == HEADER_PRIMARY_CLASS_NAME && it.text() == networkPatchNoteType.patchName } + 1

        var lastIndex = patchNoteElement
            .children()
            .drop(firstIndex + 1)
            .indexOfFirst { it.className() == HEADER_PRIMARY_CLASS_NAME }
            .takeIf { it != -1 }

        if (lastIndex != null) {
            lastIndex += firstIndex + 1
        } else {
            lastIndex = patchNoteElement.children().size
        }

        patchNoteElement.children().subList(firstIndex, lastIndex)
            .mapNotNull { element ->
                val imageElement = element.getElementsByClass(PATCH_FILTER_CHECK_CLASS_NAME)

                val title = element.getElementsByClass(PATCH_TITLE_CLASS_NAME).firstOrNull()?.text() ?: return@mapNotNull null
                val imageUrl = imageElement.firstOrNull()?.firstElementChild()?.getElementsByTag("img")?.attr("src") ?: return@mapNotNull null

                val summary = when (networkPatchNoteType) {
                    Champion -> element.getElementsByClass(PATCH_SUMMARY_CLASS_NAME).first()!!.text()
                    Item, Rune -> element.select("li").joinToString(separator = "\n", transform = Element::text)
                }

                NetworkPatchNoteData(
                    title = title,
                    imageUrl = imageUrl,
                    summary = summary
                )
            }
    }.onFailure {
        it.printStackTrace()
    }.getOrDefault(emptyList())
}

fun String.getPatchNoteUrl(): String {
    val (major1, minor1, _) = this.split('.').map { it.toInt() }
    if (major1 < 10) return ""

    val urlBuilder = StringBuilder("https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-")
    return when {
        major1 == 15 && (1..2).contains(minor1) -> urlBuilder.append("${major1 + 10}-s1-$minor1-notes/")
        major1 == 15 && 3 == minor1 -> urlBuilder.append("2025-s1-3-notes/")
        major1 >= 15 -> urlBuilder.append("${major1 + 10}-${minor1.toString().padStart(2, '0')}-notes/")
        else -> urlBuilder.append("$major1-$minor1-notes/")
    }.toString()
}