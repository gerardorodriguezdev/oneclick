package theoneclick.server.mock.utils

import theoneclick.shared.contracts.core.models.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid as KUuid

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
                    name = DeviceName.unsafe("${parentName}_D_$it"),
                )
            )
        }
    }

private fun mockDevice(
    name: DeviceName = DeviceName.unsafe("DeviceName"),
    id: theoneclick.shared.contracts.core.models.Uuid = mockUuid()
): Device =
    Device.WaterSensor.unsafe(
        id = id,
        name = name,
        range = PositiveIntRange.unsafe(
            start = NonNegativeInt.unsafe(0),
            end = NonNegativeInt.unsafe(10),
        ),
        level = NonNegativeInt.unsafe(1)
    )

@OptIn(ExperimentalUuidApi::class)
private fun mockUuid(): Uuid = Uuid.unsafe(KUuid.random().toHexDashString())