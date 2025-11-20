package oneclick.client.apps.features.home.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import oneclick.client.apps.features.home.viewModels.HomesListViewModel.HomesListViewModelState.VMHome
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.homes.models.Device
import oneclick.shared.contracts.homes.models.Home

internal fun UniqueList<Home>.toVMHomes(): ImmutableList<VMHome> =
    elements.map { home ->
        VMHome(
            id = home.id.value,
            devices = home.devices.elements.map { device ->
                when (device) {
                    is Device.WaterSensor -> VMHome.VMDevice.VMWaterSensor(
                        id = device.id.value,
                        level = device.level.value.toString(),
                    )
                }
            }.toPersistentList()
        )
    }.toPersistentList()