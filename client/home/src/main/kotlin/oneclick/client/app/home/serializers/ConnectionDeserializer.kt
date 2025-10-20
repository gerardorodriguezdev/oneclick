package oneclick.client.app.home.serializers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import oneclick.client.app.home.serializers.LineDeserializer.Entry
import oneclick.shared.contracts.core.models.NonNegativeInt.Companion.toNonNegativeInt
import oneclick.shared.contracts.core.models.PositiveIntRange.Companion.positiveIntRange
import oneclick.shared.contracts.core.models.Uuid.Companion.toUuid
import oneclick.shared.contracts.homes.models.Device
import oneclick.shared.contracts.homes.models.DeviceName.Companion.toDeviceName
import oneclick.shared.contracts.homes.models.DeviceType
import oneclick.shared.contracts.homes.models.DeviceType.Companion.toDeviceType

interface ConnectionDeserializer {
    fun deserialize(stream: Flow<Char>): Flow<Device>
}

class DefaultConnectionDeserializer(private val lineDeserializer: LineDeserializer) : ConnectionDeserializer {
    override fun deserialize(stream: Flow<Char>): Flow<Device> =
        stream
            .combineCharsIntoStrings()
            .map { data -> lineDeserializer.deserialize(data) }
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

    private fun Map<String, Entry>.toDevice(): Device? {
        val id = get(DEVICE_ID_KEY)?.value?.toUuid() ?: return null
        val type = get(TYPE_KEY)?.value?.toDeviceType() ?: return null
        val deviceName = get(DEVICE_NAME_KEY)?.value?.toDeviceName() ?: return null

        return when (type) {
            DeviceType.WATER_SENSOR -> {
                val rangeState = get(RANGE_START_KEY)?.value?.toNonNegativeInt() ?: return null
                val rangeEnd = get(RANGE_END_KEY)?.value?.toNonNegativeInt() ?: return null
                val range = positiveIntRange(start = rangeState, end = rangeEnd) ?: return null
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

    private companion object {
        const val MESSAGE_SEPARATOR = '&'

        const val DEVICE_ID_KEY = "id"
        const val TYPE_KEY = "type"
        const val DEVICE_NAME_KEY = "name"

        const val RANGE_START_KEY = "range_start"
        const val RANGE_END_KEY = "range_end"
        const val LEVEL_KEY = "level"
    }
}