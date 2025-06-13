package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PositiveIntRangeDto(
    val start: PositiveIntDto,
    val end: PositiveIntDto,
) {
    //TODO: Validate start bigger than end
}