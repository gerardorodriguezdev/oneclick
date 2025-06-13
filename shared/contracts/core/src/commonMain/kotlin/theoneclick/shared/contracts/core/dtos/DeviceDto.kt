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
    ) : DeviceDto {

        init {
            require(isValid(level, range)) { ERROR_MESSAGE }
        }

        companion object {
            private const val ERROR_MESSAGE = "Level not in range"

            fun isValid(level: PositiveIntDto, range: PositiveIntRangeDto): Boolean = range.inRange(level)

            fun waterSensorDto(
                id: UuidDto,
                nameDto: DeviceNameDto,
                rangeDto: PositiveIntRangeDto,
                level: PositiveIntDto
            ): WaterSensorDto? =
                if (isValid(level, rangeDto)) {
                    WaterSensorDto(id, nameDto, rangeDto, level)
                } else {
                    null
                }
        }
    }
}