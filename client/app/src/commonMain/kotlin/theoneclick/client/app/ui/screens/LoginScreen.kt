package theoneclick.client.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.app.generated.resources.*
import theoneclick.client.shared.ui.components.DefaultButton
import theoneclick.client.shared.ui.components.DefaultScaffold
import theoneclick.client.shared.ui.components.SnackbarState
import theoneclick.client.app.ui.events.LoginEvent
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.screens.LoginScreenTestTags.PASSWORD_PLACEHOLDER_TEST_TAG
import theoneclick.client.app.ui.screens.LoginScreenTestTags.PASSWORD_TEXT_FIELD_TEST_TAG
import theoneclick.client.app.ui.screens.LoginScreenTestTags.TITLE_TEST_TAG
import theoneclick.client.app.ui.screens.LoginScreenTestTags.USERNAME_PLACEHOLDER_TEST_TAG
import theoneclick.client.app.ui.screens.LoginScreenTestTags.USERNAME_TEXT_FIELD_TEST_TAG
import theoneclick.client.app.ui.states.LoginState

@Composable
fun LoginScreen(
    state: LoginState,
    onEvent: (event: LoginEvent) -> Unit,
) {
    DefaultScaffold(
        snackbarState = SnackbarState(
            text = stringResource(Res.string.loginScreen_snackbar_unknownError),
            isErrorType = true,
            showSnackbar = state.showError,
        ),
        onSnackbarShow = { onEvent(LoginEvent.ErrorShown) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier.align(Alignment.Center)
            ) {
                FormContent(
                    username = state.username,
                    onUsernameChange = { newUsername -> onEvent(LoginEvent.UsernameChanged(newUsername)) },
                    isUsernameValid = state.isUsernameValid != false,
                    password = state.password,
                    onPasswordChange = { newPassword -> onEvent(LoginEvent.PasswordChanged(newPassword)) },
                    isPasswordValid = state.isPasswordValid != false,
                    onRegisterButtonClick = { onEvent(LoginEvent.RegisterButtonClicked) },
                    isRegisterButtonEnabled = state.isRegisterButtonEnabled,
                    isLoading = state.isLoading,
                )
            }
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun FormContent(
    username: String,
    onUsernameChange: (username: String) -> Unit,
    isUsernameValid: Boolean,
    password: String,
    onPasswordChange: (password: String) -> Unit,
    isPasswordValid: Boolean,
    onRegisterButtonClick: () -> Unit,
    isRegisterButtonEnabled: Boolean,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Title()

        UsernameTextField(
            username = username,
            onUsernameChange = onUsernameChange,
            isUsernameValid = isUsernameValid,
        )

        PasswordTextField(
            password = password,
            onPasswordChange = onPasswordChange,
            isPasswordValid = isPasswordValid,
        )

        DefaultButton(
            text = stringResource(Res.string.loginScreen_registerButton_register),
            onClick = onRegisterButtonClick,
            isEnabled = isRegisterButtonEnabled,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Title() {
    Text(
        text = stringResource(Res.string.loginScreen_title_register),
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TITLE_TEST_TAG),
    )
}

@Composable
private fun UsernameTextField(
    username: String,
    onUsernameChange: (String) -> Unit,
    isUsernameValid: Boolean
) {
    OutlinedTextField(
        placeholder = {
            Text(
                text = stringResource(Res.string.loginScreen_usernameTextField_username),
                modifier = Modifier.testTag(USERNAME_PLACEHOLDER_TEST_TAG)
            )
        },
        value = username,
        onValueChange = onUsernameChange,
        isError = !isUsernameValid,
        maxLines = 1,
        modifier = Modifier.testTag(USERNAME_TEXT_FIELD_TEST_TAG),
    )
}

@Composable
private fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordValid: Boolean
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        placeholder = {
            Text(
                text = stringResource(Res.string.loginScreen_passwordTextField_password),
                modifier = Modifier.testTag(PASSWORD_PLACEHOLDER_TEST_TAG)
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
        modifier = Modifier.testTag(PASSWORD_TEXT_FIELD_TEST_TAG),
    )
}

object LoginScreenTestTags {
    const val TITLE_TEST_TAG = "LoginScreen.Title"
    const val USERNAME_TEXT_FIELD_TEST_TAG = "LoginScreen.Username.TextField"
    const val USERNAME_PLACEHOLDER_TEST_TAG = "LoginScreen.Username.Placeholder"
    const val PASSWORD_TEXT_FIELD_TEST_TAG = "LoginScreen.Password.TextField"
    const val PASSWORD_PLACEHOLDER_TEST_TAG = "LoginScreen.Password.Placeholder"
}

@Composable
fun LoginScreenPreview(previewModel: PreviewModel<LoginState>) {
    ScreenPreviewComposable(previewModel) {
        LoginScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}