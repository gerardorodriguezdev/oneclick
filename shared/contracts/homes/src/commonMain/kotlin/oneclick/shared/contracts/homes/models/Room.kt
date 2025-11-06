package oneclick.shared.contracts.homes.models

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.core.models.UniqueList.KeyProvider
import oneclick.shared.contracts.core.models.Uuid

@Serializable
data class Room(
    val id: Uuid,
    val devices: UniqueList<Device>,
) : KeyProvider {
    override val key: String = id.value
}
