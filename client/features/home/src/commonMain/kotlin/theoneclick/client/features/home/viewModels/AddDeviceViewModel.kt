package theoneclick.client.features.home.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import theoneclick.client.features.home.generated.resources.Res
import theoneclick.client.features.home.generated.resources.addDeviceScreen_snackbar_deviceAdded
import theoneclick.client.features.home.generated.resources.addDeviceScreen_snackbar_unknownError
import theoneclick.client.features.home.models.results.AddDeviceResult
import theoneclick.client.features.home.repositories.DevicesRepository
import theoneclick.client.features.home.states.AddDeviceState
import theoneclick.client.features.home.ui.events.AddDeviceEvent
import theoneclick.client.shared.notifications.NotificationsController
import theoneclick.shared.core.validators.deviceNameValidator
import theoneclick.shared.core.validators.roomNameValidator

@Inject
internal class AddDeviceViewModel(
    private val devicesRepository: DevicesRepository,
    private val notificationsController: NotificationsController,
) : ViewModel() {
    private val _state = mutableStateOf(AddDeviceState())
    val state: State<AddDeviceState> = _state

    private var requestAddDeviceJob: Job? = null

    fun onEvent(event: AddDeviceEvent) {
        when (event) {
            is AddDeviceEvent.DeviceNameChanged -> event.handleDeviceNameChanged()
            is AddDeviceEvent.RoomNameChanged -> event.handleRoomNameChanged()
            is AddDeviceEvent.DeviceTypeChanged -> event.handleDeviceTypeChanged()

            is AddDeviceEvent.AddDeviceButtonClicked -> handleAddDeviceButtonClicked()
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

    private fun handleAddDeviceButtonClicked() {
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
                        is AddDeviceResult.Success -> {
                            notificationsController.showSuccessNotification(
                                getString(Res.string.addDeviceScreen_snackbar_deviceAdded)
                            )
                        }

                        is AddDeviceResult.Error -> {
                            notificationsController.showErrorNotification(
                                getString(Res.string.addDeviceScreen_snackbar_unknownError)
                            )
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        requestAddDeviceJob?.cancel()
    }
}
