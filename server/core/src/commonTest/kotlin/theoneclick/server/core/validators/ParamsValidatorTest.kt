package theoneclick.server.core.validators

import theoneclick.server.core.endpoints.requestLogin.RequestLoginParams
import theoneclick.server.core.models.User
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.fakes.FakeSecurityUtils
import theoneclick.server.core.testing.fakes.FakeUserDataSource
import theoneclick.server.core.validators.ParamsValidator.AddDeviceRequestValidationResult
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.server.core.validators.ParamsValidator.UpdateDeviceValidationResult
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.entities.Uuid
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.testing.extensions.generateLongString
import theoneclick.shared.testing.extensions.parameterizedTest
import theoneclick.shared.testing.models.testScenario
import theoneclick.shared.testing.timeProvider.FakeTimeProvider
import kotlin.test.Test

class ParamsValidatorTest : IntegrationTest() {

    @Suppress("LongMethod")
    @Test
    fun `GIVEN testScenario WHEN isRequestLoginParamsValid THEN returns expected`() {
        parameterizedTest(
            // Username
            testScenario(
                input = RequestLoginParamsScenario(username = ""),
                expected = InvalidRequestLoginParams,
            ),
            testScenario(
                input = RequestLoginParamsScenario(username = TestData.USERNAME + '1'),
                expected = InvalidRequestLoginParams,
            ),
            testScenario(
                input = RequestLoginParamsScenario(
                    username = TestData.USERNAME + 'a',
                ),
                expected = InvalidRequestLoginParams,
            ),

            // Password
            testScenario(
                input = RequestLoginParamsScenario(
                    password = "",
                    isPasswordValid = false,
                ),
                expected = InvalidRequestLoginParams,
            ),
            testScenario(
                input = RequestLoginParamsScenario(
                    password = TestData.RAW_PASSWORD + '/',
                    isPasswordValid = false,
                ),
                expected = InvalidRequestLoginParams,
            ),
            testScenario(
                input = RequestLoginParamsScenario(
                    password = TestData.RAW_PASSWORD + '1',
                    isPasswordValid = false,
                ),
                expected = InvalidRequestLoginParams,
            ),

            // Valid
            testScenario(
                input = RequestLoginParamsScenario(
                    user = null,
                ),
                expected = ValidRequestLogin.RegistrableUser(
                    username = TestData.USERNAME,
                    password = TestData.RAW_PASSWORD,
                ),
            ),
            testScenario(
                input = RequestLoginParamsScenario(
                    isPasswordValid = true,
                ),
                expected = ValidRequestLogin.ValidUser(
                    user = TestData.user,
                ),
            ),

            block = { index, input ->
                val paramsValidator = paramsValidator(
                    user = input.user,
                    isPasswordValid = input.isPasswordValid,
                )
                paramsValidator.isRequestLoginParamsValid(input.requestLoginParams)
            }
        )
    }

    @Test
    fun `GIVEN testScenario WHEN isUserSessionValid THEN returns expected`() {
        parameterizedTest(
            // Invalid
            testScenario(
                input = UserSessionValidScenario(
                    user = null,
                ),
                expected = false,
            ),
            testScenario(
                input = UserSessionValidScenario(
                    user = TestData.user.copy(
                        sessionToken = null,
                    ),
                ),
                expected = false,
            ),
            testScenario(
                input = UserSessionValidScenario(
                    currentTimeInMillis =
                        TestData.CURRENT_TIME_IN_MILLIS + ParamsValidator.USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS + 1
                ),
                expected = false,
            ),
            testScenario(
                input = UserSessionValidScenario(
                    userSessionToken = TestData.ENCRYPTED_TOKEN_VALUE + '=',
                ),
                expected = false,
            ),

            // Valid
            testScenario(
                input = UserSessionValidScenario(),
                expected = true,
            ),

            block = { index, input ->
                val paramsValidator = paramsValidator(
                    currentTimeInMillis = input.currentTimeInMillis,
                    user = input.user,
                )
                paramsValidator.isUserSessionValid(input.userSession.sessionToken)
            }
        )
    }

    @Test
    fun `GIVEN testScenario WHEN isAddDeviceRequestValid THEN returns expected`() {
        parameterizedTest(
            // Invalid Room
            testScenario(
                input = AddDeviceValidScenario(room = generateLongString(2)),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),
            testScenario(
                input = AddDeviceValidScenario(room = generateLongString(21)),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),
            testScenario(
                input = AddDeviceValidScenario(room = TestData.ROOM + "1"),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),

            // Invalid DeviceName
            testScenario(
                input = AddDeviceValidScenario(deviceName = generateLongString(2)),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),
            testScenario(
                input = AddDeviceValidScenario(deviceName = generateLongString(21)),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),
            testScenario(
                input = AddDeviceValidScenario(deviceName = TestData.DEVICE_NAME + "!"),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),

            // Invalid user data
            testScenario(
                input = AddDeviceValidScenario(user = null),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),
            testScenario(
                input = AddDeviceValidScenario(),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),

            // Valid
            testScenario(
                input = AddDeviceValidScenario(
                    user = TestData.user.copy(
                        devices = emptyList()
                    )
                ),
                expected = AddDeviceRequestValidationResult.ValidDevice(
                    user = TestData.user.copy(
                        devices = emptyList()
                    ),
                    deviceName = TestData.DEVICE_NAME,
                    room = TestData.ROOM,
                    deviceType = DeviceType.BLIND,
                ),
            ),
            block = { index, input ->
                val paramsValidator = paramsValidator(user = input.user)
                paramsValidator.isAddDeviceRequestValid(
                    sessionToken = input.user?.sessionToken?.value,
                    addDeviceRequest = input.addDeviceRequest,
                )
            }
        )
    }

