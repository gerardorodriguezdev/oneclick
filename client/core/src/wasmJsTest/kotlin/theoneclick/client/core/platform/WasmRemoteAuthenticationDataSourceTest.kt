package theoneclick.client.core.platform

//TODO: Finish
class WasmRemoteAuthenticationDataSourceTest {

/*
    @Test
    fun `GIVEN user logged WHEN isUserLogged called THEN returns logged`() {
        runTest {
            val remoteAuthenticationDataSource =
                remoteAuthenticationDataSource(client = userLoggedEndpointMockHttpClient(UserLoggedResponse.Logged))

            remoteAuthenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.Logged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN isUserLogged called THEN returns not logged`() {
        runTest {
            val remoteAuthenticationDataSource =
                remoteAuthenticationDataSource(client = userLoggedEndpointMockHttpClient(UserLoggedResponse.NotLogged))

            remoteAuthenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.NotLogged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN server error WHEN isUserLogged called THEN returns unknown error`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = defaultHttpClient(
                    mockEngine(
                        pathToFake = ClientEndpoint.IS_USER_LOGGED.route,
                        onPathFound = { respondError(HttpStatusCode.BadRequest) },
                    )
                )
            )

            remoteAuthenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN valid data with local redirect WHEN requestLogin THEN returns validLocalRedirect`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = requestLoginEndpointMockHttpClient()
            )

            remoteAuthenticationDataSource.requestLogin(username = USERNAME, password = PASSWORD).test {
                assertEquals(RequestLoginResult.ValidLogin, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN invalid data WHEN requestLogin THEN returns unknown error`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = requestLoginEndpointMockHttpClient()
            )

            remoteAuthenticationDataSource.requestLogin(username = "", password = "").test {
                assertEquals(RequestLoginResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN error WHEN requestLogin THEN returns unknown error`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = defaultHttpClient(
                    mockEngine(
                        pathToFake = ClientEndpoint.REQUEST_LOGIN.route,
                        onPathFound = { respondError(HttpStatusCode.InternalServerError) },
                    )
                )
            )

            remoteAuthenticationDataSource.requestLogin(username = USERNAME, password = PASSWORD).test {
                assertEquals(RequestLoginResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    companion object {
        const val USERNAME = "Username1"
        const val PASSWORD = "Password1"

        private fun TestScope.remoteAuthenticationDataSource(client: HttpClient): RemoteAuthenticationManager =
            RemoteAuthenticationManager(
                dispatchersProvider = FakeDispatchersProvider(StandardTestDispatcher(testScheduler)),
                client = client,
                idlingResource = EmptyIdlingResource(),
            )

        private fun userLoggedEndpointMockHttpClient(result: UserLoggedResponse): HttpClient =
            defaultHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoint.IS_USER_LOGGED.route,
                    onPathFound = {
                        respondJson<UserLoggedResponse>(result)
                    },
                )
            )

        private fun requestLoginEndpointMockHttpClient(): HttpClient =
            defaultHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoint.REQUEST_LOGIN.route,
                    onPathFound = { request ->
                        val requestLoginRequest = request.toRequestBodyObject<RequestLoginRequest>()

                        when {
                            requestLoginRequest == null -> respondError(HttpStatusCode.BadRequest)
                            requestLoginRequest.username != USERNAME -> respondError(HttpStatusCode.BadRequest)
                            requestLoginRequest.password != PASSWORD -> respondError(HttpStatusCode.BadRequest)
                            else -> respondOk()
                        }
                    },
                )
            )

        private fun defaultHttpClient(mockEngine: MockEngine): HttpClient =
            defaultHttpClient(engine = mockEngine, protocol = null, host = null, port = null)
    }
*/
}