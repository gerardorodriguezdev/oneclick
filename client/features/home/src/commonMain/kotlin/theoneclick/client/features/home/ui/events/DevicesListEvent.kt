package theoneclick.client.features.home.ui.events

import theoneclick.shared.core.models.entities.Device

internal sealed interface DevicesListEvent {
    data object Refresh : DevicesListEvent
    data object ErrorShown : DevicesListEvent
    data class UpdateDevice(val updatedDevice: Device) : DevicesListEvent
}
