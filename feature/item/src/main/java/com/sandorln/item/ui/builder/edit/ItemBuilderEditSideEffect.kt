package com.sandorln.item.ui.builder.edit

sealed interface ItemBuilderEditSideEffect {
    data class ShowToast(val message: String) : ItemBuilderEditSideEffect
    data class ShowError(val messageResId: Int) : ItemBuilderEditSideEffect
    data object SaveSuccess : ItemBuilderEditSideEffect
}
