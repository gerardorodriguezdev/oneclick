package oneclick.client.apps.features.home.mappers

import oneclick.client.apps.features.home.ui.screens.UserSettingsScreenState

internal fun Boolean.toUserSettingsScreenState(): UserSettingsScreenState =
    UserSettingsScreenState(
        isLoading = this,
    )
