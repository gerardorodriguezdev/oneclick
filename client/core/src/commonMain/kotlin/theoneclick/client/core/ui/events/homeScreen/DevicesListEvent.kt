package theoneclick.client.core.ui.events.homeScreen

import theoneclick.shared.core.dataSources.models.entities.Device

sealed interface DevicesListEvent {
    data object Refresh : DevicesListEvent
    data object ErrorShown : DevicesListEvent
    data class UpdateDevice(val updatedDevice: Device) : DevicesListEvent
}
