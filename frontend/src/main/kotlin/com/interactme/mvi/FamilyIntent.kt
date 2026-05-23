package com.interactme.mvi

import com.interactme.data.FamilyField

/// Пользовательские намерения, которые UI отправляет в MVI-store.
sealed interface FamilyIntent {
    data object LoadFamilies : FamilyIntent
    data class SelectFamily(val id: Long?) : FamilyIntent
    data object CreateDraft : FamilyIntent
    data class ChangeTextField(val field: FamilyField, val value: String) : FamilyIntent
    data class ChangeBooleanField(val field: FamilyField, val value: Boolean) : FamilyIntent
    data object SaveDraft : FamilyIntent
    data object DeleteSelected : FamilyIntent
    data object ClearError : FamilyIntent
}