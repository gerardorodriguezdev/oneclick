package theoneclick.server.core.validators

import theoneclick.server.core.endpoints.requestLogin.RequestLoginParams
import theoneclick.server.core.models.UserData
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
                    userData = null,
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
                    userData = TestData.userData,
                ),
            ),

            block = { index, input ->
                val paramsValidator = paramsValidator(
                    userData = input.userData,
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
                    userData = null,
                ),
                expected = false,
            ),
            testScenario(
                input = UserSessionValidScenario(
                    userData = TestData.userData.copy(
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
                    userData = input.userData,
                )
                paramsValidator.isUserSessionValid(input.userSession)
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
                input = AddDeviceValidScenario(userData = null),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),
            testScenario(
                input = AddDeviceValidScenario(),
                expected = AddDeviceRequestValidationResult.InvalidDevice,
            ),

            // Valid
            testScenario(
                input = AddDeviceValidScenario(
                    userData = TestData.userData.copy(
                        devices = emptyList()
                    )
                ),
                expected = AddDeviceRequestValidationResult.ValidDevice(
                    userData = TestData.userData.copy(
                        devices = emptyList()
                    ),
                    deviceName = TestData.DEVICE_NAME,
                    room = TestData.ROOM,
                    deviceType = DeviceType.BLIND,
                ),
            ),
            block = { index, input ->
                val paramsValidator = paramsValidator(userData = input.userData)
                paramsValidator.isAddDeviceRequestValid(input.addDeviceRequest)
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
                input = UpdateDeviceValidScenario(userData = null),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(userData = TestData.userData.copy(devices = emptyList())),
                expected = UpdateDeviceValidationResult.InvalidDevice,
            ),
            testScenario(
                input = UpdateDeviceValidScenario(
                    userData = TestData.userData.copy(
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
                    userData = TestData.userData.copy(
                        devices = listOf(TestData.blind.copy(isOpened = true))
                    ),
                ),
            ),
            block = { index, input ->
                val paramsValidator = paramsValidator(userData = input.userData)
                paramsValidator.isUpdateDeviceRequestValid(input.updateDevideRequest)
            }
        )
    }

    companion object {
        private fun paramsValidator(
            currentTimeInMillis: Long = TestData.CURRENT_TIME_IN_MILLIS,
            userData: UserData? = TestData.userData,
            isPasswordValid: Boolean = false,
        ): ParamsValidator =
            ParamsValidator(
                timeProvider = FakeTimeProvider(currentTimeInMillis),
                securityUtils = FakeSecurityUtils(
                    verifyPasswordResult = isPasswordValid,
                ),
                userDataSource = FakeUserDataSource(userData),
            )

        data class RequestLoginParamsScenario(
            private val username: String = TestData.USERNAME,
            private val password: String = TestData.RAW_PASSWORD,

            val userData: UserData? = TestData.userData,
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
            val userData: UserData? = TestData.userData,
        ) {
            val userSession = UserSession(userSessionToken)
        }

        data class AddDeviceValidScenario(
            private val deviceName: String = TestData.DEVICE_NAME,
            private val room: String = TestData.ROOM,
            private val type: DeviceType = DeviceType.BLIND,

            val userData: UserData? = TestData.userData,
        ) {
            val addDeviceRequest = AddDeviceRequest(
                deviceName = deviceName,
                room = room,
                type = type,
            )
        }

        data class UpdateDeviceValidScenario(
            private val updatedDevice: Device = TestData.blind.copy(isOpened = true),

            val userData: UserData? = TestData.userData.copy(
                devices = listOf(updatedDevice),
            ),
        ) {
            val updateDevideRequest = UpdateDeviceRequest(
                updatedDevice = updatedDevice,
            )
        }
    }
}
