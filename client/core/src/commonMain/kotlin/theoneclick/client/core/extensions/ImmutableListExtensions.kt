package theoneclick.client.core.extensions

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import theoneclick.shared.core.dataSources.models.entities.Device

fun <T> ImmutableList<T>.updateItems(
    transform: (index: Int, currentItem: T) -> T
): ImmutableList<T> {
    val newList = mapIndexed(transform)
    return newList.toImmutableList()
}

fun ImmutableList<Device>.updateDevice(updatedDevice: Device): ImmutableList<Device> =
    updateItems(
        transform = { _, currentItem ->
            if (currentItem.id == updatedDevice.id) {
                updatedDevice
            } else {
                currentItem
            }
        }
    )
