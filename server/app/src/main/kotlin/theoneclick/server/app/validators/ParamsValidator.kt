package theoneclick.server.app.validators

import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.User
import theoneclick.server.app.models.Username
import theoneclick.server.app.platform.SecurityUtils
import theoneclick.server.app.validators.ParamsValidator.RequestLoginValidationResult.InvalidRequestLoginParams
import theoneclick.server.app.validators.ParamsValidator.RequestLoginValidationResult.ValidRequestLogin
import theoneclick.shared.core.validators.*
import theoneclick.shared.timeProvider.TimeProvider

@Suppress("TooManyFunctions")
class ParamsValidator(
    private val timeProvider: TimeProvider,
    private val securityUtils: SecurityUtils,
    private val usersDataSource: UsersDataSource,
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
        val user = usersDataSource.user(Username(username))

        return when {
            user == null -> ValidRequestLogin.RegistrableUser(
                username = username,
                password = password,
            )

            user.username.value != username -> InvalidRequestLoginParams

            !securityUtils.verifyPassword(
                password = password,
                hashedPassword = user.hashedPassword,
            ) -> InvalidRequestLoginParams

            else -> ValidRequestLogin.ValidUser(user)
        }
    }

    fun isUserSessionValid(sessionToken: String): Boolean {
        val user = usersDataSource.user(sessionToken)

        return when {
            user == null -> false
            user.sessionToken == null -> false

            timeProvider.currentTimeMillis() > user.sessionToken.creationTimeInMillis +
                    USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS -> false

            user.sessionToken.value != sessionToken -> false
            else -> true
        }
    }

    sealed interface RequestLoginValidationResult {
        sealed interface ValidRequestLogin : RequestLoginValidationResult {
            data class ValidUser(val user: User) : ValidRequestLogin

            data class RegistrableUser(
                val username: String,
                val password: String,
            ) : ValidRequestLogin
        }

        data object InvalidRequestLoginParams : RequestLoginValidationResult
    }

    companion object {
        const val USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS = 3_600_000L
    }
}
