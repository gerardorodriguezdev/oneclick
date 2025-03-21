package theoneclick.client.core.ui.previews.providers.screens.homeScreen

import kotlinx.collections.immutable.persistentListOf
import theoneclick.client.core.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.core.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.core.ui.states.homeScreen.DevicesListState
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.Uuid

class DevicesListScreenPreviewModels : PreviewModelProvider<DevicesListState> {
    override val values = sequenceOf(
        lightThemeCompactPreviewModel(description = "Init", model = initState),
        lightThemeCompactPreviewModel(description = "Loading", model = loadingState),
        lightThemeCompactPreviewModel(description = "Error", model = isErrorState),
        lightThemeCompactPreviewModel(description = "Loaded", model = loadedState),

        darkThemeCompactPreviewModel(description = "Init", model = initState),
        darkThemeCompactPreviewModel(description = "Loading", model = loadingState),
        darkThemeCompactPreviewModel(description = "Error", model = isErrorState),
        darkThemeCompactPreviewModel(description = "Loaded", model = loadedState),
    )

    companion object {
        val UUID = Uuid("1")
        const val DEVICE_NAME = "Device name"
        const val ROOM_NAME = "Room name"

        val closedBlind = Device.Blind(
            id = Uuid("1"),
            deviceName = DEVICE_NAME,
            room = ROOM_NAME,
            isOpened = false,
            rotation = 0,
        )
        val openedBlind = Device.Blind(
            id = Uuid("2"),
            deviceName = DEVICE_NAME,
            room = ROOM_NAME,
            isOpened = true,
            rotation = 90,
        )
        val rotatedBlind = Device.Blind(
            id = Uuid("3"),
            deviceName = DEVICE_NAME,
            room = ROOM_NAME,
            isOpened = false,
            rotation = 180,
        )

        val devices = persistentListOf(
            closedBlind,
            openedBlind,
            rotatedBlind,
        )

        val initState = DevicesListState()

        val loadingState = DevicesListState(isLoading = true)

        val isErrorState = DevicesListState(showError = true)

        val loadedState = DevicesListState(
            devices = devices,
        )
    }
}
