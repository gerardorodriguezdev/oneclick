package theoneclick.client.core.ui.states.homeScreen

import theoneclick.shared.core.models.entities.DeviceType

data class AddDeviceState(
    val deviceName: String = "",
    val isDeviceNameValid: Boolean? = null,

    val roomName: String = "",
    val isRoomNameValid: Boolean? = null,

    val deviceType: DeviceType = DeviceType.BLIND,

    val isAddDeviceButtonEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val showSuccess: Boolean = false,
)
