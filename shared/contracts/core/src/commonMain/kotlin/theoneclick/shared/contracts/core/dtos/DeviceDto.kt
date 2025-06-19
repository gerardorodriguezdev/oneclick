package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
sealed interface DeviceDto {
    val id: UuidDto
    val name: DeviceNameDto

    @Serializable
    class WaterSensorDto private constructor(
        override val id: UuidDto,
        override val name: DeviceNameDto,
        val range: PositiveIntRangeDto,
        val level: NonNegativeIntDto,
    ) : DeviceDto {

        init {
            require(isValid(level = level, range = range)) { ERROR_MESSAGE }
        }

        companion object {
            private const val ERROR_MESSAGE = "Level not in range"

            private fun isValid(level: NonNegativeIntDto, range: PositiveIntRangeDto): Boolean = range.inRange(level)

            fun waterSensorDto(
                id: UuidDto,
                name: DeviceNameDto,
                range: PositiveIntRangeDto,
                level: NonNegativeIntDto
            ): WaterSensorDto? =
                if (isValid(level, range)) {
                    WaterSensorDto(
                        id = id,
                        name = name,
                        range = range,
                        level = level,
                    )
                } else {
                    null
                }

            fun unsafe(
                id: UuidDto,
                name: DeviceNameDto,
                range: PositiveIntRangeDto,
                level: NonNegativeIntDto
            ): WaterSensorDto =
                WaterSensorDto(
                    id = id,
                    name = name,
                    range = range,
                    level = level,
                )
        }
    }
}