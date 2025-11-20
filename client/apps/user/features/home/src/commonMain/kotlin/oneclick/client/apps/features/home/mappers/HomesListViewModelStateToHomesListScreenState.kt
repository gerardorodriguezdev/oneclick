package oneclick.client.apps.features.home.mappers

import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState.UiHome
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState.UiHome.UiDevice.UiWaterSensor
import oneclick.client.apps.features.home.viewModels.HomesListViewModel
import oneclick.client.apps.features.home.viewModels.HomesListViewModel.HomesListViewModelState.VMHome

internal fun HomesListViewModel.HomesListViewModelState.toHomesListScreenState(): HomesListScreenState =
    HomesListScreenState(
        homes = homes.map { home ->
            UiHome(
                id = home.id,
                devices = home.devices.map { device ->
                    when (device) {
                        is VMHome.VMDevice.VMWaterSensor ->
                            UiWaterSensor(
                                id = device.id,
                                level = device.level,
                            )
                    }
                }.toImmutableList()
            )
        }.toPersistentList(),
        isFullScreenLoading = isFullPageLoading,
        isPaginationLoading = isPaginationLoading,
    )