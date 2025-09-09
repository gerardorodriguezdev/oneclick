package theoneclick.shared.contracts.homes.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.UniqueList
import theoneclick.shared.contracts.core.models.UniqueList.KeyProvider
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
data class Home(
    val id: Uuid,
    val name: HomeName,
    val rooms: UniqueList<Room>,
) : KeyProvider {
    override val key: String = name.value
}
