package oneclick.shared.contracts.homes.models

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.core.models.UniqueList.KeyProvider
import oneclick.shared.contracts.core.models.Uuid

@Serializable
data class Home(
    val id: Uuid,
    val name: HomeName,
    val rooms: UniqueList<Room>,
) : KeyProvider {
    override val key: String = name.value
}
