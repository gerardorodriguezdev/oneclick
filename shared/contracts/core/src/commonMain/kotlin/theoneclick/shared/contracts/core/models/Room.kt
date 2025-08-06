package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.UniqueList.KeyProvider

@Serializable
data class Room(
    val id: Uuid,
    val name: RoomName,
    val devices: UniqueList<Device>,
) : KeyProvider {
    override val key: String = name.value
}
