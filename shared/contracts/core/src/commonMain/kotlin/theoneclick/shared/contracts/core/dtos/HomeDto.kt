package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class HomeDto(
    val name: HomeNameDto,
    val roomsDtos: List<RoomDto>,
) {
    init {
        //TODO: No duplicated by room name
    }
}