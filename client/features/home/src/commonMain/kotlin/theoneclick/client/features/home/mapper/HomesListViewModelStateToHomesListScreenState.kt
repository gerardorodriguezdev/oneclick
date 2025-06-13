package theoneclick.client.features.home.mapper

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import theoneclick.client.features.home.ui.screens.HomesListScreenState
import theoneclick.client.features.home.viewModels.HomesListViewModel
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.Home.Room
import theoneclick.shared.contracts.core.models.Home.Room.Device
import theoneclick.client.features.home.ui.screens.HomesListScreenState.Home as SHome
import theoneclick.client.features.home.ui.screens.HomesListScreenState.Home.Room as SRoom
import theoneclick.client.features.home.ui.screens.HomesListScreenState.Home.Room.Device as SDevice

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes.toHomes(),
        isLoading = isLoading,
    )

private fun List<Home>.toHomes(): ImmutableList<SHome> =
    map { home ->
        home.toHome()
    }.toPersistentList()

private fun Home.toHome(): SHome =
    SHome(
        rooms = rooms.toRooms(),
    )

private fun List<Room>.toRooms(): ImmutableList<SRoom> =
    map { room ->
        room.toRoom()
    }.toPersistentList()

private fun Room.toRoom(): SRoom =
    SRoom(
        devices = devices.toDevices(),
    )

private fun List<Device>.toDevices(): ImmutableList<SDevice> =
    map { device ->
        device.toDevice()
    }.toImmutableList()

private fun Device.toDevice(): SDevice =
    when (this) {
        is Device.WaterSensor -> SDevice.WaterSensor(
            name = name,
            level = level.toString(),
        )
    }