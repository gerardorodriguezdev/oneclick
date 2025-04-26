package theoneclick.client.core.ui.previews.providers.components

import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.core.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.core.ui.previews.providers.components.DefaultSnackbarPreviewModels.DefaultSnackbarModel

class DefaultSnackbarPreviewModels : PreviewModelProvider<DefaultSnackbarModel> {

    override val values: Sequence<PreviewModel<DefaultSnackbarModel>> =
        sequenceOf(
            lightThemeCompactPreviewModel(description = "Error", model = errorDefaultSnackbarModel),
            lightThemeCompactPreviewModel(description = "Success", model = successDefaultSnackbarModel),

            darkThemeCompactPreviewModel(description = "Error", model = errorDefaultSnackbarModel),
            darkThemeCompactPreviewModel(description = "Success", model = successDefaultSnackbarModel),
        )

    companion object {
        val mockSnackbarData = object : SnackbarData {
            override val visuals: SnackbarVisuals = object : SnackbarVisuals {
                override val actionLabel: String? = null
                override val duration: SnackbarDuration = SnackbarDuration.Short
                override val message: String = "Message"
                override val withDismissAction: Boolean = false
            }

            @Suppress("EmptyFunctionBlock")
            override fun dismiss() {}

            @Suppress("EmptyFunctionBlock")
            override fun performAction() {}
        }

        val errorDefaultSnackbarModel = DefaultSnackbarModel(
            snackbarData = mockSnackbarData,
            isError = true,
        )

        val successDefaultSnackbarModel = DefaultSnackbarModel(
            snackbarData = mockSnackbarData,
            isError = false,
        )
    }

    data class DefaultSnackbarModel(
        val snackbarData: SnackbarData,
        val isError: Boolean,
    )
}
