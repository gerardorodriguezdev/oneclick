package oneclick.client.features.home.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import oneclick.client.features.home.ui.screens.HomesListScreenState
import oneclick.client.features.home.ui.screens.HomesListScreenState.UiHome
import oneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom
import oneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice
import oneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice.UiWaterSensor
import oneclick.client.features.home.viewModels.HomesListViewModel
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.homes.models.Device
import oneclick.shared.contracts.homes.models.Home
import oneclick.shared.contracts.homes.models.Room

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes.toUiHomes(),
        isFullScreenLoading = isFullPageLoading,
        isPaginationLoading = isPaginationLoading,
    )

private fun UniqueList<Home>.toUiHomes(): ImmutableList<UiHome> =
    map { it.toUiHome() }.toPersistentList()

private fun Home.toUiHome(): UiHome =
    UiHome(id = id.value, name = name.value, rooms = rooms.toUiRooms())

private fun UniqueList<Room>.toUiRooms(): ImmutableList<UiRoom> =
    map { it.toUiRoom() }.toPersistentList()

private fun Room.toUiRoom(): UiRoom =
    UiRoom(id = id.value, name = name.value, devices = devices.toUiDevices())

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
