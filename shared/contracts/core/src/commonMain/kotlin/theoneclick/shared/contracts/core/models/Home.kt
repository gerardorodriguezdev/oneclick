package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.containsDuplicatesBy

@Serializable
class Home private constructor(
    val name: HomeName,
    val rooms: List<Room>,
) {
    init {
        require(isValid(rooms)) { ERROR_MESSAGE }
    }

    companion object Companion {
        private const val ERROR_MESSAGE = "Duplicated room name"

        fun isValid(rooms: List<Room>): Boolean =
            rooms.containsDuplicatesBy { roomDto -> roomDto.name }

        fun home(name: HomeName, rooms: List<Room>): Home? =
            if (isValid(rooms)) {
                Home(name = name, rooms = rooms)
            } else {
                null
            }

        fun unsafe(name: HomeName, rooms: List<Room>): Home =
            Home(name = name, rooms = rooms)
    }
}