    @Suppress("LongMethod")
    @Test
    fun `GIVEN testScenario WHEN isUpdateDeviceRequestValid THEN returns expected`() {
        parameterizedTest(
            // Invalid Device Id
            testScenario(
                input = UpdateDeviceValidScenario(updatedDevice = TestData.blind.copy(id = Uuid(""))),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(updatedDevice = TestData.blind.copy(id = Uuid("A"))),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(
                    updatedDevice = TestData.blind.copy(
                        id = Uuid(TestData.UUID + "a")
                    )
                ),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(
                    updatedDevice = TestData.blind.copy(
                        id = Uuid(TestData.UUID.dropLast(1))
                    )
                ),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),

            // Invalid Room
            testScenario(
                input = UpdateDeviceValidScenario(updatedDevice = TestData.blind.copy(room = "")),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(
                    updatedDevice = TestData.blind.copy(
                        room = generateLongString(21)
                    )
                ),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(updatedDevice = TestData.blind.copy(room = TestData.ROOM + "1")),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),

            // Invalid DeviceName
            testScenario(
                input = UpdateDeviceValidScenario(
                    updatedDevice = TestData.blind.copy(
                        deviceName = generateLongString(2)
                    )
                ),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(
                    updatedDevice = TestData.blind.copy(
                        deviceName = generateLongString(21)
                    )
                ),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(
                    updatedDevice = TestData.blind.copy(
                        deviceName = TestData.DEVICE_NAME + "!"
                    )
                ),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),

            // Invalid Rotation
            testScenario(
                input = UpdateDeviceValidScenario(updatedDevice = TestData.blind.copy(rotation = -1)),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(updatedDevice = TestData.blind.copy(rotation = 181)),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),

            // Invalid user data
            testScenario(
                input = UpdateDeviceValidScenario(user = null),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(user = TestData.user.copy(devices = emptyList())),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(
                    user = TestData.user.copy(
                        devices = listOf(
                            TestData.blind.copy(
                                id = Uuid(
                                    value = TestData.UUID.replace(
                                        "-",
                                        "1"
                                    )
                                ),
                            ),
                        )
                    )
                ),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),

            // Valid
            testScenario(
                input = UpdateDeviceValidScenario(),
                expected = UpdateDeviceValidationResult.ValidDevice(
                    updatedDevice = TestData.blind.copy(isOpened = true),
                    user = TestData.user.copy(
                        devices = listOf(TestData.blind.copy(isOpened = true))
                    ),
                ),
            ),
            block = { index, input ->
                val paramsValidator = paramsValidator(user = input.user)
                paramsValidator.isUpdateDeviceRequestValid(
                    sessionToken = input.user?.sessionToken?.value,
                    updateDeviceRequest = input.updateDevideRequest
                )
            }
        )
    }

    companion object {
        private fun paramsValidator(
            currentTimeInMillis: Long = TestData.CURRENT_TIME_IN_MILLIS,
            user: User? = TestData.user,
            isPasswordValid: Boolean = false,
        ): ParamsValidator =
            ParamsValidator(
                timeProvider = FakeTimeProvider(currentTimeInMillis),
                securityUtils = FakeSecurityUtils(
                    verifyPasswordResult = isPasswordValid,
                ),
                userDataSource = FakeUserDataSource(user),
            )

        data class RequestLoginParamsScenario(
            private val username: String = TestData.USERNAME,
            private val password: String = TestData.RAW_PASSWORD,

            val user: User? = TestData.user,
            val isPasswordValid: Boolean = false,
        ) {
            val requestLoginParams = RequestLoginParams(
                username = username,
                password = password,
            )
        }

        data class UserSessionValidScenario(
            private val userSessionToken: String = TestData.ENCRYPTED_TOKEN_VALUE,

            val currentTimeInMillis: Long = TestData.CURRENT_TIME_IN_MILLIS,
            val user: User? = TestData.user,
        ) {
            val userSession = UserSession(userSessionToken)
        }

        data class AddDeviceValidScenario(
            private val deviceName: String = TestData.DEVICE_NAME,
            private val room: String = TestData.ROOM,
            private val type: DeviceType = DeviceType.BLIND,

            val user: User? = TestData.user,
        ) {
            val addDeviceRequest = AddDeviceRequest(
                deviceName = deviceName,
                room = room,
                type = type,
            )
        }

        data class UpdateDeviceValidScenario(
            private val updatedDevice: Device = TestData.blind.copy(isOpened = true),

            val user: User? = TestData.user.copy(
                devices = listOf(updatedDevice),
            ),
        ) {
            val updateDevideRequest = UpdateDeviceRequest(
                updatedDevice = updatedDevice,
            )
        }
    }
}
