package theoneclick.client.core.viewModels.homeScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.client.core.repositories.DevicesRepository
import theoneclick.client.core.ui.events.homeScreen.DevicesListEvent
import theoneclick.client.core.ui.states.homeScreen.DevicesListState

class DevicesListViewModel(
    private val devicesRepository: DevicesRepository,
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
            is DevicesListEvent.ErrorShown -> event.handleErrorShown()
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
                        is DevicesResult.Failure -> handleUnknownError()
                    }
                }
        }
    }

    private fun handleUnknownError() {
        _state.value = _state.value.copy(showError = true)
    }

    private fun DevicesListEvent.ErrorShown.handleErrorShown() {
        _state.value = _state.value.copy(showError = false)
    }

    private fun DevicesListEvent.UpdateDevice.handleUpdateDevice() {
        updateDeviceJob?.cancel()

        updateDeviceJob = viewModelScope.launch {
            devicesRepository
                .updateDevice(updatedDevice)
                .collect { updatedDeviceResult ->
                    when (updatedDeviceResult) {
                        is UpdateDeviceResult.Success -> Unit // Observed at the start
                        is UpdateDeviceResult.Failure -> handleUnknownError()
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        requestDevicesJob?.cancel()
        updateDeviceJob?.cancel()
    }
}
