package oneclick.client.apps.home

import com.juul.kable.Advertisement
import com.juul.kable.State
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.client.apps.home.devices.WaterSensor
import oneclick.shared.logging.AppLogger

internal interface DevicesController {
    suspend fun scan()
}

internal class BluetoothDevicesController(
    private val appLogger: AppLogger,
    private val devicesStore: DevicesStore,
) : DevicesController {

    override suspend fun scan() {
        try {
            val advertisements = mutableListOf<Advertisement>()
            withTimeoutOrNull(SCAN_TIMEOUT) {
                WaterSensor.scanner.advertisements.collect { advertisement ->
                    advertisements.add(advertisement)
                }
            }

            if (advertisements.isEmpty()) {
                appLogger.i("No advertisements found")
            } else {
                advertisements.forEach { advertisement ->
                    val waterSensor = WaterSensor(advertisement)

                    coroutineScope {
                        launch {
                            var scanDelay = STARTING_SCAN_DELAY
                            waterSensor.connection.collect { state ->
                                when (state) {
                                    is State.Connected -> scanDelay = STARTING_SCAN_DELAY
                                    is State.Disconnected -> {
                                        waterSensor.connect()
                                        scanDelay *= 2
                                        delay(scanDelay)
                                    }

                                    else -> Unit
                                }
                            }
                        }
                    }

                    coroutineScope {
                        waterSensor.state.collect { device ->
                            devicesStore.updateDevice(device)
                        }
                    }
                }
            }
        } catch (error: Exception) {
            appLogger.e("Exception '${error.stackTraceToString()}' while scanning devices")
        }
    }

    private companion object {
        const val SCAN_TIMEOUT = 10_000L
        const val STARTING_SCAN_DELAY = 1_000L
    }
}
