package oneclick.client.apps.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import oneclick.shared.contracts.core.models.NonNegativeInt.Companion.toNonNegativeInt
import oneclick.shared.contracts.core.models.PositiveIntRange
import oneclick.shared.contracts.core.models.Uuid.Companion.toUuid
import oneclick.shared.contracts.homes.models.Device
import oneclick.shared.contracts.homes.models.DeviceName.Companion.toDeviceName
import oneclick.shared.contracts.homes.models.DeviceType
import oneclick.shared.contracts.homes.models.DeviceType.Companion.toDeviceType

//TODO: Move to devices controller
internal object ConnectionDeserializer {
    private const val ENTRY_SEPARATOR = ";"
    private const val ENTRY_KEY_VALUE_SEPARATOR = "="
    private const val MESSAGE_SEPARATOR = '&'

    private const val DEVICE_ID_KEY = "id"
    private const val TYPE_KEY = "type"
    private const val DEVICE_NAME_KEY = "name"

    private const val RANGE_START_KEY = "range_start"
    private const val RANGE_END_KEY = "range_end"
    private const val LEVEL_KEY = "level"

    fun Flow<Char>.deserialize(): Flow<Device> =
        combineCharsIntoStrings()
            .map { data -> data.toEntries() }
            .map { entries -> entries.toDevice() }
            .filterNotNull()

    private fun Flow<Char>.combineCharsIntoStrings(): Flow<String> =
        flow {
            val stringBuilder = StringBuilder()
            collect { char ->
                if (MESSAGE_SEPARATOR == char) {
                    emit(stringBuilder.toString())
                    stringBuilder.clear()
                } else {
                    stringBuilder.append(char)
                }
            }
        }

    private fun String.toEntries(): Map<String, Entry> {
        val entriesStrings = split(ENTRY_SEPARATOR)

        val entries = entriesStrings
            .map { entryString ->
                val (key, value) = entryString.split(ENTRY_KEY_VALUE_SEPARATOR)
                Entry(key = key, value = value)
            }

        return entries.associateBy { entry -> entry.key }
    }

    private fun Map<String, Entry>.toDevice(): Device? {
        val id = get(DEVICE_ID_KEY)?.value?.toUuid() ?: return null
        val type = get(TYPE_KEY)?.value?.toDeviceType() ?: return null
        val deviceName = get(DEVICE_NAME_KEY)?.value?.toDeviceName() ?: return null

        return when (type) {
            DeviceType.WATER_SENSOR -> {
                val rangeState = get(RANGE_START_KEY)?.value?.toNonNegativeInt() ?: return null
                val rangeEnd = get(RANGE_END_KEY)?.value?.toNonNegativeInt() ?: return null
                val range = PositiveIntRange.Companion.positiveIntRange(start = rangeState, end = rangeEnd) ?: return null
                val level = get(LEVEL_KEY)?.value?.toNonNegativeInt() ?: return null

                Device.WaterSensor.waterSensor(
                    id = id,
                    name = deviceName,
                    range = range,
                    level = level,
                )
            }
        }
    }

    private data class Entry(
        val key: String,
        val value: String,
    )
}