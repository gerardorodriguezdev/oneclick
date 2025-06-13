package theoneclick.client.features.home.mapper

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.features.home.ui.screens.HomesListScreenState.Home
import theoneclick.client.features.home.ui.screens.HomesListScreenState.Home.Room
import theoneclick.client.features.home.ui.screens.HomesListScreenState.Home.Room.Device
import theoneclick.client.features.home.ui.screens.HomesListScreenState.Home.Room.Device.WaterSensor
import theoneclick.client.features.home.viewModels.HomesListViewModel
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.HomeDto.RoomDto
import theoneclick.shared.contracts.core.dtos.HomeDto.RoomDto.DeviceDto
import theoneclick.shared.contracts.core.dtos.HomeDto.RoomDto.DeviceDto.WaterSensorDto

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homesDtos.toHomes(),
        isLoading = isLoading,
    )

private fun List<HomeDto>.toHomes(): ImmutableList<Home> =
    map { homeDto -> homeDto.toHome() }.toPersistentList()

private fun HomeDto.toHome(): Home =
    Home(rooms = roomsDtos.toRooms())

private fun List<RoomDto>.toRooms(): ImmutableList<Room> =
    map { roomDto -> roomDto.toRoom() }.toPersistentList()

private fun RoomDto.toRoom(): Room =
    Room(devices = devicesDtos.toDevices())

private fun List<DeviceDto>.toDevices(): ImmutableList<Device> =
    map { deviceDto -> deviceDto.toDevice() }.toImmutableList()

private fun DeviceDto.toDevice(): Device =
    when (this) {
        is WaterSensorDto -> WaterSensor(
            name = name,
            level = level.toString(),
        )
    }