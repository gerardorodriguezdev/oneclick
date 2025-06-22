package theoneclick.client.features.home.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import theoneclick.client.features.home.models.Home
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice.UiWaterSensor
import theoneclick.client.features.home.viewModels.HomesListViewModel

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes.toUiHomes(),
        isFullScreenLoading = isFullPageLoading,
        isPaginationLoading = isPaginationLoading,
    )

private fun List<Home>.toUiHomes(): ImmutableList<UiHome> =
    map { it.toUiHome() }.toPersistentList()

private fun Home.toUiHome(): UiHome =
    UiHome(name = name, rooms = rooms.toUiRooms())

private fun List<Home.Room>.toUiRooms(): ImmutableList<UiRoom> =
    map { it.toUiRoom() }.toPersistentList()

private fun Home.Room.toUiRoom(): UiRoom =
    UiRoom(name = name, devices = devices.toUiDevices())

private fun List<Home.Room.Device>.toUiDevices(): ImmutableList<UiDevice> =
    map { it.toUiDevice() }.toImmutableList()

private fun Home.Room.Device.toUiDevice(): UiDevice =
    when (this) {
        is Home.Room.Device.WaterSensor -> UiWaterSensor(
            id = id,
            name = name,
            level = level.toString(),
        )
    }