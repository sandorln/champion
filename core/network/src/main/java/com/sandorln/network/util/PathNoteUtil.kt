package com.sandorln.network.util

import com.sandorln.network.model.patchnote.NetworkPatchNoteData
import com.sandorln.network.model.patchnote.NetworkPatchNoteType
import com.sandorln.network.model.patchnote.NetworkPatchNoteType.Champion
import com.sandorln.network.model.patchnote.NetworkPatchNoteType.Item
import org.jsoup.nodes.Element

private const val PATCH_CHANGE_BLOCK_CLASS_NAME = "patch-change-block white-stone accent-before"
private const val PATCH_FILTER_CHECK_CLASS_NAME = "reference-link"

private const val PATCH_TITLE_CLASS_NAME = "change-title"
private const val PATCH_SUMMARY_CLASS_NAME = "summary"

fun Element.toNetworkPatchNoteList(
    networkPatchNoteType: NetworkPatchNoteType
): List<NetworkPatchNoteData> = getElementsByClass(PATCH_CHANGE_BLOCK_CLASS_NAME)
    .filter { element ->
        val pathNoteBlock = element.getElementsByClass(PATCH_FILTER_CHECK_CLASS_NAME)
        val imageElement = pathNoteBlock.firstOrNull()?.firstElementChild()?.getElementsByTag("img")

        val isOldPatch = pathNoteBlock.attr("href").lowercase().contains(networkPatchNoteType.name.lowercase())
        val isNewPatch: Boolean =
            when (networkPatchNoteType) {
                Champion -> imageElement?.attr("src")?.lowercase()?.contains(networkPatchNoteType.name.lowercase()) == true
                Item -> imageElement?.attr("src")?.lowercase()?.contains(networkPatchNoteType.name.lowercase()) == true ||
                        imageElement?.attr("src")?.lowercase()?.contains("https://cmsassets.rgpub.io/sanity/images") == true
            }

        isOldPatch || isNewPatch
    }
    .mapNotNull { element ->
        runCatching {
            val imageElement = element.getElementsByClass(PATCH_FILTER_CHECK_CLASS_NAME)

            val title = element.getElementsByClass(PATCH_TITLE_CLASS_NAME).first()!!.text()
            val imageUrl = imageElement.first()!!.firstElementChild()!!.getElementsByTag("img").attr("src")

            val summary = when (networkPatchNoteType) {
                Champion -> element.getElementsByClass(PATCH_SUMMARY_CLASS_NAME).first()!!.text()
                Item -> element.select("li").joinToString(separator = "\n", transform = Element::text)
            }

            NetworkPatchNoteData(
                title = title,
                imageUrl = imageUrl,
                summary = summary
            )
        }.getOrNull()
    }