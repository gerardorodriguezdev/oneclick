package theoneclick.client.features.home.mappers

import theoneclick.client.features.home.models.results.HomesResult.Success.Home
import theoneclick.client.features.home.models.results.HomesResult.Success.Home.Room
import theoneclick.client.features.home.models.results.HomesResult.Success.Home.Room.Device
import theoneclick.shared.contracts.core.dtos.DeviceDto
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.RoomDto

internal fun List<HomeDto>.toHomes(): List<Home> =
    map { it.toHome() }

private fun HomeDto.toHome(): Home =
    Home(
        name = name.value,
        rooms = rooms.toRooms(),
    )

private fun List<RoomDto>.toRooms(): List<Room> =
    map { it.toRoom() }

private fun RoomDto.toRoom(): Room =
    Room(
        name = name.value,
        devices = devices.toDevices()
    )

private fun List<DeviceDto>.toDevices(): List<Device> =
    map { it.toDevice() }

private fun DeviceDto.toDevice(): Device =
    when (this) {
        is DeviceDto.WaterSensorDto -> Device.WaterSensor(
            id = id.value,
            name = name.value,
            level = level.value,
        )
    }