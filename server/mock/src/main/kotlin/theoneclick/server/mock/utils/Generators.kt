package theoneclick.server.mock.utils

import theoneclick.shared.contracts.core.models.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid as KUuid

fun mockJwt(): Jwt =
    Jwt.unsafe(
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
    )

fun mockHomes(number: Int): UniqueList<Home> =
    UniqueList.unsafe(
        buildList {
            repeat(number) {
                add(
                    mockHome(
                        name = HomeName.unsafe("H_$it"),
                    )
                )
            }
        }
    )

private fun mockHome(
    name: HomeName = HomeName.unsafe("HomeName"),
    rooms: UniqueList<Room> = mockRooms(name.value, 5),
): Home =
    Home(
        id = mockUuid(),
        name = name,
        rooms = rooms,
    )

private fun mockRooms(parentName: String, number: Int): UniqueList<Room> =
    UniqueList.unsafe(
        buildList {
            repeat(number) {
                add(
                    mockRoom(
                        name = RoomName.unsafe("${parentName}_R_$it"),
                    )
                )
            }
        }
    )

private fun mockRoom(
    name: RoomName = RoomName.unsafe("RoomName"),
    devices: UniqueList<Device> = mockDevices(name.value, 5),
): Room =
    Room(
        id = mockUuid(),
        name = name,
        devices = devices,
    )

private fun mockDevices(parentName: String, number: Int): UniqueList<Device> =
    UniqueList.unsafe(
        buildList {
            repeat(number) {
                add(
                    mockDevice(
                        name = DeviceName.unsafe("${parentName}_D_$it"),
                    )
                )
            }
        }
    )

private fun mockDevice(
    name: DeviceName = DeviceName.unsafe("DeviceName"),
    id: Uuid = mockUuid()
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
