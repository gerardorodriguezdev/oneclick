package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val name: RoomNameDto,
    val devicesDtos: List<DeviceDto>,
) {
    init {
        //TODO: No duplicated devices by device id
    }
}