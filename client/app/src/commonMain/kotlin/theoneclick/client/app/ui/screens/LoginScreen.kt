package theoneclick.client.app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.app.generated.resources.*
import theoneclick.client.app.ui.events.LoginEvent
import theoneclick.client.app.ui.screens.LoginScreenTestTags.PASSWORD_PLACEHOLDER
import theoneclick.client.app.ui.screens.LoginScreenTestTags.PASSWORD_TEXT_FIELD
import theoneclick.client.app.ui.screens.LoginScreenTestTags.USERNAME_PLACEHOLDER
import theoneclick.client.app.ui.screens.LoginScreenTestTags.USERNAME_TEXT_FIELD
import theoneclick.client.shared.ui.components.Body
import theoneclick.client.shared.ui.components.DefaultButton
import theoneclick.client.shared.ui.components.DialogBox
import theoneclick.client.shared.ui.components.ScreenBox
import theoneclick.client.shared.ui.models.Field
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel

@Composable
fun LoginScreen(
    state: LoginScreenState,
    onEvent: (event: LoginEvent) -> Unit,
) {
    ScreenBox {
        DialogBox(header = stringResource(Res.string.loginScreen_title_register)) {
            UsernameTextField(
                username = state.username.text,
                isUsernameValid = state.username.isValid != false,
                onUsernameChange = { newUsername -> onEvent(LoginEvent.UsernameChanged(newUsername)) },
            )

            PasswordTextField(
                password = state.password.text,
                isPasswordValid = state.password.isValid != false,
                onPasswordChange = { newPassword -> onEvent(LoginEvent.PasswordChanged(newPassword)) },
            )

            DefaultButton(
                text = stringResource(Res.string.loginScreen_registerButton_register),
                isEnabled = state.isRegisterButtonEnabled,
                isLoading = state.isLoading,
                onClick = { onEvent(LoginEvent.RegisterButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun UsernameTextField(
    username: String,
    isUsernameValid: Boolean,
    onUsernameChange: (String) -> Unit
) {
    OutlinedTextField(
        placeholder = {
            Body(
                text = stringResource(Res.string.loginScreen_usernameTextField_username),
                modifier = Modifier.testTag(USERNAME_PLACEHOLDER)
            )
        },
        value = username,
        onValueChange = onUsernameChange,
        isError = !isUsernameValid,
        maxLines = 1,
        modifier = Modifier.testTag(USERNAME_TEXT_FIELD),
    )
}

@Composable
private fun PasswordTextField(
    password: String,
    isPasswordValid: Boolean,
    onPasswordChange: (String) -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        placeholder = {
            Body(
                text = stringResource(Res.string.loginScreen_passwordTextField_password),
                modifier = Modifier.testTag(PASSWORD_PLACEHOLDER)
            )
        },
        trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = null,
                )
            }
        },
        value = password,
        onValueChange = onPasswordChange,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        isError = !isPasswordValid,
        maxLines = 1,
        modifier = Modifier
            .semantics { password() }
            .testTag(PASSWORD_TEXT_FIELD),
    )
}

object LoginScreenTestTags {
    const val USERNAME_TEXT_FIELD = "LoginScreen.Username.TextField"
    const val USERNAME_PLACEHOLDER = "LoginScreen.Username.Placeholder"
    const val PASSWORD_TEXT_FIELD = "LoginScreen.Password.TextField"
    const val PASSWORD_PLACEHOLDER = "LoginScreen.Password.Placeholder"
}

data class LoginScreenState(
    val username: Field = Field(),
    val password: Field = Field(),
    val isRegisterButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
)

@Composable
fun LoginScreenPreview(previewModel: PreviewModel<LoginScreenState>) {
    ScreenPreviewComposable(previewModel) {
        LoginScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}