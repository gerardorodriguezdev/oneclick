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
import theoneclick.client.core.platform.LoggedDataSource
import theoneclick.client.core.extensions.updateDevice
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.client.core.ui.events.homeScreen.DevicesListEvent
import theoneclick.client.core.ui.states.homeScreen.DevicesListState

class DevicesListViewModel(
    private val loggedDataSource: LoggedDataSource,
) : ViewModel() {

    private val _state = mutableStateOf(DevicesListState())
    val state: State<DevicesListState> = _state

    private var requestDevicesJob: Job? = null

    private var updateDeviceJob: Job? = null

    init {
        requestDevices()
    }

    fun onEvent(event: DevicesListEvent) {
        when (event) {
            is DevicesListEvent.Refresh -> requestDevices()
            is DevicesListEvent.ErrorShown -> event.handleErrorShown()
            is DevicesListEvent.UpdateDevice -> event.handleUpdateDevice()
        }
    }

    private fun requestDevices() {
        requestDevicesJob?.cancel()

        requestDevicesJob = viewModelScope.launch {
            loggedDataSource
                .devices()
                .onStart {
                    _state.value = _state.value.copy(isLoading = true)
                }
                .onCompletion {
                    _state.value = _state.value.copy(isLoading = false)
                }
                .collect { devicesResult ->
                    when (devicesResult) {
                        is DevicesResult.Success -> {
                            _state.value = _state.value.copy(
                                devices = devicesResult.devices.toImmutableList(),
                            )
                        }

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
            loggedDataSource
                .updateDevice(updatedDevice)
                .onStart {
                    // Optimistic approach
                    _state.value = _state.value.copy(
                        devices = _state.value.devices.updateDevice(updatedDevice),
                    )
                }
                .collect { updatedDeviceResult ->
                    when (updatedDeviceResult) {
                        is UpdateDeviceResult.Success -> Unit
                        is UpdateDeviceResult.Failure -> handleUnknownError()
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        requestDevicesJob?.cancel()
    }
}
