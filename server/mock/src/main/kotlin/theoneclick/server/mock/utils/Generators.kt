package theoneclick.server.mock.utils

import theoneclick.shared.contracts.core.dtos.*
import theoneclick.shared.contracts.core.models.Device
import theoneclick.shared.contracts.core.models.DeviceName
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.HomeName
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveIntRange
import theoneclick.shared.contracts.core.models.Room
import theoneclick.shared.contracts.core.models.RoomName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun mockHomes(number: Int): List<Home> =
    buildList {
        repeat(number) {
            add(
                mockHome(
                    name = HomeName.unsafe("H_$it"),
                )
            )
        }
    }

private fun mockHome(
    name: HomeName = HomeName.unsafe("HomeName"),
    rooms: List<Room> = mockRooms(name.value, 5),
): Home =
    Home.unsafe(
        name = name,
        rooms = rooms,
    )

private fun mockRooms(parentName: String, number: Int): List<Room> =
    buildList {
        repeat(number) {
            add(
                mockRoom(
                    name = RoomName.unsafe("${parentName}_R_$it"),
                )
            )
        }
    }

private fun mockRoom(
    name: RoomName = RoomName.unsafe("RoomName"),
    devices: List<Device> = mockDevices(name.value, 5),
): Room =
    Room.unsafe(
        name = name,
        devices = devices,
    )

private fun mockDevices(parentName: String, number: Int): List<Device> =
    buildList {
        repeat(number) {
            add(
                mockDevice(
                    nameDto = DeviceName.unsafe("${parentName}_D_$it"),
                )
            )
        }
    }

private fun mockDevice(
    nameDto: DeviceName = DeviceName.unsafe("DeviceName"),
    id: theoneclick.shared.contracts.core.models.Uuid = mockUuid()
): Device =
    Device.WaterSensor.unsafe(
        id = id,
        name = nameDto,
        range = PositiveIntRange.unsafe(
            start = NonNegativeInt.unsafe(0),
            end = NonNegativeInt.unsafe(10),
        ),
        level = NonNegativeInt.unsafe(1)
    )

@OptIn(ExperimentalUuidApi::class)
private fun mockUuid(): theoneclick.shared.contracts.core.models.Uuid = Uuid.unsafe(Uuid.random().toHexDashString())