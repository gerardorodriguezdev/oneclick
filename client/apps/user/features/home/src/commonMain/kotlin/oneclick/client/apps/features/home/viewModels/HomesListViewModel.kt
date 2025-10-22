package oneclick.client.apps.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import oneclick.client.apps.features.home.mappers.toHomesListScreenState
import oneclick.client.apps.features.home.models.HomesResult
import oneclick.client.apps.features.home.repositories.HomesRepository
import oneclick.client.apps.features.home.ui.screens.HomesListEvent
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState
import oneclick.client.apps.user.features.home.generated.resources.Res
import oneclick.client.apps.user.features.home.generated.resources.homesListScreen_snackbar_unknownError
import oneclick.client.shared.notifications.NotificationsController
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.core.models.UniqueList.Companion.emptyUniqueList
import oneclick.shared.contracts.homes.models.Home
import org.jetbrains.compose.resources.getString

@Inject
internal class HomesListViewModel(
    private val homesRepository: HomesRepository,
    private val notificationsController: NotificationsController,
) : ViewModel() {

    private val homesListViewModelState = MutableStateFlow(HomesListViewModelState())
    val homesListScreenState: StateFlow<HomesListScreenState> =
        homesListViewModelState
            .map(HomesListViewModelState::toHomesListScreenState)
            .stateIn(
                viewModelScope, SharingStarted.Eagerly,
                HomesListScreenState()
            )

    private var requestHomesJob: Job? = null

    init {
        viewModelScope.launch {
            homesRepository.homesEntry.collect { homeEntry ->
                homesListViewModelState.value = homesListViewModelState.value.copy(
                    homes = homeEntry?.homes ?: emptyUniqueList(),
                    canRequestMore = homeEntry?.canRequestMore ?: true,
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
            homesListViewModelState.value = homesListViewModelState.value.copy(isFullPageLoading = true)

            val homesResult = homesRepository.refreshHomes()
            when (homesResult) {
                is HomesResult.Success -> Unit // Observed at the start
                is HomesResult.Error -> handleUnknownError()
            }

            homesListViewModelState.value = homesListViewModelState.value.copy(isFullPageLoading = false)
        }
    }

    private fun handleEndReached() {
        if (!homesListViewModelState.value.canRequestMore) return

        requestHomesJob?.cancel()

        requestHomesJob = viewModelScope.launch {
            homesListViewModelState.value = homesListViewModelState.value.copy(isPaginationLoading = true)

            val homesResult = homesRepository.requestMoreHomes()
            when (homesResult) {
                is HomesResult.Success -> Unit // Observed at the start
                is HomesResult.Error -> handleUnknownError()
            }

            homesListViewModelState.value = homesListViewModelState.value.copy(isPaginationLoading = false)
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
        val homes: UniqueList<Home> = emptyUniqueList(),
        val canRequestMore: Boolean = true,
        val isFullPageLoading: Boolean = false,
        val isPaginationLoading: Boolean = false,
    )
}
