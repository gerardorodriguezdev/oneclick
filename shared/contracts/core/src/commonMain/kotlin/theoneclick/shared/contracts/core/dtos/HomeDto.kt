package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class HomeDto(
    val roomsDtos: List<RoomDto>,
) {
    @Serializable
    data class RoomDto(
        val name: String,
        val devicesDtos: List<DeviceDto>,
    ) {
        @Serializable
        sealed interface DeviceDto {
            val id: String
            val name: String

            @Serializable
            data class WaterSensorDto(
                override val id: String,
                override val name: String,
                val level: Int,
            ) : DeviceDto
        }
    }
}