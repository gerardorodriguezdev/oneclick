package theoneclick.client.core.dataSources

import app.cash.turbine.test
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.client.core.testing.fakes.fakeHttpClient
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.entities.Uuid
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import theoneclick.shared.testing.extensions.mockEngine
import theoneclick.shared.testing.extensions.respondJson
import theoneclick.shared.testing.extensions.toRequestBodyObject
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteLoggedDataSourceTest {

    @Test
    fun `GIVEN user logged with valid device WHEN addDevice called THEN returns ok`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(httpClient = addDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.addDevice(deviceName = DEVICE_NAME, room = ROOM_NAME, type = DeviceType.BLIND)
                .test {
                    assertEquals(AddDeviceResult.Success, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN addDevice called THEN returns failure`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(httpClient = addDeviceEndpointMockHttpClient(isLogged = false))

            remoteLoggedDataSource.addDevice(deviceName = DEVICE_NAME, room = ROOM_NAME, type = DeviceType.BLIND)
                .test {
                    assertEquals(AddDeviceResult.Failure, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user logged with invalid device WHEN addDevice called THEN returns failure`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(httpClient = addDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.addDevice(deviceName = "", room = "", type = DeviceType.BLIND).test {
                assertEquals(AddDeviceResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user logged with no devices WHEN devices called THEN returns no devices`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(
                    httpClient = devicesEndpointMockHttpClient(
                        isLogged = true,
                        devices = emptyList()
                    )
                )

            remoteLoggedDataSource.devices().test {
                assertEquals(DevicesResult.Success(devices = emptyList()), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user logged with devices WHEN devices called THEN returns devices`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(
                    httpClient = devicesEndpointMockHttpClient(
                        isLogged = true,
                        devices = devices
                    )
                )

            remoteLoggedDataSource.devices().test {
                assertEquals(DevicesResult.Success(devices = devices), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN devices called THEN returns failure`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(
                    httpClient = devicesEndpointMockHttpClient(
                        isLogged = false,
                        devices = emptyList()
                    )
                )

            remoteLoggedDataSource.devices().test {
                assertEquals(DevicesResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user logged with valid device WHEN updateDevice called THEN returns ok`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(httpClient = updateDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.updateDevice(updatedDevice = device)
                .test {
                    assertEquals(UpdateDeviceResult.Success, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user logged with invalid device WHEN updateDevice called THEN returns failure`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(httpClient = updateDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.updateDevice(updatedDevice = invalidDevice)
                .test {
                    assertEquals(UpdateDeviceResult.Failure, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN updateDevice called THEN returns failure`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(httpClient = updateDeviceEndpointMockHttpClient(isLogged = false))

            remoteLoggedDataSource.updateDevice(updatedDevice = device)
                .test {
                    assertEquals(UpdateDeviceResult.Failure, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    companion object {
        const val DEVICE_NAME = "device1"
        const val ROOM_NAME = "room1"
        val device = Device.Blind(
            id = Uuid("1"),
            deviceName = DEVICE_NAME,
            room = ROOM_NAME,
            isOpened = false,
            rotation = 0,
        )
        val invalidDevice = Device.Blind(
            id = Uuid(value = "2"),
            deviceName = "DeviceName2!",
            room = "RoomName2!",
            isOpened = false,
            rotation = 181,
        )

        val devices = listOf(
            Device.Blind(
                id = Uuid("1"),
                deviceName = DEVICE_NAME,
                room = ROOM_NAME,
                isOpened = false,
                rotation = 0,
            )
        )

        private fun TestScope.remoteLoggedDataSource(httpClient: HttpClient): RemoteLoggedDataSource =
            RemoteLoggedDataSource(
                dispatchersProvider = FakeDispatchersProvider(StandardTestDispatcher(testScheduler)),
                httpClient = httpClient,
            )

        private fun addDeviceEndpointMockHttpClient(isLogged: Boolean): HttpClient =
            fakeHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoint.ADD_DEVICE.route,
                    onPathFound = { request ->
                        val addDeviceRequest = request.toRequestBodyObject<AddDeviceRequest>()

                        when {
                            !isLogged -> respondError(HttpStatusCode.Unauthorized)
                            addDeviceRequest == null -> respondError(HttpStatusCode.BadRequest)
                            addDeviceRequest.deviceName != DEVICE_NAME -> respondError(HttpStatusCode.BadRequest)
                            addDeviceRequest.room != ROOM_NAME -> respondError(HttpStatusCode.BadRequest)
                            else -> respondOk()
                        }
                    },
                )
            )

        private fun devicesEndpointMockHttpClient(
            isLogged: Boolean,
            devices: List<Device>,
        ): HttpClient =
            fakeHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoint.DEVICES.route,
                    onPathFound = { request ->
                        if (isLogged) {
                            respondJson(DevicesResponse(devices))
                        } else {
                            respondError(HttpStatusCode.Unauthorized)
                        }
                    },
                )
            )

        private fun updateDeviceEndpointMockHttpClient(
            isLogged: Boolean,
        ): HttpClient =
            fakeHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoint.UPDATE_DEVICE.route,
                    onPathFound = { request ->
                        val updateDeviceRequest = request.toRequestBodyObject<UpdateDeviceRequest>()

                        when {
                            !isLogged -> respondError(HttpStatusCode.Unauthorized)
                            updateDeviceRequest == null -> respondError(HttpStatusCode.BadRequest)
                            updateDeviceRequest.updatedDevice != device -> respondError(HttpStatusCode.BadRequest)
                            else -> respondOk()
                        }
                    },
                )
            )
    }
}
