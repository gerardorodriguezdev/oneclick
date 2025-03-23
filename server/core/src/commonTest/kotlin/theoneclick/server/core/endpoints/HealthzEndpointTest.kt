package theoneclick.server.core.endpoints

import io.ktor.http.*
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestHealthz
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthzEndpointTest : IntegrationTest() {

    @Test
    fun `GIVEN server healthy WHEN healthz requested THEN returns ok`() {
        testApplication {
            val response = httpClient.requestHealthz()

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }
    }
}
