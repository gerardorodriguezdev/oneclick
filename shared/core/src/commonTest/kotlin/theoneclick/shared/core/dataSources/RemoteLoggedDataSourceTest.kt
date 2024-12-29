package theoneclick.shared.core.dataSources

import app.cash.turbine.test
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import theoneclick.shared.core.dataSources.models.endpoints.Endpoint
import theoneclick.shared.core.dataSources.models.entities.Device
import theoneclick.shared.core.dataSources.models.entities.DeviceType
import theoneclick.shared.core.dataSources.models.entities.Uuid
import theoneclick.shared.core.dataSources.models.requests.AddDeviceRequest
import theoneclick.shared.core.dataSources.models.requests.UpdateDeviceRequest
import theoneclick.shared.core.dataSources.models.responses.DevicesResponse
import theoneclick.shared.core.dataSources.models.results.AddDeviceResult
import theoneclick.shared.core.dataSources.models.results.DevicesResult
import theoneclick.shared.core.dataSources.models.results.UpdateDeviceResult
import theoneclick.shared.core.extensions.defaultHttpClient
import theoneclick.shared.core.idlingResources.EmptyIdlingResource
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
                remoteLoggedDataSource(client = addDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.addDevice(deviceName = DEVICE_NAME, room = ROOM_NAME, type = DeviceType.BLIND)
                .test {
                    assertEquals(AddDeviceResult.Success, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN addDevice called THEN returns not logged error`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(client = addDeviceEndpointMockHttpClient(isLogged = false))

            remoteLoggedDataSource.addDevice(deviceName = DEVICE_NAME, room = ROOM_NAME, type = DeviceType.BLIND)
                .test {
                    assertEquals(AddDeviceResult.Failure.NotLogged, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user logged with invalid device WHEN addDevice called THEN returns unknown error`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(client = addDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.addDevice(deviceName = "", room = "", type = DeviceType.BLIND).test {
                assertEquals(AddDeviceResult.Failure.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user logged with no devices WHEN devices called THEN returns no devices`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(
                    client = devicesEndpointMockHttpClient(
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
                    client = devicesEndpointMockHttpClient(
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
    fun `GIVEN user not logged WHEN devices called THEN returns unauthorized`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(
                    client = devicesEndpointMockHttpClient(
                        isLogged = false,
                        devices = emptyList()
                    )
                )

            remoteLoggedDataSource.devices().test {
                assertEquals(DevicesResult.Failure.NotLogged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user logged with valid device WHEN updateDevice called THEN returns ok`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(client = updateDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.updateDevice(updatedDevice = device)
                .test {
                    assertEquals(UpdateDeviceResult.Success, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user logged with invalid device WHEN updateDevice called THEN returns unknown error`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(client = updateDeviceEndpointMockHttpClient(isLogged = true))

            remoteLoggedDataSource.updateDevice(updatedDevice = invalidDevice)
                .test {
                    assertEquals(UpdateDeviceResult.Failure.UnknownError, awaitItem())
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN updateDevice called THEN returns not logged error`() {
        runTest {
            val remoteLoggedDataSource =
                remoteLoggedDataSource(client = updateDeviceEndpointMockHttpClient(isLogged = false))

            remoteLoggedDataSource.updateDevice(updatedDevice = device)
                .test {
                    assertEquals(UpdateDeviceResult.Failure.NotLogged, awaitItem())
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

        private fun TestScope.remoteLoggedDataSource(client: HttpClient): RemoteLoggedDataSource =
            RemoteLoggedDataSource(
                dispatchersProvider = FakeDispatchersProvider(StandardTestDispatcher(testScheduler)),
                client = client,
                idlingResource = EmptyIdlingResource(),
            )

        private fun addDeviceEndpointMockHttpClient(
            isLogged: Boolean,
        ): HttpClient =
            defaultHttpClient(
                mockEngine(
                    pathToFake = Endpoint.ADD_DEVICE.route,
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
            defaultHttpClient(
                mockEngine(
                    pathToFake = Endpoint.DEVICES.route,
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
            defaultHttpClient(
                mockEngine(
                    pathToFake = Endpoint.UPDATE_DEVICE.route,
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

        private fun defaultHttpClient(mockEngine: MockEngine): HttpClient =
            defaultHttpClient(
                engine = mockEngine,
                protocol = null,
                host = null,
                port = null
            )
    }
}
