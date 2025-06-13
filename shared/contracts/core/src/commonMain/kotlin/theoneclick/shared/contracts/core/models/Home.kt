package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val rooms: List<Room>,
) {
    @Serializable
    data class Room(
        val name: String,
        val devices: List<Device>,
    ) {
        @Serializable
        sealed interface Device {
            val id: Uuid
            val name: String

            @Serializable
            data class WaterSensor(
                override val id: Uuid,
                override val name: String,
                val level: Int,
            ) : Device
        }
    }
}