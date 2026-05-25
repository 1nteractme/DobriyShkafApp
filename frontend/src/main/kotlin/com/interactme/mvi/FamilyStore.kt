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
            FamilyIntent.CreateDraft -> reduce { copy(selectedId = null, draft = Family(), error = null, operationError = null) }

            is FamilyIntent.ChangeTextField -> reduce { copy(draft = draft.withField(intent.field, intent.value), operationError = null) }

            is FamilyIntent.ChangeBooleanField -> reduce { copy(draft = draft.withBooleanField(intent.field, intent.value), operationError = null) }

            FamilyIntent.SaveDraft -> saveDraft()
            FamilyIntent.DeleteSelected -> deleteSelected()
            FamilyIntent.ClearError -> reduce { copy(error = null, operationError = null) }
        }
    }

    private fun loadFamilies() {
        reduce { copy(isLoading = true, error = null, operationError = null) }
        executor.execute {
            runCatching {
                val families = apiClient.getAllFamilies().sortedBy { it.familyNumber ?: Int.MAX_VALUE }
                val databaseSizeBytes = runCatching { apiClient.getDatabaseSizeBytes() }.getOrNull()
                families to databaseSizeBytes
            }
                .onSuccess { result ->
                    reduce {
                        val loadedFamilies = result.first
                        val nextSelectedId = selectedId?.takeIf { id -> loadedFamilies.any { it.id == id } }
                        copy(
                            families = loadedFamilies,
                            databaseSizeBytes = result.second,
                            selectedId = nextSelectedId,
                            draft = loadedFamilies.firstOrNull { it.id == nextSelectedId } ?: draft,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    reduce {
                        copy(
                            families = emptyList(),
                            databaseSizeBytes = null,
                            selectedId = null,
                            draft = Family(),
                            isLoading = false,
                            error = throwable.userMessage()
                        )
                    }
                }
        }
    }

    private fun selectFamily(id: Long?) {
        val selected = state.families.firstOrNull { it.id == id }
        reduce {
            copy(
                selectedId = selected?.id,
                draft = selected ?: Family(),
                operationError = null
            )
        }
    }

    private fun saveDraft() {
        val draftToSave = state.draft
        reduce { copy(isSaving = true, operationError = null) }

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
                        isSaving = false,
                        operationError = null
                    )
                }
            }.onFailure { throwable -> reduce { copy(isSaving = false, operationError = throwable.userMessage()) } }
        }
    }

    private fun deleteSelected() {
        val id = state.selectedId ?: return
        reduce { copy(isSaving = true, operationError = null) }

        executor.execute {
            runCatching { apiClient.deleteFamily(id) }
                .onSuccess {
                    reduce {
                        copy(
                            families = families.filterNot { it.id == id },
                            selectedId = null,
                            draft = Family(),
                            isSaving = false,
                            operationError = null
                        )
                    }
                }
                .onFailure { throwable -> reduce { copy(isSaving = false, operationError = throwable.userMessage()) } }
        }
    }

    private fun reduce(block: FamilyState.() -> FamilyState) {
        state = state.block()
        val nextState = state
        SwingUtilities.invokeLater { listeners.forEach { it(nextState) } }
    }

    private fun Throwable.userMessage(): String = message ?: javaClass.simpleName
}