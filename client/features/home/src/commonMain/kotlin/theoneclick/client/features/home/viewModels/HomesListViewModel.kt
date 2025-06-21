package theoneclick.client.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import theoneclick.client.features.home.generated.resources.Res
import theoneclick.client.features.home.generated.resources.homesListScreen_snackbar_unknownError
import theoneclick.client.features.home.mappers.toHomesListScreenState
import theoneclick.client.features.home.models.GenericResult
import theoneclick.client.features.home.repositories.HomesRepository
import theoneclick.client.features.home.ui.screens.HomesListEvent
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.shared.notifications.NotificationsController
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto

@Inject
internal class HomesListViewModel(
    private val homesRepository: HomesRepository,
    private val notificationsController: NotificationsController,
) : ViewModel() {

    private val homesListViewModelState = MutableStateFlow(HomesListViewModelState())
    val homesListScreenState: StateFlow<HomesListScreenState> =
        homesListViewModelState
            .map(HomesListViewModelState::toHomesListScreenState)
            .stateIn(viewModelScope, SharingStarted.Eagerly, HomesListScreenState())

    private var requestHomesJob: Job? = null

    init {
        viewModelScope.launch {
            homesRepository.pagination.collect { pagination ->
                pagination?.let {
                    homesListViewModelState.value = homesListViewModelState.value.copy(
                        homes = pagination.value,
                    )
                }
            }
        }

        refreshHomes()
    }

    fun onEvent(event: HomesListEvent) {
        when (event) {
            is HomesListEvent.Refresh -> refreshHomes()
            is HomesListEvent.EndReached -> homes()
        }
    }

    private fun refreshHomes() {
        requestHomesJob?.cancel()

        requestHomesJob = viewModelScope.launch {
            homesRepository
                .refreshHomes()
                .onStart {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isLoading = true)
                }
                .onCompletion {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isLoading = false)
                }
                .collect { homesResult ->
                    when (homesResult) {
                        is GenericResult.Success -> Unit // Observed at the start
                        is GenericResult.Error -> handleUnknownError()
                    }
                }
        }
    }

    private fun homes() {
        if (!canRequestMoreHomes()) return

        requestHomesJob?.cancel()

        requestHomesJob = viewModelScope.launch {
            homesRepository
                .homes(
                    pageSize = HomesRepository.defaultPageSize,
                    currentPageIndex = homesListViewModelState.value.pageIndex
                )
                .onStart {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isLoading = true)
                }
                .onCompletion {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isLoading = false)
                }
                .collect { homesResult ->
                    when (homesResult) {
                        is GenericResult.Success -> Unit // Observed at the start
                        is GenericResult.Error -> handleUnknownError()
                    }
                }
        }
    }

    private suspend fun handleUnknownError() {
        notificationsController.showErrorNotification(
            getString(
                Res.string.homesListScreen_snackbar_unknownError
            )
        )
    }

    private fun canRequestMoreHomes(): Boolean {
        val currentState = homesListViewModelState.value
        val currentPageIndex = currentState.pageIndex
        val currentTotalPages = currentState.totalPages ?: return true
        return currentPageIndex.value < (currentTotalPages.value - 1)
    }

    override fun onCleared() {
        super.onCleared()

        requestHomesJob?.cancel()
    }

    data class HomesListViewModelState(
        val homes: List<HomeDto> = emptyList(),
        val pageIndex: NonNegativeIntDto = NonNegativeIntDto.zero,
        val totalPages: PositiveIntDto? = null,
        val isLoading: Boolean = false,
    )
}
