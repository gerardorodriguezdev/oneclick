package theoneclick.client.feature.home.states

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import theoneclick.shared.core.models.entities.Device

data class DevicesListState(
    val devices: ImmutableList<Device> = persistentListOf(),

    val isLoading: Boolean = false,
    val showError: Boolean = false,
)
