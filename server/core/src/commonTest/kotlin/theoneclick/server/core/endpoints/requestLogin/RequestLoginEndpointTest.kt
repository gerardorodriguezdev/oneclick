package theoneclick.server.core.endpoints.requestLogin

import io.ktor.client.call.*
import io.ktor.http.*
import org.koin.test.KoinTest
import org.koin.test.inject
import theoneclick.server.core.data.models.HashedPassword
import theoneclick.server.core.data.models.UserData
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.endpoints.authorize.AuthorizeParams
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.authorizeUrlString
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestLogin
import theoneclick.shared.core.dataSources.models.entities.Uuid
import theoneclick.shared.core.dataSources.models.responses.RequestLoginResponse
import theoneclick.shared.core.routes.AppRoute
import theoneclick.shared.testing.extensions.runOnlyParameterizedTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestLoginEndpointTest : IntegrationTest(), KoinTest {

    // UserDataEmpty
    @Test
    fun `GIVEN scenario WHEN request login THEN returns bad request`() {
        runOnlyParameterizedTest(
            // Invalid username
            UserDataEmptyTestsScenario(username = ""),
            UserDataEmptyTestsScenario(username = "user/"),

            // Invalid password
            UserDataEmptyTestsScenario(password = ""),
            UserDataEmptyTestsScenario(password = "Stuff/"),

            block = { index, input ->
                testApplication {
                    val response = client.requestLogin(
                        username = input.username,
                        password = input.password,
                        authorizeParams = input.authorizeParams,
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
    fun `GIVEN loginData without authorizeParams and userDataEmpty WHEN requestLogin THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = client.requestLogin()

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals<RequestLoginResponse>(
                expected = RequestLoginResponse.LocalRedirect(AppRoute.Home),
                actual = response.body(),
            )

            val userSessionCookie = response.userSessionCookie
            assertEquals(TestData.ENCRYPTED_USER_SESSION_DATA_STRING, userSessionCookie)

            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    @Test
    fun `GIVEN valid login data with authorizeParams WHEN request login THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response =
                client.requestLogin(authorizeParams = TestData.validAuthorizeParams)

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals<RequestLoginResponse>(
                expected = RequestLoginResponse.ExternalRedirect(urlString = expectedAuthorizeUrl),
                actual = response.body(),
            )

            assertEquals(
                expected = TestData.ENCRYPTED_USER_SESSION_DATA_STRING,
                actual = response.userSessionCookie
            )
            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    // UserDataSaved
    @Test
    fun `GIVEN username is invalid WHEN request login THEN returns bad request`() {
        testApplication {
            val response = client.requestLogin(username = "InvalidUsername", userData = savedUserData)

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN password is invalid WHEN request login THEN returns bad request`() {
        testApplication {
            val response = client.requestLogin(password = "InvalidPassword", userData = savedUserData)

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN loginData without authorizeParams and userDataSaved WHEN requestLogin THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = client.requestLogin(userData = savedUserData)

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals<RequestLoginResponse>(
                expected = RequestLoginResponse.LocalRedirect(AppRoute.Home),
                actual = response.body(),
            )

            val userSessionCookie = response.userSessionCookie
            assertEquals(expected = TestData.ENCRYPTED_USER_SESSION_DATA_STRING, actual = userSessionCookie)
            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    @Test
    fun `GIVEN valid login data with authorize redirect WHEN request login THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = client.requestLogin(
                userData = savedUserData,
                authorizeParams = TestData.validAuthorizeParams,
            )

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals<RequestLoginResponse>(
                expected = RequestLoginResponse.ExternalRedirect(urlString = expectedAuthorizeUrl),
                actual = response.body(),
            )

            assertEquals(
                expected = TestData.ENCRYPTED_USER_SESSION_DATA_STRING,
                actual = response.userSessionCookie
            )
            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    private companion object {
        val expectedAuthorizeUrl = authorizeUrlString()

        val expectedUserData = TestData.userData.copy(
            authorizationCode = null,
            state = null,
            accessToken = null,
            refreshToken = null,
            devices = emptyList(),
        )

        val savedUserData = UserData(
            userId = Uuid(TestData.UUID),
            username = TestData.USERNAME,
            hashedPassword = HashedPassword(TestData.HASHED_PASSWORD),
        )

        data class UserDataEmptyTestsScenario(
            val username: String = TestData.USERNAME,
            val password: String = TestData.RAW_PASSWORD,
            val authorizeParams: AuthorizeParams? = null,
        )
    }
}
