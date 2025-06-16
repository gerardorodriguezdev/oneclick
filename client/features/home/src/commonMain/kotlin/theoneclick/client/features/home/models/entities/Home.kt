package theoneclick.client.features.home.models.entities

data class Home(
    val name: String,
    val rooms: List<Room>,
) {
    data class Room(
        val name: String,
        val devices: List<Device>,
    ) {
        sealed interface Device {
            val id: String
            val name: String

            data class WaterSensor(
                override val id: String,
                override val name: String,
                val level: Int,
            ) : Device
        }
    }
}