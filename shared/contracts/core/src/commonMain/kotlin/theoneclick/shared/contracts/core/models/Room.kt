package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.UniqueList.KeyProvider

@Serializable
class Room private constructor(
    val name: RoomName,
    val devices: UniqueList<Device>,
) : KeyProvider<String> {
    override val key: String = name.value
}