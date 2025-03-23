package theoneclick.server.core.endpoints.devices

import io.ktor.client.call.*
import io.ktor.http.*
import org.koin.test.KoinTest
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestDevices
import theoneclick.shared.core.models.responses.DevicesResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicesEndpointTest : IntegrationTest(), KoinTest {

    @Test
    fun `GIVEN user not logged WHEN devices requested THEN redirects to login`() {
        testApplication {
            val response = httpClient.requestDevices(userSession = null)

            assertEquals(expected = HttpStatusCode.Unauthorized, actual = response.status)
        }
    }

    @Test
    fun `GIVEN user logged with no devices WHEN devices requested THEN returns empty list`() {
        testApplication {
            val response = httpClient.requestDevices(devices = emptyList())

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(expected = DevicesResponse(devices = emptyList()), actual = response.body())
        }
    }

    @Test
    fun `GIVEN user logged with devices WHEN devices requested THEN devices returned`() {
        testApplication {
            val response = httpClient.requestDevices()

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(expected = DevicesResponse(devices = TestData.devices.toList()), actual = response.body())
        }
    }
}
