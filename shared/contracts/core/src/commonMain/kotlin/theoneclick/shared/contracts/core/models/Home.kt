package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.UniqueList.KeyProvider

@Serializable
class Home(
    val id: Uuid,
    val name: HomeName,
    val rooms: UniqueList<Room>,
) : KeyProvider {
    override val key: String = name.value
}
