package com.sandorln.network.util

import com.sandorln.network.model.champion.NetworkChampionPatchNote
import org.jsoup.nodes.Element

fun Element.toNetworkChampionPatchNoteList(): List<NetworkChampionPatchNote> = getElementsByClass("patch-change-block white-stone accent-before")
    .filter { element -> element.getElementsByClass("reference-link").attr("href").contains("champions") }
    .mapNotNull { element ->
        runCatching {
            val title = element.getElementsByClass("change-title").first()!!.text()
            val summary = element.getElementsByClass("summary").first()!!.text()
            val image = element.getElementsByClass("reference-link").first()!!.firstElementChild()!!.getElementsByTag("img").attr("src")
            val detailPathStory = element.getElementsByClass("blockquote context").text()
            NetworkChampionPatchNote(
                title = title,
                image = image,
                summary = summary,
                detailPathStory = detailPathStory
            )
        }.getOrNull()
    }