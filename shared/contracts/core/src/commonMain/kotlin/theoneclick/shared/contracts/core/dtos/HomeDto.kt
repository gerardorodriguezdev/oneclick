package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy

@Serializable
class HomeDto private constructor(
    val name: HomeNameDto,
    val rooms: List<RoomDto>,
) {
    init {
        require(isValid(rooms)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated room name"

        fun isValid(roomsDtos: List<RoomDto>): Boolean =
            roomsDtos.containsDuplicatesBy { roomDto -> roomDto.name }

        fun homeDto(name: HomeNameDto, rooms: List<RoomDto>): HomeDto? =
            if (isValid(rooms)) {
                HomeDto(name = name, rooms = rooms)
            } else {
                null
            }

        fun unsafe(name: HomeNameDto, rooms: List<RoomDto>): HomeDto =
            HomeDto(name = name, rooms = rooms)
    }
}