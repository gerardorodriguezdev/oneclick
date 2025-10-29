package oneclick.shared.contracts.homes.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PositiveIntRange
import oneclick.shared.contracts.core.models.UniqueList.KeyProvider
import oneclick.shared.contracts.core.models.Uuid

@Serializable
sealed class Device : KeyProvider {
    abstract val id: Uuid

    override val key: String
        get() = id.value

    @Poko
    @Serializable
    class WaterSensor private constructor(
        override val id: Uuid,
        val range: PositiveIntRange,
        val level: NonNegativeInt,
    ) : Device() {

        init {
            require(isValid(level = level, range = range)) { ERROR_MESSAGE }
        }

        companion object {
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
                        range = range,
                        level = level,
                    )
                } else {
                    null
                }

            fun unsafe(
                id: Uuid,
                range: PositiveIntRange,
                level: NonNegativeInt
            ): WaterSensor =
                WaterSensor(
                    id = id,
                    range = range,
                    level = level,
                )
        }
    }
}
