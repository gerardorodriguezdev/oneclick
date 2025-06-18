package theoneclick.client.features.home.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice.UiWaterSensor
import theoneclick.client.features.home.viewModels.HomesListViewModel
import theoneclick.shared.contracts.core.dtos.DeviceDto
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.RoomDto

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes?.homes?.toUiHomes() ?: persistentListOf(),
        isLoading = isLoading,
    )

private fun List<HomeDto>.toUiHomes(): ImmutableList<UiHome> =
    map { it.toUiHome() }.toPersistentList()

private fun HomeDto.toUiHome(): UiHome =
    UiHome(name = name.value, rooms = rooms.toUiRooms())

private fun List<RoomDto>.toUiRooms(): ImmutableList<UiRoom> =
    map { it.toUiRoom() }.toPersistentList()

private fun RoomDto.toUiRoom(): UiRoom =
    UiRoom(name = name.value, devices = devices.toUiDevices())

private fun List<DeviceDto>.toUiDevices(): ImmutableList<UiDevice> =
    map { it.toUiDevice() }.toImmutableList()

private fun DeviceDto.toUiDevice(): UiDevice =
    when (this) {
        is DeviceDto.WaterSensorDto -> UiWaterSensor(
            id = id.value,
            name = name.value,
            level = level.toString(),
        )
    }