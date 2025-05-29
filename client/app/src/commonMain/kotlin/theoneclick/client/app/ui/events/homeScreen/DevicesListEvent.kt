package theoneclick.client.app.ui.events.homeScreen

import theoneclick.shared.core.models.entities.Device

sealed interface DevicesListEvent {
    data object Refresh : DevicesListEvent
    data object ErrorShown : DevicesListEvent
    data class UpdateDevice(val updatedDevice: Device) : DevicesListEvent
}
