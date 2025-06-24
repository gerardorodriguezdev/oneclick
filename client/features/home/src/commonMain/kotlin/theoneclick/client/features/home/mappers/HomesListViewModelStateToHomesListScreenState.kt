package theoneclick.client.features.home.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice.UiWaterSensor
import theoneclick.client.features.home.viewModels.HomesListViewModel
import theoneclick.shared.contracts.core.models.Device
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.Room
import theoneclick.shared.contracts.core.models.UniqueList

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes.toUiHomes(),
        isFullScreenLoading = isFullPageLoading,
        isPaginationLoading = isPaginationLoading,
    )

private fun UniqueList<Home>.toUiHomes(): ImmutableList<UiHome> =
    map { it.toUiHome() }.toPersistentList()

private fun Home.toUiHome(): UiHome =
    UiHome(name = name.value, rooms = rooms.toUiRooms())

private fun UniqueList<Room>.toUiRooms(): ImmutableList<UiRoom> =
    map { it.toUiRoom() }.toPersistentList()

private fun Room.toUiRoom(): UiRoom =
    UiRoom(name = name.value, devices = devices.toUiDevices())

private fun UniqueList<Device>.toUiDevices(): ImmutableList<UiDevice> =
    map { it.toUiDevice() }.toImmutableList()

private fun Device.toUiDevice(): UiDevice =
    when (this) {
        is Device.WaterSensor -> UiWaterSensor(
            id = id.value,
            name = name.value,
            level = level.toString(),
        )
    }