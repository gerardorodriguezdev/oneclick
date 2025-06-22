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
import theoneclick.client.features.home.models.Home
import theoneclick.client.features.home.models.HomesResult
import theoneclick.client.features.home.repositories.HomesRepository
import theoneclick.client.features.home.ui.screens.HomesListEvent
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.shared.notifications.NotificationsController

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
            homesRepository.homesEntry.collect { homes ->
                homesListViewModelState.value = homesListViewModelState.value.copy(
                    pageIndex = homes?.pageIndex ?: 0,
                    homes = homes?.homes ?: emptyList(),
                    canRequestMore = homes?.canRequestMore ?: true,
                )
            }
        }

        refreshHomes()
    }

    fun onEvent(event: HomesListEvent) {
        when (event) {
            is HomesListEvent.Refresh -> refreshHomes()
            is HomesListEvent.EndReached -> handleEndReached()
        }
    }

    private fun refreshHomes() {
        requestHomesJob?.cancel()

        requestHomesJob = viewModelScope.launch {
            homesRepository
                .refreshHomes()
                .onStart {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isFullPageLoading = true)
                }
                .onCompletion {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isFullPageLoading = false)
                }
                .collect { homesResult ->
                    when (homesResult) {
                        is HomesResult.Success -> Unit // Observed at the start
                        is HomesResult.Error -> handleUnknownError()
                    }
                }
        }
    }

    private fun handleEndReached() {
        if (!homesListViewModelState.value.canRequestMore) return

        requestHomesJob?.cancel()

        requestHomesJob = viewModelScope.launch {
            homesRepository
                .requestMoreHomes(
                    currentPageIndex = homesListViewModelState.value.pageIndex
                )
                .onStart {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isPaginationLoading = true)
                }
                .onCompletion {
                    homesListViewModelState.value = homesListViewModelState.value.copy(isPaginationLoading = false)
                }
                .collect { homesResult ->
                    when (homesResult) {
                        is HomesResult.Success -> Unit // Observed at the start
                        is HomesResult.Error -> handleUnknownError()
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

    override fun onCleared() {
        super.onCleared()

        requestHomesJob?.cancel()
    }

    data class HomesListViewModelState(
        val homes: List<Home> = emptyList(),
        val pageIndex: Int = 0,
        val canRequestMore: Boolean = true,
        val isFullPageLoading: Boolean = false,
        val isPaginationLoading: Boolean = false,
    )
}
