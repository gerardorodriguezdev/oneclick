package theoneclick.server.core.testing.base

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import theoneclick.server.core.entrypoint.configureModules
import theoneclick.server.core.models.Path
import theoneclick.server.core.platform.Environment
import theoneclick.server.core.platform.fileSystem
import theoneclick.server.core.plugins.authentication.AuthenticationConstants
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.platform.testDependencies
import theoneclick.shared.testing.timeProvider.FakeTimeProvider
import theoneclick.shared.timeProvider.TimeProvider
import kotlin.test.AfterTest
import io.ktor.server.testing.testApplication as ktorTestApplication

abstract class IntegrationTest {
    val fileSystem = fileSystem()
    val tempDirectory = tempPath()

    @AfterTest
    fun deleteTempDirectory() {
        fileSystem.delete(tempDirectory)
    }

    @Suppress("LongParameterList")
    fun testApplication(
        environment: Environment = TestData.environment,
        timeProvider: TimeProvider = FakeTimeProvider(fakeCurrentTimeInMillis = TestData.CURRENT_TIME_IN_MILLIS),
        externalServicesConfigBlock: ExternalServicesBuilder.() -> Unit = {},
        applicationConfigBlock: suspend ApplicationTestBuilder.() -> Unit = {},
        clientConfigBlock: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit = { followRedirects = false },
        testExecutionBlock: suspend ApplicationScope.() -> Unit
    ) {
        ktorTestApplication {
            application {
                configureModules(
                    testDependencies(environment = environment, timeProvider = timeProvider, directory = tempDirectory)
                )
            }

            externalServices {
                externalServicesConfigBlock()
            }

            applicationConfigBlock.invoke(this)

            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }

                install(HttpCookies)

                clientConfigBlock()
            }

            val scope = object : ApplicationScope {
                override val httpClient: HttpClient = client
            }

            scope.testExecutionBlock()
        }
    }

    interface ApplicationScope {
        val httpClient: HttpClient

        val HttpResponse.userSessionCookie: String?
            get() {
                val cookieHeader = headers[HttpHeaders.SetCookie] ?: return null
                val cookies = parseClientCookiesHeader(cookieHeader)
                return cookies[AuthenticationConstants.USER_SESSION]
            }
    }
}

expect fun IntegrationTest.tempPath(): Path
