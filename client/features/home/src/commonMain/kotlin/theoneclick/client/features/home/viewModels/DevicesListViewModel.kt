package theoneclick.client.features.home.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import theoneclick.client.features.home.generated.resources.Res
import theoneclick.client.features.home.generated.resources.devicesListScreen_snackbar_unknownError
import theoneclick.client.features.home.models.results.DevicesResult
import theoneclick.client.features.home.models.results.UpdateDeviceResult
import theoneclick.client.features.home.repositories.DevicesRepository
import theoneclick.client.features.home.states.DevicesListState
import theoneclick.client.features.home.ui.events.DevicesListEvent
import theoneclick.client.shared.notifications.NotificationsController

//TODO: Migrate
@Inject
internal class DevicesListViewModel(
    private val devicesRepository: DevicesRepository,
    private val notificationsController: NotificationsController,
) : ViewModel() {

    private val _state = mutableStateOf(DevicesListState())
    val state: State<DevicesListState> = _state

    private var requestDevicesJob: Job? = null
    private var updateDeviceJob: Job? = null

    init {
        viewModelScope.launch {
            devicesRepository.devices.collect { devices ->
                _state.value = _state.value.copy(
                    devices = devices.toImmutableList(),
                )
            }
        }

        refreshDevices()
    }

    fun onEvent(event: DevicesListEvent) {
        when (event) {
            is DevicesListEvent.Refresh -> refreshDevices()
            is DevicesListEvent.UpdateDevice -> event.handleUpdateDevice()
        }
    }

    private fun refreshDevices() {
        requestDevicesJob?.cancel()

        requestDevicesJob = viewModelScope.launch {
            devicesRepository
                .refreshDevices()
                .onStart {
                    _state.value = _state.value.copy(isLoading = true)
                }
                .onCompletion {
                    _state.value = _state.value.copy(isLoading = false)
                }
                .collect { devicesResult ->
                    when (devicesResult) {
                        is DevicesResult.Success -> Unit // Observed at the start
                        is DevicesResult.Error -> handleUnknownError()
                    }
                }
        }
    }

    private fun DevicesListEvent.UpdateDevice.handleUpdateDevice() {
        updateDeviceJob?.cancel()

        updateDeviceJob = viewModelScope.launch {
            devicesRepository
                .updateDevice(updatedDevice)
                .collect { updatedDeviceResult ->
                    when (updatedDeviceResult) {
                        is UpdateDeviceResult.Success -> Unit // Observed at the start
                        is UpdateDeviceResult.Error -> handleUnknownError()
                    }
                }
        }
    }

    private suspend fun handleUnknownError() {
        notificationsController.showErrorNotification(
            getString(
                Res.string.devicesListScreen_snackbar_unknownError
            )
        )
    }

    override fun onCleared() {
        super.onCleared()

        requestDevicesJob?.cancel()
        updateDeviceJob?.cancel()
    }
}
