package oneclick.client.apps.home.devices

import com.juul.kable.Advertisement
import com.juul.kable.State
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.shared.logging.AppLogger

internal interface DevicesController {
    suspend fun scan(): Boolean
}

internal class BluetoothDevicesController(
    private val appLogger: AppLogger,
    private val devicesStore: DevicesStore,
) : DevicesController {

    override suspend fun scan(): Boolean {
        try {
            val advertisements = mutableListOf<Advertisement>()
            withTimeoutOrNull(SCAN_TIMEOUT) {
                WaterSensor.scanner.advertisements.collect { advertisement ->
                    advertisements.add(advertisement)
                }
            }

            if (advertisements.isEmpty()) {
                appLogger.i("No advertisements found")
                return false
            } else {
                advertisements.forEach { advertisement ->
                    coroutineScope {
                        val waterSensor = WaterSensor(advertisement)
                        launch {
                            var connectionDelay = STARTING_CONNECTION_DELAY
                            waterSensor.connection.collect { state ->
                                when (state) {
                                    is State.Connected -> connectionDelay = STARTING_CONNECTION_DELAY
                                    is State.Disconnected -> {
                                        waterSensor.connect()
                                        delay(connectionDelay)
                                        connectionDelay *= 2
                                    }

                                    else -> Unit
                                }
                            }
                        }

                        launch {
                            waterSensor.state.collect { device ->
                                devicesStore.updateDevice(device)
                            }
                        }
                    }
                }
                return true
            }
        } catch (error: Exception) {
            appLogger.e("Exception '${error.stackTraceToString()}' while scanning devices")
            return false
        }
    }

    private companion object {
        const val SCAN_TIMEOUT = 10_000L
        const val STARTING_CONNECTION_DELAY = 1_000L
    }
}
