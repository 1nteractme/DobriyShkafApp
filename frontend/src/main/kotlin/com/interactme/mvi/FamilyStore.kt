package com.interactme.mvi

import com.interactme.data.Family
import com.interactme.data.withBooleanField
import com.interactme.data.withField
import com.interactme.network.FamilyApiClient
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import javax.swing.SwingUtilities

/// MVI-store: принимает intent, меняет state и выполняет сетевые эффекты.
class FamilyStore(private val apiClient: FamilyApiClient = FamilyApiClient())
{
    private val listeners = CopyOnWriteArrayList<(FamilyState) -> Unit>()

    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "family-store-worker").apply { isDaemon = true }
    }

    /// Текущее состояние экрана.
    var state: FamilyState = FamilyState()
        private set

    /// Подписывает UI на изменения состояния.
    fun subscribe(listener: (FamilyState) -> Unit): () -> Unit {
        listeners += listener
        listener(state)
        return { listeners -= listener }
    }

    /// Обрабатывает intent от UI.
    fun dispatch(intent: FamilyIntent) {
        when (intent) {
            FamilyIntent.LoadFamilies -> loadFamilies()
            is FamilyIntent.SelectFamily -> selectFamily(intent.id)
            FamilyIntent.CreateDraft -> reduce { copy(selectedId = null, draft = Family(), error = null) }

            is FamilyIntent.ChangeTextField -> reduce { copy(draft = draft.withField(intent.field, intent.value), error = null) }

            is FamilyIntent.ChangeBooleanField -> reduce { copy(draft = draft.withBooleanField(intent.field, intent.value), error = null) }

            FamilyIntent.SaveDraft -> saveDraft()
            FamilyIntent.DeleteSelected -> deleteSelected()
            FamilyIntent.ClearError -> reduce { copy(error = null) }
        }
    }

    private fun loadFamilies() {
        reduce { copy(isLoading = true, error = null) }
        executor.execute {
            runCatching { apiClient.getAllFamilies().sortedBy { it.familyNumber ?: Int.MAX_VALUE } }
                .onSuccess { families ->
                    reduce {
                        val nextSelectedId = selectedId?.takeIf { id -> families.any { it.id == id } }
                        copy(
                            families = families,
                            selectedId = nextSelectedId,
                            draft = families.firstOrNull { it.id == nextSelectedId } ?: draft,
                            isLoading = false
                        )
                    }
                }
                .onFailure { throwable ->
                    reduce { copy(isLoading = false, error = throwable.userMessage()) }
                }
        }
    }

    private fun selectFamily(id: Long?) {
        val selected = state.families.firstOrNull { it.id == id }
        reduce {
            copy(
                selectedId = selected?.id,
                draft = selected ?: Family(),
                error = null
            )
        }
    }

    private fun saveDraft() {
        val draftToSave = state.draft
        reduce { copy(isSaving = true, error = null) }

        executor.execute {
            runCatching {
                if (draftToSave.id == null) apiClient.createFamily(draftToSave)
                else apiClient.updateFamily(draftToSave)
            }.onSuccess { saved ->
                val updatedFamilies = state.families
                    .filterNot { it.id == saved.id }
                    .plus(saved)
                    .sortedBy { it.familyNumber ?: Int.MAX_VALUE }
                reduce {
                    copy(
                        families = updatedFamilies,
                        selectedId = saved.id,
                        draft = saved,
                        isSaving = false
                    )
                }
            }.onFailure { throwable -> reduce { copy(isSaving = false, error = throwable.userMessage()) } }
        }
    }

    private fun deleteSelected() {
        val id = state.selectedId ?: return
        reduce { copy(isSaving = true, error = null) }

        executor.execute {
            runCatching { apiClient.deleteFamily(id) }
                .onSuccess {
                    reduce {
                        copy(
                            families = families.filterNot { it.id == id },
                            selectedId = null,
                            draft = Family(),
                            isSaving = false
                        )
                    }
                }
                .onFailure { throwable -> reduce { copy(isSaving = false, error = throwable.userMessage()) } }
        }
    }

    private fun reduce(block: FamilyState.() -> FamilyState) {
        state = state.block()
        val nextState = state
        SwingUtilities.invokeLater { listeners.forEach { it(nextState) } }
    }

    private fun Throwable.userMessage(): String = message ?: javaClass.simpleName
}