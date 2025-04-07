package theoneclick.server.core.validators

import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.endpoints.requestLogin.RequestLoginParams
import theoneclick.server.core.models.UserData
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.core.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.core.models.requests.AddDeviceRequest
import theoneclick.shared.core.models.requests.UpdateDeviceRequest
import theoneclick.shared.core.validators.*
import theoneclick.shared.timeProvider.TimeProvider

@Suppress("TooManyFunctions")
class ParamsValidator(
    private val timeProvider: TimeProvider,
    private val securityUtils: SecurityUtils,
    private val userDataSource: UserDataSource,
) {

    fun isRequestLoginParamsValid(requestLoginParams: RequestLoginParams): RequestLoginValidationResult {
        val username = requestLoginParams.username
        val password = requestLoginParams.password

        return when {
            usernameValidator.isNotValid(username) -> InvalidRequestLoginParams
            passwordValidator.isNotValid(password) -> InvalidRequestLoginParams
            else -> isLoginValid(
                username = username,
                password = password,
            )
        }
    }

    private fun isLoginValid(
        username: String,
        password: String,
    ): RequestLoginValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> ValidRequestLogin.RegistrableUser(
                username = username,
                password = password,
            )

            userData.username != username -> InvalidRequestLoginParams

            !securityUtils.verifyPassword(
                password = password,
                hashedPassword = userData.hashedPassword,
            ) -> InvalidRequestLoginParams

            else -> ValidRequestLogin.ValidUser(userData)
        }
    }

    fun isUserSessionValid(userSession: UserSession): Boolean {
        val userData = userDataSource.userData()

        return when {
            userData == null -> false
            userData.sessionToken == null -> false

            timeProvider.currentTimeMillis() > userData.sessionToken.creationTimeInMillis +
                USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS -> false

            userData.sessionToken.value != userSession.sessionToken -> false
            else -> true
        }
    }

    fun isAddDeviceRequestValid(addDeviceRequest: AddDeviceRequest): AddDeviceRequestValidationResult {
        return when {
            roomNameValidator.isNotValid(addDeviceRequest.room) -> AddDeviceRequestValidationResult.InvalidDevice
            deviceNameValidator.isNotValid(addDeviceRequest.deviceName) ->
                AddDeviceRequestValidationResult.InvalidDevice

            else -> addDeviceRequest.handleUserDataValidationForAddDeviceRequest()
        }
    }

    private fun AddDeviceRequest.handleUserDataValidationForAddDeviceRequest(): AddDeviceRequestValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> AddDeviceRequestValidationResult.InvalidDevice
            userData.hasDevice(deviceName) -> AddDeviceRequestValidationResult.InvalidDevice
            else -> AddDeviceRequestValidationResult.ValidDevice(
                userData = userData,
                deviceName = deviceName,
                room = room,
                deviceType = type,
            )
        }
    }

    fun isUpdateDeviceRequestValid(updateDeviceRequest: UpdateDeviceRequest): UpdateDeviceValidationResult {
        val device = updateDeviceRequest.updatedDevice

        return when {
            !device.isDeviceValid() -> UpdateDeviceValidationResult.InvalidDevice
            else -> device.handleDeviceUpdate()
        }
    }

    private fun Device.isDeviceValid(): Boolean =
        when {
            deviceNameValidator.isNotValid(deviceName) -> false
            roomNameValidator.isNotValid(room) -> false
            deviceIdValidator.isNotValid(id.value) -> false
            else -> when (this) {
                is Device.Blind -> isDeviceValid()
            }
        }

    private fun Device.Blind.isDeviceValid(): Boolean = Device.Blind.blindRange.isOnRange(rotation)

    private fun Device.handleDeviceUpdate(): UpdateDeviceValidationResult {
        val userData = userDataSource.userData()

        return when {
            userData == null -> UpdateDeviceValidationResult.InvalidDevice
            !userData.canUpdateDevice(this) -> UpdateDeviceValidationResult.InvalidDevice
            else -> UpdateDeviceValidationResult.ValidDevice(
                userData = userData,
                updatedDevice = this,
            )
        }
    }

    sealed interface RequestLoginValidationResult {
        sealed interface ValidRequestLogin : RequestLoginValidationResult {
            data class ValidUser(val userData: UserData) : ValidRequestLogin

            data class RegistrableUser(
                val username: String,
                val password: String,
            ) : ValidRequestLogin
        }

        data object InvalidRequestLoginParams : RequestLoginValidationResult
    }

    sealed interface AddDeviceRequestValidationResult {
        data class ValidDevice(
            val userData: UserData,
            val deviceName: String,
            val room: String,
            val deviceType: DeviceType,
        ) : AddDeviceRequestValidationResult

        data object InvalidDevice : AddDeviceRequestValidationResult
    }

    sealed interface UpdateDeviceValidationResult {
        data class ValidDevice(
            val userData: UserData,
            val updatedDevice: Device,
        ) : UpdateDeviceValidationResult

        data object InvalidDevice : UpdateDeviceValidationResult
    }

    companion object {
        const val USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS = 3_600_000L
    }
}
