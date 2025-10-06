package oneclick.client.features.home.ui.screens

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import oneclick.client.features.home.generated.resources.Res
import oneclick.client.features.home.generated.resources.userSettingsScreen_snackbar_logout
import oneclick.client.features.home.generated.resources.userSettingsScreen_title_userSettings
import oneclick.client.shared.ui.components.DefaultButton
import oneclick.client.shared.ui.components.DialogBox
import oneclick.client.shared.ui.components.ScreenBox
import oneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import oneclick.client.shared.ui.previews.providers.base.PreviewModel

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
