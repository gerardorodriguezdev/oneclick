package theoneclick.shared.core.dataSources.models.entities

import kotlinx.serialization.Serializable
import theoneclick.shared.core.dataSources.models.entities.DeviceFeature.Openable
import theoneclick.shared.core.dataSources.models.entities.DeviceFeature.Rotateable

@Serializable
sealed interface Device {
    val id: Uuid
    val deviceName: String
    val room: String

    @Serializable
    data class Blind(
        override val id: Uuid,
        override val deviceName: String,
        override val room: String,
        override val isOpened: Boolean,
        override val rotation: Int,
    ) : Device, Openable, Rotateable {
        override val range: DeviceFeature.Range = blindRange

        override fun toggle(newState: Boolean): Device = copy(isOpened = newState)
        override fun rotate(newRotation: Int): Device = copy(rotation = newRotation)

        companion object {
            val blindRange = DeviceFeature.Range(start = 0, end = 180)
        }
    }
}
