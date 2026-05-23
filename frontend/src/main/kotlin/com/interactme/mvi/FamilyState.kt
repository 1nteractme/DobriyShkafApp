package com.interactme.mvi

import com.interactme.data.Family

/// Полное состояние экрана администрирования семей.
data class FamilyState(
    val families: List<Family> = emptyList(),
    val selectedId: Long? = null,
    val draft: Family = Family(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
) {
    /// Текущая выбранная семья, найденная по selectedId.
    val selectedFamily: Family?
        get() = families.firstOrNull { it.id == selectedId }
}