package theoneclick.shared.core.models.entities

import kotlinx.serialization.Serializable

sealed interface DeviceFeature {
    interface Openable : DeviceFeature {
        val isOpened: Boolean

        fun toggle(newState: Boolean): Device
    }

    interface Rotable : DeviceFeature {
        val rotation: Int
        val range: Range

        fun rotate(newRotation: Int): Device
    }

    @Serializable
    data class Range(val start: Int, val end: Int) {
        fun isOnRange(value: Int): Boolean = value in toIntRange()

        private fun toIntRange(): IntRange = start..end

        fun toClosedFloatingPointRange(): ClosedFloatingPointRange<Float> {
            return start.toFloat()..end.toFloat()
        }
    }
}
