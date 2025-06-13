package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
sealed interface DeviceDto {
    val id: UuidDto
    val name: DeviceNameDto

    @Serializable
    data class WaterSensorDto(
        override val id: UuidDto,
        override val name: DeviceNameDto,
        val range: PositiveIntRangeDto,
        val level: PositiveIntDto,
    ) : DeviceDto

    //TODO: Validate value inside range
}