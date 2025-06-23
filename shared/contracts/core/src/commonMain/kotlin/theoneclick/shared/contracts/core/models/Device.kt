package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable

@Serializable
sealed interface Device {
    val id: Uuid
    val name: DeviceName

    @Serializable
    class WaterSensor private constructor(
        override val id: Uuid,
        override val name: DeviceName,
        val range: PositiveIntRange,
        val level: NonNegativeInt,
    ) : Device {

        init {
            require(isValid(level = level, range = range)) { ERROR_MESSAGE }
        }

        companion object Companion {
            private const val ERROR_MESSAGE = "Level not in range"

            private fun isValid(level: NonNegativeInt, range: PositiveIntRange): Boolean = range.inRange(level)

            fun waterSensor(
                id: Uuid,
                name: DeviceName,
                range: PositiveIntRange,
                level: NonNegativeInt
            ): WaterSensor? =
                if (isValid(level, range)) {
                    WaterSensor(
                        id = id,
                        name = name,
                        range = range,
                        level = level,
                    )
                } else {
                    null
                }

            fun unsafe(
                id: Uuid,
                name: DeviceName,
                range: PositiveIntRange,
                level: NonNegativeInt
            ): WaterSensor =
                WaterSensor(
                    id = id,
                    name = name,
                    range = range,
                    level = level,
                )
        }
    }
}