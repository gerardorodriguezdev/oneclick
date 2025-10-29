package oneclick.client.apps.home.extensions

import com.juul.kable.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal fun scanner(serviceMostSignificantBits: Long): Scanner<PlatformAdvertisement> = Scanner {
    filters {
        match {
            name = Filter.Name.Prefix("OneClick")
            services = listOf(Bluetooth.BaseUuid + serviceMostSignificantBits)
        }
    }
}

@OptIn(ExperimentalUuidApi::class, ExperimentalApi::class)
internal fun humidityCharacteristic(serviceUuid: Uuid) = characteristicOf(
    service = serviceUuid,
    characteristic = Uuid.characteristic("humidity")
)

@OptIn(ExperimentalUuidApi::class, ExperimentalApi::class)
internal fun serialNumberCharacteristic(serviceUuid: Uuid) = characteristicOf(
    service = serviceUuid,
    characteristic = Uuid.characteristic("serial_number_string")
)