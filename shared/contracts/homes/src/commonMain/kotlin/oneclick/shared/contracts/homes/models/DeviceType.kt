package oneclick.shared.contracts.homes.models

enum class DeviceType(val value: String) {
    WATER_SENSOR(value = "WS");

    companion object {
        fun String?.toDeviceType(): DeviceType? =
            when (this) {
                WATER_SENSOR.value -> WATER_SENSOR
                else -> null
            }
    }
}
