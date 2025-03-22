package theoneclick.server.core.endpoints.updateDevice

import io.ktor.http.*
import org.koin.test.KoinTest
import org.koin.test.inject
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestUpdateDevice
import theoneclick.shared.core.models.entities.Uuid
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateDeviceEndpointTest : IntegrationTest(), KoinTest {

    @Test
    fun `GIVEN invalid request WHEN updateDevice requested THEN returns bad request`() {
        testApplication {
            val response = client.requestUpdateDevice(
                updatedDevice = TestData.blind.copy(id = Uuid(value = "1"))
            )

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN no user session WHEN updateDevice requested THEN redirects to login`() {
        testApplication {
            val response = client.requestUpdateDevice(updatedDevice = TestData.blind, userSession = null)

            assertEquals(expected = HttpStatusCode.Unauthorized, actual = response.status)
        }
    }

    @Test
    fun `GIVEN valid request WHEN updateDevice requested THEN returns ok`() {
        val repository: UserDataSource by inject()
        val updatedDevice = TestData.blind.copy(isOpened = true)

        testApplication {
            val response = client.requestUpdateDevice(
                updatedDevice = updatedDevice,
            )

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(
                expected = TestData.userData.copy(
                    devices = listOf(updatedDevice),
                ),
                actual = repository.userData()
            )
        }
    }
}
