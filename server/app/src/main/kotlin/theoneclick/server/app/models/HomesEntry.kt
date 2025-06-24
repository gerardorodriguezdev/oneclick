package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.PositiveLong
import theoneclick.shared.contracts.core.models.UniqueList
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
class HomesEntry(
    val userId: Uuid,
    val lastModified: PositiveLong,
    val homes: UniqueList<Home>,
) : UniqueList.KeyProvider<String> {
    override val key: String = userId.value
}