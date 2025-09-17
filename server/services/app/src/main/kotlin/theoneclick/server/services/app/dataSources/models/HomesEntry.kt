package theoneclick.server.services.app.dataSources.models

import theoneclick.shared.contracts.core.models.UniqueList
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.contracts.homes.models.Home

data class HomesEntry(
    val userId: Uuid,
    val homes: UniqueList<Home>,
) : UniqueList.KeyProvider {
    override val key: String = userId.value
}