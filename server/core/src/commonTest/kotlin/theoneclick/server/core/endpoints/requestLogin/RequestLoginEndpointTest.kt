package theoneclick.server.core.endpoints.requestLogin

import io.ktor.http.*
import org.koin.test.KoinTest
import org.koin.test.inject
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.models.HashedPassword
import theoneclick.server.core.models.UserData
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestLogin
import theoneclick.shared.core.models.entities.Uuid
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
    fun `GIVEN loginData without userDataEmpty WHEN requestLogin THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = httpClient.requestLogin()

            assertEquals(HttpStatusCode.OK, response.status)

            val userSessionCookie = response.userSessionCookie
            assertEquals(TestData.ENCRYPTED_USER_SESSION_DATA_STRING, userSessionCookie)

            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    // UserDataSaved
    @Test
    fun `GIVEN username is invalid WHEN request login THEN returns bad request`() {
        testApplication {
            val response = httpClient.requestLogin(username = "InvalidUsername", userData = savedUserData)

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN password is invalid WHEN request login THEN returns bad request`() {
        testApplication {
            val response = httpClient.requestLogin(password = "InvalidPassword", userData = savedUserData)

            assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status)
        }
    }

    @Test
    fun `GIVEN loginData without userDataSaved WHEN requestLogin THEN returns valid session`() {
        val repository: UserDataSource by inject()

        testApplication {
            val response = httpClient.requestLogin(userData = savedUserData)

            assertEquals(HttpStatusCode.OK, response.status)

            val userSessionCookie = response.userSessionCookie
            assertEquals(expected = TestData.ENCRYPTED_USER_SESSION_DATA_STRING, actual = userSessionCookie)
            assertEquals(expected = expectedUserData, actual = repository.userData())
        }
    }

    private companion object {
        val expectedUserData = TestData.userData.copy(
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
        )
    }
}
