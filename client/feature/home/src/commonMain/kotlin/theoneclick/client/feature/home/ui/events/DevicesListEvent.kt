package theoneclick.client.feature.home.ui.events

import theoneclick.shared.core.models.entities.Device

sealed interface DevicesListEvent {
    data object Refresh : DevicesListEvent
    data object ErrorShown : DevicesListEvent
    data class UpdateDevice(val updatedDevice: Device) : DevicesListEvent
}
