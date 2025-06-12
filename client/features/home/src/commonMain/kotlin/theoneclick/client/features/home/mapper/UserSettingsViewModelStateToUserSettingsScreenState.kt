package theoneclick.client.features.home.mapper

import theoneclick.client.features.home.ui.screens.UserSettingsScreenState

internal fun Boolean.toUserSettingsScreenState(): UserSettingsScreenState =
    UserSettingsScreenState(
        isButtonEnabled = !this,
        isLoading = this,
    )