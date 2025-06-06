package theoneclick.client.features.home.ui.events

import theoneclick.shared.core.models.entities.DeviceType

internal sealed interface AddDeviceEvent {
    data class DeviceNameChanged(val newDeviceName: String) : AddDeviceEvent
    data class RoomNameChanged(val newRoomName: String) : AddDeviceEvent
    data class DeviceTypeChanged(val newDeviceType: DeviceType) : AddDeviceEvent
    data object AddDeviceButtonClicked : AddDeviceEvent
}
