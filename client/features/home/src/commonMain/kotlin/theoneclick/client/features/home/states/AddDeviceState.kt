package theoneclick.client.features.home.states

import theoneclick.shared.core.models.entities.DeviceType

internal data class AddDeviceState(
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
