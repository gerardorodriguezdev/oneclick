package oneclick.server.services.mock.utils

import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PositiveIntRange
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.homes.models.Device
import oneclick.shared.contracts.homes.models.Home
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid as KUuid

internal fun mockJwt(): Jwt =
    Jwt.unsafe(
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
    )

internal fun mockHomes(number: Int): UniqueList<Home> =
    UniqueList.unsafe(
        buildList {
            repeat(number) {
                add(
                    mockHome()
                )
            }
        }
    )

private fun mockHome(
    devices: UniqueList<Device> = mockDevices(5),
): Home =
    Home(
        id = mockUuid(),
        devices = devices,
    )

private fun mockDevices(number: Int): UniqueList<Device> =
    UniqueList.unsafe(
        buildList {
            repeat(number) {
                add(
                    mockDevice()
                )
            }
        }
    )

private fun mockDevice(id: Uuid = mockUuid()): Device =
    Device.WaterSensor.unsafe(
        id = id,
        range = PositiveIntRange.unsafe(
            start = NonNegativeInt.unsafe(0),
            end = NonNegativeInt.unsafe(10),
        ),
        level = NonNegativeInt.unsafe(1)
    )

@OptIn(ExperimentalUuidApi::class)
private fun mockUuid(): Uuid = Uuid.unsafe(KUuid.random().toHexDashString())
