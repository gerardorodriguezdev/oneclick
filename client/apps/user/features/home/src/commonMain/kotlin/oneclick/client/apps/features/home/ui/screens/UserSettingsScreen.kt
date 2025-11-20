package oneclick.client.apps.features.home.ui.screens

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import oneclick.client.apps.user.features.home.generated.resources.Res
import oneclick.client.apps.user.features.home.generated.resources.userSettingsScreen_snackbar_logout
import oneclick.client.apps.user.features.home.generated.resources.userSettingsScreen_title_userSettings
import oneclick.client.apps.user.ui.components.DefaultButton
import oneclick.client.apps.user.ui.components.DialogBox
import oneclick.client.apps.user.ui.components.ScreenBox
import oneclick.client.apps.user.ui.previews.dev.ScreenPreviewComposable
import oneclick.client.apps.user.ui.previews.providers.base.PreviewModel
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UserSettingsScreen(
    state: UserSettingsScreenState,
    onEvent: (event: UserSettingsEvent) -> Unit,
) {
    ScreenBox {
        DialogBox(header = stringResource(Res.string.userSettingsScreen_title_userSettings)) {
            DefaultButton(
                text = stringResource(Res.string.userSettingsScreen_snackbar_logout),
                onClick = {
                    onEvent(UserSettingsEvent.LogoutClicked)
                },
                isLoading = state.isLoading,
                isEnabled = !state.isLoading,
                modifier = Modifier.widthIn(min = 200.dp),
            )
        }
    }
}

@Immutable
internal data class UserSettingsScreenState(
    val isLoading: Boolean = false,
)

internal sealed interface UserSettingsEvent {
    data object LogoutClicked : UserSettingsEvent
}

@Composable
internal fun UserSettingsScreenPreview(previewModel: PreviewModel<UserSettingsScreenState>) {
    ScreenPreviewComposable(previewModel) {
        UserSettingsScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}
