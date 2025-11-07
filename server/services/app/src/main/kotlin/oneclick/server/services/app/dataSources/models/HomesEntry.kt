package oneclick.server.services.app.dataSources.models

import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.core.models.UniqueList.KeyProvider
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.homes.models.Home

internal data class HomesEntry(
    val userId: Uuid,
    val homes: UniqueList<Home>,
) : KeyProvider {
    override val key: String = userId.value
}