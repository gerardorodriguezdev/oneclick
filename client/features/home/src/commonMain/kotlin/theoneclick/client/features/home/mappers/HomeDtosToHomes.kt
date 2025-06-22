package theoneclick.client.features.home.mappers

import theoneclick.client.features.home.models.Home
import theoneclick.shared.contracts.core.dtos.DeviceDto
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.RoomDto

internal fun List<HomeDto>.toHomes(): List<Home> = map { it.toHome() }

private fun HomeDto.toHome(): Home =
    Home(
        name = name.value,
        rooms = rooms.toRooms(),
    )

private fun List<RoomDto>.toRooms(): List<Home.Room> = map { it.toRoom() }

private fun RoomDto.toRoom(): Home.Room =
    Home.Room(
        name = name.value,
        devices = devices.toDevices()
    )

private fun List<DeviceDto>.toDevices(): List<Home.Room.Device> = map { it.toDevice() }

private fun DeviceDto.toDevice(): Home.Room.Device =
    when (this) {
        is DeviceDto.WaterSensorDto -> Home.Room.Device.WaterSensor(
            id = id.value,
            name = name.value,
            from = range.start.value,
            to = range.end.value,
            level = level.value,
        )
    }