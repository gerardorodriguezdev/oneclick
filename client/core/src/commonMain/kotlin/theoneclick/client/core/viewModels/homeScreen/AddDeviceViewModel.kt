package theoneclick.client.core.viewModels.homeScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.repositories.DevicesRepository
import theoneclick.client.core.ui.events.homeScreen.AddDeviceEvent
import theoneclick.client.core.ui.states.homeScreen.AddDeviceState
import theoneclick.shared.core.validators.deviceNameValidator
import theoneclick.shared.core.validators.roomNameValidator

class AddDeviceViewModel(private val devicesRepository: DevicesRepository) : ViewModel() {
    private val _state = mutableStateOf(AddDeviceState())
    val state: State<AddDeviceState> = _state

    private var requestAddDeviceJob: Job? = null

    fun onEvent(event: AddDeviceEvent) {
        when (event) {
            is AddDeviceEvent.DeviceNameChanged -> event.handleDeviceNameChanged()
            is AddDeviceEvent.RoomNameChanged -> event.handleRoomNameChanged()
            is AddDeviceEvent.DeviceTypeChanged -> event.handleDeviceTypeChanged()

            is AddDeviceEvent.AddDeviceButtonClicked -> event.handleAddDeviceButtonClicked()

            is AddDeviceEvent.SuccessShown -> event.handleSuccessShown()
            is AddDeviceEvent.ErrorShown -> event.handleErrorShown()
        }
    }

    private fun AddDeviceEvent.DeviceNameChanged.handleDeviceNameChanged() {
        val isNewDeviceNameValid = deviceNameValidator.isValid(newDeviceName)

        _state.value = _state.value.copy(
            deviceName = newDeviceName,
            isDeviceNameValid = isNewDeviceNameValid,
            isAddDeviceButtonEnabled = isNewDeviceNameValid && roomNameValidator.isValid(_state.value.roomName),
        )
    }

    private fun AddDeviceEvent.RoomNameChanged.handleRoomNameChanged() {
        val isNewRoomNameValid = roomNameValidator.isValid(newRoomName)

        _state.value = _state.value.copy(
            roomName = newRoomName,
            isRoomNameValid = isNewRoomNameValid,
            isAddDeviceButtonEnabled = isNewRoomNameValid && deviceNameValidator.isValid(_state.value.deviceName)
        )
    }

    private fun AddDeviceEvent.DeviceTypeChanged.handleDeviceTypeChanged() {
        _state.value = _state.value.copy(deviceType = newDeviceType)
    }

    private fun AddDeviceEvent.AddDeviceButtonClicked.handleAddDeviceButtonClicked() {
        requestAddDeviceJob?.cancel()

        requestAddDeviceJob = viewModelScope.launch {
            devicesRepository
                .addDevice(
                    deviceName = _state.value.deviceName,
                    room = _state.value.roomName,
                    type = _state.value.deviceType
                )
                .onStart {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        isAddDeviceButtonEnabled = false,
                        showError = false,
                        showSuccess = false,
                    )
                }
                .onCompletion {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAddDeviceButtonEnabled = true,
                    )
                }
                .collect { addDeviceResult ->
                    when (addDeviceResult) {
                        is AddDeviceResult.Success ->
                            _state.value = _state.value.copy(showSuccess = true)

                        is AddDeviceResult.Failure ->
                            _state.value = _state.value.copy(showError = true)
                    }
                }
        }
    }

    private fun AddDeviceEvent.SuccessShown.handleSuccessShown() {
        _state.value = _state.value.copy(showSuccess = false)
    }

    private fun AddDeviceEvent.ErrorShown.handleErrorShown() {
        _state.value = _state.value.copy(showError = false)
    }

    override fun onCleared() {
        super.onCleared()

        requestAddDeviceJob?.cancel()
    }
}
