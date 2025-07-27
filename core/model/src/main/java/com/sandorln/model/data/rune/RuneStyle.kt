package com.sandorln.model.data.rune

data class RuneStyle(
    val id: Int,
    val key: String,
    val icon: String,
    val name: String,
    val slots: List<RuneSlot>
)