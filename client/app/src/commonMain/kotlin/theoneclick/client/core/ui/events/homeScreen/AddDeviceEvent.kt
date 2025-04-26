package theoneclick.client.core.ui.events.homeScreen

import theoneclick.shared.core.models.entities.DeviceType

sealed interface AddDeviceEvent {
    data class DeviceNameChanged(val newDeviceName: String) : AddDeviceEvent
    data class RoomNameChanged(val newRoomName: String) : AddDeviceEvent
    data class DeviceTypeChanged(val newDeviceType: DeviceType) : AddDeviceEvent
    data object AddDeviceButtonClicked : AddDeviceEvent
    data object SuccessShown : AddDeviceEvent
    data object ErrorShown : AddDeviceEvent
}
