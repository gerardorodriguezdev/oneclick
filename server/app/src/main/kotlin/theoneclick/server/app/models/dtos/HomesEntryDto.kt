package theoneclick.server.app.models.dtos

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.PositiveLongDto
import theoneclick.shared.contracts.core.dtos.UuidDto

@Serializable
class HomesEntryDto private constructor(
    val userId: UuidDto,
    val lastModified: PositiveLongDto,
    val homes: List<HomeDto>,
) {
    init {
        require(isValid(homes)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated homes name"

        fun isValid(homes: List<HomeDto>): Boolean =
            homes.containsDuplicatesBy { homeDto -> homeDto.name }

        fun homesEntry(
            userId: UuidDto,
            lastModified: PositiveLongDto,
            homes: List<HomeDto>,
        ): HomesEntryDto? =
            if (isValid(homes)) {
                HomesEntryDto(userId = userId, lastModified = lastModified, homes = homes)
            } else {
                null
            }

        fun unsafe(
            userId: UuidDto,
            lastModified: PositiveLongDto,
            homes: List<HomeDto>,
        ): HomesEntryDto =
            HomesEntryDto(userId = userId, lastModified = lastModified, homes = homes)
    }
}