package theoneclick.server.core.endpoints.authorize

import io.ktor.http.*
import theoneclick.server.core.endpoints.authorize.AuthorizeParams.Companion.RESPONSE_TYPE_CODE
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestAuthorize
import theoneclick.server.core.models.endpoints.ServerEndpoints.LOGIN
import theoneclick.shared.testing.extensions.runOnlyParameterizedTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorizeEndpointTest : IntegrationTest() {

    @Test
    fun `GIVEN invalid parameter WHEN authorize requested THEN returns bad request`() {
        runOnlyParameterizedTest(
            // Invalid state
            AuthorizeEndpointTestsScenario(state = null),

            // Invalid responseType
            AuthorizeEndpointTestsScenario(responseType = "other"),
            AuthorizeEndpointTestsScenario(responseType = null),

            // Invalid clientId
            AuthorizeEndpointTestsScenario(clientId = "SomeClientId-1"),
            AuthorizeEndpointTestsScenario(clientId = null),

            // Invalid googleHomeActionsRedirectUrl
            AuthorizeEndpointTestsScenario(googleHomeActionsRedirectUrl = "SomeRedirectUrl-1"),
            AuthorizeEndpointTestsScenario(googleHomeActionsRedirectUrl = null),

            block = { index, input ->
                testApplication {
                    val response = client.requestAuthorize(
                        state = input.state,
                        responseType = input.responseType,
                        clientId = input.clientId,
                        googleHomeActionsRedirectUrl = input.googleHomeActionsRedirectUrl,
                    )

                    assertEquals(
                        expected = HttpStatusCode.BadRequest,
                        actual = response.status,
                        message = "Status incorrect | Index: $index | Status: ${response.status} | Input: $input"
                    )
                }
            }
        )
    }

    @Test
    fun `GIVEN invalid user session WHEN authorize requested THEN redirects login`() {
        testApplication {
            val response = client.requestAuthorize(userSession = null)

            assertEquals(expected = HttpStatusCode.Found, actual = response.status)
            assertEquals(expected = LOGIN.route, actual = response.rawCurrentUrl)
        }
    }

    @Test
    fun `GIVEN valid authorize params WHEN authorize requested THEN redirect`() {
        testApplication {
            val response = client.requestAuthorize()

            assertEquals(expected = HttpStatusCode.Found, actual = response.status)
            assertEquals(
                expected = TestData.googleHomeActionsRedirectUrlStringWithParametersUrlString,
                actual = response.rawCurrentUrl
            )
        }
    }

    private data class AuthorizeEndpointTestsScenario(
        val clientId: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_CLIENT_ID,
        val googleHomeActionsRedirectUrl: String? = TestData.googleHomeActionsRedirectWithClientIdUrl.value,
        val state: String? = TestData.state,
        val responseType: String? = RESPONSE_TYPE_CODE,
    )
}
