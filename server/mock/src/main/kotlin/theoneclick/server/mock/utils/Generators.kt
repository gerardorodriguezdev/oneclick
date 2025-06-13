package theoneclick.server.mock.utils

import theoneclick.shared.contracts.core.dtos.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun mockHomes(number: Int): List<HomeDto> =
    buildList {
        repeat(number) {
            add(
                mockHome(
                    name = HomeNameDto("H_$it"),
                )
            )
        }
    }

private fun mockHome(
    name: HomeNameDto = HomeNameDto("HomeName"),
    rooms: List<RoomDto> = mockRooms(name.value, 5),
): HomeDto =
    HomeDto(
        name = name,
        roomsDtos = rooms,
    )

private fun mockRooms(parentName: String, number: Int): List<RoomDto> =
    buildList {
        repeat(number) {
            add(
                mockRoom(
                    name = RoomNameDto("${parentName}_R_$it"),
                )
            )
        }
    }

private fun mockRoom(
    name: RoomNameDto = RoomNameDto("RoomName"),
    devices: List<DeviceDto> = mockDevices(name.value, 5),
): RoomDto =
    RoomDto(
        name = name,
        devicesDtos = devices,
    )

private fun mockDevices(parentName: String, number: Int): List<DeviceDto> =
    buildList {
        repeat(number) {
            add(
                mockDevice(
                    nameDto = DeviceNameDto("${parentName}_D_$it"),
                )
            )
        }
    }

private fun mockDevice(
    nameDto: DeviceNameDto = DeviceNameDto("DeviceName"),
    id: UuidDto = mockUuid()
): DeviceDto =
    DeviceDto.WaterSensorDto(
        id = id,
        name = nameDto,
        range = PositiveIntRangeDto(
            start = PositiveIntDto(0),
            end = PositiveIntDto(10),
        ),
        level = PositiveIntDto(1)
    )

@OptIn(ExperimentalUuidApi::class)
private fun mockUuid(): UuidDto = UuidDto(Uuid.random().toHexDashString())