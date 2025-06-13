package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PositiveIntDto(
    val value: Int,
) {
    //TODO: Validate positive
}