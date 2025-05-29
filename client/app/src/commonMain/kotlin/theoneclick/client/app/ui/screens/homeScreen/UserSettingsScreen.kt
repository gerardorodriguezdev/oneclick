package theoneclick.client.app.ui.screens.homeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.generated.resources.Res
import theoneclick.client.app.generated.resources.userSettings_snackbar_logout
import theoneclick.client.app.generated.resources.userSettings_snackbar_unknownError
import theoneclick.client.app.generated.resources.userSettings_title_userSettings
import theoneclick.client.app.ui.components.DefaultButton
import theoneclick.client.app.ui.components.DefaultScaffold
import theoneclick.client.app.ui.components.SnackbarState
import theoneclick.client.app.ui.events.homeScreen.UserSettingsEvent
import theoneclick.client.app.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.screens.homeScreen.UserSettingsStateProvider
import theoneclick.client.app.ui.screens.homeScreen.UserSettingsTestTags.TITLE_TEST_TAG
import theoneclick.client.app.ui.states.homeScreen.UserSettingsState

@Composable
fun UserSettingsScreen(
    state: UserSettingsState,
    onEvent: (event: UserSettingsEvent) -> Unit,
) {
    DefaultScaffold(
        snackbarState = SnackbarState(
            text = snackbarText(state.showError),
            isErrorType = state.showError,
            showSnackbar = state.showError || state.showSuccess,
        ),
        onSnackbarShow = { onEvent(UserSettingsEvent.ErrorShown) },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Card(modifier = Modifier.align(Alignment.Center)) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .width(IntrinsicSize.Max),
                ) {
                    Title()

                    DefaultButton(
                        text = stringResource(Res.string.userSettings_snackbar_logout),
                        onClick = {
                            onEvent(UserSettingsEvent.LogoutClicked)
                        },
                        isLoading = state.isLoading,
                        isEnabled = state.isButtonEnabled,
                        modifier = Modifier.widthIn(min = 200.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Title() {
    Text(
        text = stringResource(Res.string.userSettings_title_userSettings),
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TITLE_TEST_TAG),
    )
}

@Composable
private fun snackbarText(isErrorType: Boolean) =
    if (isErrorType) {
        stringResource(Res.string.userSettings_snackbar_unknownError)
    } else {
        stringResource(Res.string.userSettings_snackbar_logout)
    }

object UserSettingsTestTags {
    const val TITLE_TEST_TAG = "UserSettingsScreen.Title"
}

@Composable
fun UserSettingsPreview(previewModel: PreviewModel<UserSettingsState>) {
    ScreenPreviewComposable(previewModel) {
        UserSettingsScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}


@Preview
@Composable
private fun UserSettingsPreviews(
    @PreviewParameter(UserSettingsScreenPreviewProvider::class) previewModel: PreviewModel<UserSettingsState>,
) {
    UserSettingsPreview(previewModel)
}

private class UserSettingsScreenPreviewProvider : PreviewParameterProvider<PreviewModel<UserSettingsState>> {
    override val values: Sequence<PreviewModel<UserSettingsState>> = UserSettingsStateProvider().values
}
