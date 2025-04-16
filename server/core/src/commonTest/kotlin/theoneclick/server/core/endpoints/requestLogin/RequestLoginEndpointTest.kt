package theoneclick.server.core.endpoints.requestLogin

import io.ktor.http.*
import org.koin.test.KoinTest
import org.koin.test.inject
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.models.HashedPassword
import theoneclick.server.core.models.User
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestLogin
import theoneclick.shared.core.models.entities.Uuid
import theoneclick.shared.testing.extensions.runOnlyParameterizedTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestLoginEndpointTest : IntegrationTest(), KoinTest {

    // UserEmpty
    @Test
    fun `GIVEN scenario WHEN request login THEN returns bad request`() {
        runOnlyParameterizedTest(
            // Invalid username
            UserEmptyTestsScenario(username = ""),
            UserEmptyTestsScenario(username = "user/"),

            // Invalid password
            UserEmptyTestsScenario(password = ""),
            UserEmptyTestsScenario(password = "Stuff/"),

            block = { index, input ->
                testApplication {
                    val response = httpClient.requestLogin(
                        username = input.username,
                        password = input.password,
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
    fun `GIVEN loginData without userEmpty WHEN requestLogin THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = httpClient.requestLogin()

            assertEquals(HttpStatusCode.OK, response.status)

            val userSessionCookie = response.userSessionCookie
            assertEquals(TestData.ENCRYPTED_USER_SESSION_DATA_STRING, userSessionCookie)

            assertEquals(expected = expectedUser, actual = repository.user(TestData.username))
        }
    }

    // UserSaved
    @Test
    fun `GIVEN username is invalid WHEN request login THEN returns bad request`() {
        testApplication {
            val response = httpClient.requestLogin(username = "InvalidUsername", user = savedUser)

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN password is invalid WHEN request login THEN returns bad request`() {
        testApplication {
            val response = httpClient.requestLogin(password = "InvalidPassword", user = savedUser)

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN loginData without userSaved WHEN requestLogin THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = httpClient.requestLogin(user = savedUser)

            assertEquals(HttpStatusCode.OK, response.status)

            val userSessionCookie = response.userSessionCookie
            assertEquals(expected = TestData.ENCRYPTED_USER_SESSION_DATA_STRING, actual = userSessionCookie)
            assertEquals(expected = expectedUser, actual = repository.user(TestData.username))
        }
    }

    private companion object {
        val expectedUser = TestData.user.copy(
            devices = emptyList(),
        )

        val savedUser = User(
            id = Uuid(TestData.UUID),
            username = TestData.username,
            hashedPassword = HashedPassword(TestData.HASHED_PASSWORD),
        )

        data class UserEmptyTestsScenario(
            val username: String = TestData.USERNAME,
            val password: String = TestData.RAW_PASSWORD,
        )
    }
}
