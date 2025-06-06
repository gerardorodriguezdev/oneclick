package theoneclick.client.feature.home.ui.previews.providers

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.feature.home.states.AddDeviceState
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.shared.ui.previews.providers.base.lightThemeCompactPreviewModel

internal class AddDeviceStateProvider : PreviewParameterProvider<PreviewModel<AddDeviceState>> {
    override val values = sequenceOf(
        lightThemeCompactPreviewModel(description = "Init", model = initState),
        lightThemeCompactPreviewModel(description = "InvalidDeviceName", model = invalidDeviceNameState),
        lightThemeCompactPreviewModel(description = "InvalidRoomName", model = invalidRoomNameState),
        lightThemeCompactPreviewModel(description = "Loading", model = loadingState),
        lightThemeCompactPreviewModel(description = "Error", model = errorState),
        lightThemeCompactPreviewModel(description = "Valid", model = validState),
        lightThemeCompactPreviewModel(description = "Success", model = successState),

        darkThemeCompactPreviewModel(description = "Init", model = initState),
        darkThemeCompactPreviewModel(description = "InvalidDeviceName", model = invalidDeviceNameState),
        darkThemeCompactPreviewModel(description = "InvalidRoomName", model = invalidRoomNameState),
        darkThemeCompactPreviewModel(description = "Loading", model = loadingState),
        darkThemeCompactPreviewModel(description = "Error", model = errorState),
        darkThemeCompactPreviewModel(description = "Valid", model = validState),
        darkThemeCompactPreviewModel(description = "Success", model = successState),
    )

    companion object Companion {
        const val DEVICE_NAME = "DeviceName"
        const val ROOM_NAME = "RoomName"

        val initState = AddDeviceState()

        val invalidDeviceNameState = AddDeviceState(
            deviceName = DEVICE_NAME,
            isDeviceNameValid = false,
            roomName = ROOM_NAME,
            isRoomNameValid = true,
        )

        val invalidRoomNameState = AddDeviceState(
            deviceName = DEVICE_NAME,
            isDeviceNameValid = true,
            roomName = ROOM_NAME,
            isRoomNameValid = false,
        )

        val loadingState = AddDeviceState(
            deviceName = DEVICE_NAME,
            isDeviceNameValid = true,
            roomName = ROOM_NAME,
            isRoomNameValid = true,
            isAddDeviceButtonEnabled = false,
            isLoading = true,
        )

        val errorState = AddDeviceState(
            deviceName = DEVICE_NAME,
            isDeviceNameValid = true,
            roomName = ROOM_NAME,
            isRoomNameValid = true,
            isAddDeviceButtonEnabled = true,
            showError = true,
        )

        val validState = AddDeviceState(
            deviceName = DEVICE_NAME,
            isDeviceNameValid = true,
            roomName = ROOM_NAME,
            isRoomNameValid = true,
            isAddDeviceButtonEnabled = true,
        )

        val successState = AddDeviceState(
            deviceName = DEVICE_NAME,
            isDeviceNameValid = true,
            roomName = ROOM_NAME,
            isRoomNameValid = true,
            showSuccess = true,
            isAddDeviceButtonEnabled = true,
        )
    }
}
