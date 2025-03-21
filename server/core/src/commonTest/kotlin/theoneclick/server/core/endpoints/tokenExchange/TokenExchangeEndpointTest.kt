package theoneclick.server.core.endpoints.tokenExchange

import io.ktor.client.call.*
import io.ktor.http.*
import org.koin.test.KoinTest
import org.koin.test.inject
import theoneclick.server.core.validators.ParamsValidator
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.TestData.googleHomeActionsRedirectWithClientIdUrl
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestTokenExchange
import theoneclick.shared.testing.extensions.runOnlyParameterizedTest
import theoneclick.shared.testing.timeProvider.FakeTimeProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class TokenExchangeEndpointTest : IntegrationTest(), KoinTest {

    // Common
    @Test
    fun `GIVEN invalid user session WHEN token exchange requested THEN redirects login`() {
        testApplication {
            val response = client.requestTokenExchange(
                userSession = null,
                grantTypeString = TokenExchangeParams.AUTHORIZATION_CODE_TYPE,
                userData = TestData.userData.copy(
                    authorizationCode = null,
                    state = null,
                    accessToken = null,
                    refreshToken = null,
                )
            )

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN scenario WHEN requesting token THEN returns bad request`() {
        runOnlyParameterizedTest(
            // Invalid clientId
            CommonTestsScenario(
                clientId = "clientId",
                grantTypeString = TokenExchangeParams.REFRESH_TOKEN_TYPE,
            ),
            CommonTestsScenario(
                clientId = null,
                grantTypeString = TokenExchangeParams.REFRESH_TOKEN_TYPE,
            ),

            // Invalid clientSecret
            CommonTestsScenario(
                clientSecret = "clientSecret",
                grantTypeString = TokenExchangeParams.REFRESH_TOKEN_TYPE,
            ),
            CommonTestsScenario(
                clientSecret = null,
                grantTypeString = TokenExchangeParams.REFRESH_TOKEN_TYPE,
            ),

            // Invalid grantType
            CommonTestsScenario(
                grantTypeString = "incorret_type",
            ),
            CommonTestsScenario(
                grantTypeString = null,
            ),
            block = { index, input ->
                testApplication {
                    val response = client.requestTokenExchange(
                        clientId = input.clientId,
                        grantTypeString = input.grantTypeString,
                        clientSecret = input.clientSecret,
                        authorizationCode = input.authorizationCode,
                        refreshToken = input.refreshToken,
                        redirectUrl = input.redirectUrl,
                        userData = TestData.userData.copy(
                            authorizationCode = null,
                            state = null,
                            accessToken = null,
                            refreshToken = null,
                        ),
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

    // AccessEncryptedToken

    @Test
    fun `GIVEN expired authorization code WHEN tokens requested THEN bad request returned`() {
        val timeProvider = FakeTimeProvider(fakeCurrentTimeInMillis = 0L)

        testApplication(timeProvider = timeProvider) {
            timeProvider.fakeCurrentTimeInMillis =
                ParamsValidator.AUTHORIZATION_CODE_TOKEN_EXPIRATION_IN_MILLIS + 1

            val response = client.requestTokenExchange(
                grantTypeString = TokenExchangeParams.AUTHORIZATION_CODE_TYPE,
                authorizationCode = TestData.ENCRYPTED_TOKEN_VALUE,
                redirectUrl = googleHomeActionsRedirectWithClientIdUrl.value,
                userData = TestData.userData.copy(
                    state = null,
                    accessToken = null,
                    refreshToken = null,
                    authorizationCode = TestData.encryptedToken.copy(
                        value = TestData.ENCRYPTED_TOKEN_VALUE,
                        creationTimeInMillis = 0L
                    )
                ),
            )

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN invalid authorization code WHEN tokens requested THEN bad request returned`() {
        testApplication {
            val response = client.requestTokenExchange(
                grantTypeString = TokenExchangeParams.AUTHORIZATION_CODE_TYPE,
                authorizationCode = "InvalidAuthorizationCode",
                redirectUrl = googleHomeActionsRedirectWithClientIdUrl.value,
                userData = TestData.userData.copy(
                    state = null,
                    accessToken = null,
                    refreshToken = null,
                ),
            )

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN valid session WHEN tokens requested THEN tokens returned`() {
        val repository: UserDataSource by inject()

        testApplication {
            val expectedUserData = TestData.userData.copy(
                authorizationCode = null,
                state = null,
                accessToken = TestData.encryptedToken,
                refreshToken = TestData.encryptedToken,
            )

            val response = client.requestTokenExchange(
                grantTypeString = TokenExchangeParams.AUTHORIZATION_CODE_TYPE,
                authorizationCode = TestData.ENCRYPTED_TOKEN_VALUE,
                redirectUrl = googleHomeActionsRedirectWithClientIdUrl.value,
                userData = TestData.userData.copy(
                    state = null,
                    accessToken = null,
                    refreshToken = null,
                ),
            )
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(
                expected = TokenExchangeResponse(
                    accessToken = TestData.ENCRYPTED_TOKEN_VALUE,
                    refreshToken = TestData.ENCRYPTED_TOKEN_VALUE,
                    expiresIn = ParamsValidator.USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS,
                ),
                actual = response.body<TokenExchangeResponse>(),
            )
            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    // RefreshEncryptedToken

    @Test
    fun `GIVEN invalid refresh token WHEN token requested THEN bad request returned`() {
        testApplication {
            val response = client.requestTokenExchange(
                grantTypeString = TokenExchangeParams.REFRESH_TOKEN_TYPE,
                refreshToken = "InvalidRefreshToken",
                userData = TestData.userData.copy(
                    state = null,
                ),
            )

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN valid session WHEN refresh token requested THEN tokens returned`() {
        val repository: UserDataSource by inject()

        testApplication {
            val expectedUserData = TestData.userData.copy(
                authorizationCode = null,
                state = null,
                accessToken = TestData.encryptedToken,
                refreshToken = TestData.encryptedToken,
            )

            val response = client.requestTokenExchange(
                grantTypeString = TokenExchangeParams.REFRESH_TOKEN_TYPE,
                refreshToken = TestData.ENCRYPTED_TOKEN_VALUE,
                userData = TestData.userData.copy(
                    authorizationCode = null,
                    state = null,
                ),
            )

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(
                expected = TokenExchangeResponse(
                    accessToken = TestData.ENCRYPTED_TOKEN_VALUE,
                    refreshToken = null,
                    expiresIn = ParamsValidator.USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS,
                ),
                actual = response.body<TokenExchangeResponse>(),
            )
            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    private companion object {
        data class CommonTestsScenario(
            val clientId: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_CLIENT_ID,
            val clientSecret: String? = TestData.SECRET_GOOGLE_HOME_ACTIONS_SECRET,
            val grantTypeString: String?,
            val authorizationCode: String? = null,
            val refreshToken: String? = null,
            val redirectUrl: String? = null,
        )
    }
}
