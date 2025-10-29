package oneclick.client.apps.home.devices

import com.juul.kable.Advertisement
import com.juul.kable.ExperimentalApi
import com.juul.kable.Peripheral
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import oneclick.client.apps.home.devices.base.BaseUuid
import oneclick.client.apps.home.extensions.humidityCharacteristic
import oneclick.client.apps.home.extensions.scanner
import oneclick.client.apps.home.extensions.serialNumberCharacteristic
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PositiveIntRange
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.homes.models.Device
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal class WaterSensor(advertisement: Advertisement) {
    private val peripheral = Peripheral(advertisement)
    private val serialNumber = MutableStateFlow<Uuid?>(null)
    private val humidity = MutableStateFlow<Int?>(null)

    val connection = peripheral.state
    val state: Flow<Device> =
        combine(
            serialNumber.filterNotNull(),
            humidity.filterNotNull()
        ) { serialNumber, humidity ->
            Device.WaterSensor.unsafe(
                id = serialNumber,
                range = range,
                level = NonNegativeInt.unsafe(humidity),
            )
        }

    suspend fun connect() {
        try {
            val scope = peripheral.connect()
            scope.launch {
                peripheral.observe(humidityCharacteristic)
                    .map { byteArray -> byteArray.first().toInt() }
                    .collect { humidity -> this@WaterSensor.humidity.value = humidity }
            }

            scope.launch {
                val serialNumberByteArray = peripheral.read(serialNumberCharacteristic)
                val serialNumberString = serialNumberByteArray.decodeToString()
                serialNumber.value = Uuid.unsafe(serialNumberString)
            }
        } catch (_: Exception) {
            peripheral.disconnect()
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalApi::class)
    companion object {
        private const val WATER_SENSOR_MOST_SIGNIFICANT_BITS = -5882832869184353020L // ae5bfaac-8f46-4d04
        private const val WATER_SENSOR_16_BIT = 0xAA80

        private val baseUuid = BaseUuid(WATER_SENSOR_MOST_SIGNIFICANT_BITS)
        private val serviceUuid = baseUuid + WATER_SENSOR_16_BIT

        private val humidityCharacteristic = humidityCharacteristic(serviceUuid)
        private val serialNumberCharacteristic = serialNumberCharacteristic(serviceUuid)

        val scanner = scanner(WATER_SENSOR_MOST_SIGNIFICANT_BITS)

        private val range = PositiveIntRange.unsafe(
            start = NonNegativeInt.zero,
            end = NonNegativeInt.unsafe(100)
        )
    }
}