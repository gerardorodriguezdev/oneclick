package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy

@Serializable
data class HomeDto(
    val name: HomeNameDto,
    val roomsDtos: List<RoomDto>,
) {
    init {
        require(isValid(roomsDtos)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated room name"

        fun isValid(roomsDtos: List<RoomDto>): Boolean =
            roomsDtos.containsDuplicatesBy { roomDto -> roomDto.name }

        fun homeDto(nameDto: HomeNameDto, roomsDtos: List<RoomDto>): HomeDto? =
            if (isValid(roomsDtos)) HomeDto(nameDto, roomsDtos) else null
    }
}