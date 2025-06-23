package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.PositiveLong
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
class HomesEntry private constructor(
    val userId: Uuid,
    val lastModified: PositiveLong,
    val homes: List<Home>, //TODO: Replace for unique list
) {
    init {
        require(isValid(homes)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated homes name"

        fun isValid(homes: List<Home>): Boolean =
            homes.containsDuplicatesBy { it.name }

        fun homesEntry(
            userId: Uuid,
            lastModified: PositiveLong,
            homes: List<Home>,
        ): HomesEntry? =
            if (isValid(homes)) {
                HomesEntry(userId = userId, lastModified = lastModified, homes = homes)
            } else {
                null
            }

        fun unsafe(
            userId: Uuid,
            lastModified: PositiveLong,
            homes: List<Home>,
        ): HomesEntry =
            HomesEntry(userId = userId, lastModified = lastModified, homes = homes)
    }
}