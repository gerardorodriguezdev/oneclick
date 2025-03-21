package theoneclick.server.core.endpoints

import io.ktor.client.call.*
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestIsUserLogged
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.core.models.responses.UserLoggedResponse.Logged
import theoneclick.shared.core.models.responses.UserLoggedResponse.NotLogged
import kotlin.test.Test
import kotlin.test.assertEquals

class IsUserLoggedEndpointTest : IntegrationTest() {

    @Test
    fun `GIVEN user logged WHEN isUserLogged requested THEN returns user logged`() {
        testApplication {
            val response = client.requestIsUserLogged()

            assertEquals<UserLoggedResponse>(expected = Logged, actual = response.body())
        }
    }

    @Test
    fun `GIVEN user not logged WHEN isUserLogged requested THEN returns user not logged`() {
        testApplication {
            val response = client.requestIsUserLogged(userSession = null)

            assertEquals<UserLoggedResponse>(expected = NotLogged, actual = response.body())
        }
    }
}
