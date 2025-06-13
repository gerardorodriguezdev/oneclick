package theoneclick.client.features.home.mappers

import theoneclick.client.features.home.ui.screens.UserSettingsScreenState

internal fun Boolean.toUserSettingsScreenState(): UserSettingsScreenState =
    UserSettingsScreenState(
        isLoading = this,
    )