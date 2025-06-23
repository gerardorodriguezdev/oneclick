package theoneclick.client.features.home.models

import theoneclick.client.features.home.models.Home.Room.Companion.toRoom
import theoneclick.client.features.home.models.Home.Room.Device.Companion.toDevice
import theoneclick.client.features.home.models.Home.Room.Device.WaterSensor.Companion.toWaterSensor
import theoneclick.shared.contracts.core.models.Home

internal class Home private constructor(
    val name: String,
    val rooms: List<Room>,
) {

    companion object {
        fun Home.toHome(): Home =
            Home(
                name = name.value,
                rooms = rooms.toRooms(),
            )

        private fun List<theoneclick.shared.contracts.core.models.Room>.toRooms(): List<Room> = map { it.toRoom() }
    }

    class Room private constructor(
        val name: String,
        val devices: List<Device>,
    ) {

        companion object {
            fun theoneclick.shared.contracts.core.models.Room.toRoom(): Room =
                Room(
                    name = name.value,
                    devices = devices.toDevices()
                )

            private fun List<theoneclick.shared.contracts.core.models.Device>.toDevices(): List<Device> = map { it.toDevice() }
        }

        sealed interface Device {
            val id: String
            val name: String

            class WaterSensor private constructor(
                override val id: String,
                override val name: String,
                val from: Int,
                val to: Int,
                val level: Int,
            ) : Device {

                companion object {
                    fun theoneclick.shared.contracts.core.dtos.DeviceDto.WaterSensor.toWaterSensor(): WaterSensor =
                        WaterSensor(
                            id = id.value,
                            name = name.value,
                            from = range.start.value,
                            to = range.end.value,
                            level = level.value,
                        )
                }
            }

            companion object {
                fun theoneclick.shared.contracts.core.models.Device.toDevice(): Device =
                    when (this) {
                        is theoneclick.shared.contracts.core.dtos.DeviceDto.WaterSensor -> toWaterSensor()
                    }
            }
        }
    }
}