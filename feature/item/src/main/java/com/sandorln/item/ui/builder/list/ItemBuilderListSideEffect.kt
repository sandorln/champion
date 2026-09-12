package com.sandorln.item.ui.builder.list

sealed interface ItemBuilderListSideEffect {
    data class ShowToast(val message: String) : ItemBuilderListSideEffect
    data class ShowError(val messageResId: Int) : ItemBuilderListSideEffect
    data object BuildDeleted : ItemBuilderListSideEffect
}
