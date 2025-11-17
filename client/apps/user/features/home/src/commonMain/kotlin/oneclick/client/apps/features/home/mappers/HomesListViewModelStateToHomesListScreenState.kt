package oneclick.client.apps.features.home.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState.UiHome
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState.UiHome.UiDevice
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState.UiHome.UiDevice.UiWaterSensor
import oneclick.client.apps.features.home.viewModels.HomesListViewModel
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.homes.models.Device
import oneclick.shared.contracts.homes.models.Home

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes.toUiHomes(),
        isFullScreenLoading = isFullPageLoading,
        isPaginationLoading = isPaginationLoading,
    )

private fun UniqueList<Home>.toUiHomes(): ImmutableList<UiHome> =
    map { home -> home.toUiHome() }.toPersistentList()

private fun Home.toUiHome(): UiHome =
    UiHome(
        id = id.value,
        devices = devices.toUiDevices()
    )

private fun UniqueList<Device>.toUiDevices(): ImmutableList<UiDevice> =
    map { device -> device.toUiDevice() }.toImmutableList()

private fun Device.toUiDevice(): UiDevice =
    when (this) {
        is Device.WaterSensor -> UiWaterSensor(
            id = id.value,
            level = level.value.toString(),
        )
    }
