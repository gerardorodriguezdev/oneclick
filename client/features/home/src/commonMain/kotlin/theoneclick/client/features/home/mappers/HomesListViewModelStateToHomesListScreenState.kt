package theoneclick.client.features.home.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import theoneclick.client.features.home.models.entities.Home
import theoneclick.client.features.home.models.entities.Home.Room
import theoneclick.client.features.home.models.entities.Home.Room.Device
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice.UiWaterSensor
import theoneclick.client.features.home.viewModels.HomesListViewModel

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes.toUiHomes(),
        isLoading = isLoading,
    )

private fun List<Home>.toUiHomes(): ImmutableList<UiHome> =
    map { it.toUiHome() }.toPersistentList()

private fun Home.toUiHome(): UiHome =
    UiHome(name = name, rooms = rooms.toUiRooms())

private fun List<Room>.toUiRooms(): ImmutableList<UiRoom> =
    map { it.toUiRoom() }.toPersistentList()

private fun Room.toUiRoom(): UiRoom =
    UiRoom(name = name, devices = devices.toUiDevices())

private fun List<Device>.toUiDevices(): ImmutableList<UiDevice> =
    map { it.toUiDevice() }.toImmutableList()

private fun Device.toUiDevice(): UiDevice =
    when (this) {
        is Device.WaterSensor -> UiWaterSensor(
            id = id,
            name = name,
            level = level.toString(),
        )
    }