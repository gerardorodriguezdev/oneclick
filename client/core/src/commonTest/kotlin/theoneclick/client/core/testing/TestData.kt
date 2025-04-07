package theoneclick.client.core.testing

import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.Uuid

object TestData {
    const val USERNAME = "Username"
    const val PASSWORD = "Password123"
    const val TOKEN = "Token"
    const val DEVICE_NAME = "device1"
    const val ROOM_NAME = "room1"
    val device = Device.Blind(
        id = Uuid("1"),
        deviceName = DEVICE_NAME,
        room = ROOM_NAME,
        isOpened = false,
        rotation = 0,
    )
    val invalidDevice = Device.Blind(
        id = Uuid(value = "2"),
        deviceName = "DeviceName2!",
        room = "RoomName2!",
        isOpened = false,
        rotation = 181,
    )

    val devices = listOf(
        Device.Blind(
            id = Uuid("1"),
            deviceName = DEVICE_NAME,
            room = ROOM_NAME,
            isOpened = false,
            rotation = 0,
        )
    )
}
