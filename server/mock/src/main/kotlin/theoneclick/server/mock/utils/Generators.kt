package theoneclick.server.mock.utils

import theoneclick.shared.contracts.core.dtos.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun mockHomes(number: Int): List<HomeDto> =
    buildList {
        repeat(number) {
            add(
                mockHome(
                    name = HomeNameDto.unsafe("H_$it"),
                )
            )
        }
    }

private fun mockHome(
    name: HomeNameDto = HomeNameDto.unsafe("HomeName"),
    rooms: List<RoomDto> = mockRooms(name.value, 5),
): HomeDto =
    HomeDto.unsafe(
        name = name,
        roomsDtos = rooms,
    )

private fun mockRooms(parentName: String, number: Int): List<RoomDto> =
    buildList {
        repeat(number) {
            add(
                mockRoom(
                    name = RoomNameDto.unsafe("${parentName}_R_$it"),
                )
            )
        }
    }

private fun mockRoom(
    name: RoomNameDto = RoomNameDto.unsafe("RoomName"),
    devices: List<DeviceDto> = mockDevices(name.value, 5),
): RoomDto =
    RoomDto.unsafe(
        name = name,
        devices = devices,
    )

private fun mockDevices(parentName: String, number: Int): List<DeviceDto> =
    buildList {
        repeat(number) {
            add(
                mockDevice(
                    nameDto = DeviceNameDto.unsafe("${parentName}_D_$it"),
                )
            )
        }
    }

private fun mockDevice(
    nameDto: DeviceNameDto = DeviceNameDto.unsafe("DeviceName"),
    id: UuidDto = mockUuid()
): DeviceDto =
    DeviceDto.WaterSensorDto.unsafe(
        id = id,
        name = nameDto,
        range = PositiveIntRangeDto.unsafe(
            start = PositiveIntDto.unsafe(0),
            end = PositiveIntDto.unsafe(10),
        ),
        level = PositiveIntDto.unsafe(1)
    )

@OptIn(ExperimentalUuidApi::class)
private fun mockUuid(): UuidDto = UuidDto.unsafe(Uuid.random().toHexDashString())