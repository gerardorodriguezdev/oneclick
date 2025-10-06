package oneclick.client.features.home.mappers

import oneclick.client.features.home.ui.screens.UserSettingsScreenState

internal fun Boolean.toUserSettingsScreenState(): UserSettingsScreenState =
    UserSettingsScreenState(
        isLoading = this,
    )
