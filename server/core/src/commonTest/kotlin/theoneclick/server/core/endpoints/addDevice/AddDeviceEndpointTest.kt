package theoneclick.server.core.endpoints.addDevice

import io.ktor.http.*
import org.koin.test.KoinTest
import org.koin.test.inject
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestAddDevice
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.Uuid
import kotlin.test.Test
import kotlin.test.assertEquals

class AddDeviceEndpointTest : IntegrationTest(), KoinTest {

    @Test
    fun `GIVEN invalid request WHEN addDevice requested THEN returns bad request`() {
        testApplication {
            val response = httpClient.requestAddDevice(deviceName = "")

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN no user session WHEN addDevice requested THEN redirects to login`() {
        testApplication {
            val response = httpClient.requestAddDevice(userSession = null)

            assertEquals(expected = HttpStatusCode.Unauthorized, actual = response.status)
        }
    }

    @Test
    fun `GIVEN valid request WHEN addDevice requested THEN returns ok`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = httpClient.requestAddDevice()

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(
                expected = TestData.user.copy(
                    devices = listOf(
                        Device.Blind(
                            id = Uuid(TestData.UUID),
                            deviceName = TestData.DEVICE_NAME,
                            room = TestData.ROOM,
                            isOpened = false,
                            rotation = 0,
                        )
                    )
                ),
                actual = repository.user(TestData.username)
            )
        }
    }
}
