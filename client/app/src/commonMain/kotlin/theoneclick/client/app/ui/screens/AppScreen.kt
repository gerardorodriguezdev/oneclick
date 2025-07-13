package theoneclick.client.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.app.generated.resources.Res
import theoneclick.client.app.generated.resources.appScreen_navigationBar_homesList
import theoneclick.client.app.generated.resources.appScreen_navigationBar_userSettings
import theoneclick.client.app.ui.screens.AppScreenConstants.navigationBarRoutes
import theoneclick.client.shared.navigation.models.routes.HomeRoute.NavigationBarRoute
import theoneclick.client.shared.navigation.models.routes.HomeRoute.NavigationBarRoute.HomesList
import theoneclick.client.shared.navigation.models.routes.HomeRoute.NavigationBarRoute.UserSettings
import theoneclick.client.shared.ui.components.DefaultSnackbar
import theoneclick.client.shared.ui.components.DefaultSnackbarState
import theoneclick.client.shared.ui.components.Label
import theoneclick.client.shared.ui.previews.dev.MockContent
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.theme.Tokens

//TODO: Update nav thing
@Composable
fun AppScreen(
    state: AppScreenState,
    onNavigationBarClick: (navigationBarRoute: NavigationBarRoute) -> Unit,
    onSnackbarShown: () -> Unit,
    content: @Composable () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    state.snackbarState?.let {
                        DefaultSnackbar(
                            state = DefaultSnackbarState(
                                snackbarData = snackbarData,
                                isError = state.snackbarState.isError,
                            )
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.navigationBar is AppScreenState.NavigationBar.Start) {
                    state.navigationBar.StartNavigationBar(
                        onNavigationBarClick = onNavigationBarClick,
                    )
                }

                content()
            }
        },
        bottomBar = {
            if (state.navigationBar is AppScreenState.NavigationBar.Bottom) {
                state.navigationBar.BottomNavigationBar(
                    onNavigationBarClick = onNavigationBarClick,
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    )

    val onSnackbarShown by rememberUpdatedState(onSnackbarShown)
    LaunchedEffect(state.snackbarState) {
        state.snackbarState?.let {
            snackbarHostState.showSnackbar(state.snackbarState.text)
            onSnackbarShown()
        }
    }
}

@Composable
private fun AppScreenState.NavigationBar.Start.StartNavigationBar(
    onNavigationBarClick: (navigationBarRoute: NavigationBarRoute) -> Unit,
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = Tokens.containerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            navigationBarRoutes.forEach { navigationBarRoute ->
                NavigationRailItem(
                    selected = navigationBarRoute == selectedRoute,
                    onClick = { onNavigationBarClick(navigationBarRoute) },
                    icon = { NavigationIcon(navigationBarRoute) },
                    label = { NavigationLabel(navigationBarRoute) },
                    modifier = Modifier.testTag(
                        AppScreenTestTags.navigationItem(navigationBarRoute)
                    ),
                )
            }
        }
    }
}

@Composable
private fun AppScreenState.NavigationBar.Bottom.BottomNavigationBar(
    onNavigationBarClick: (navigationBarRoute: NavigationBarRoute) -> Unit,
) {
    NavigationBar {
        Row(modifier = Modifier.fillMaxWidth()) {
            navigationBarRoutes.forEach { navigationBarRoute ->
                NavigationBarItem(
                    selected = navigationBarRoute == selectedRoute,
                    onClick = { onNavigationBarClick(navigationBarRoute) },
                    icon = { NavigationIcon(navigationBarRoute) },
                    label = { NavigationLabel(navigationBarRoute) },
                    modifier = Modifier.testTag(
                        AppScreenTestTags.navigationItem(navigationBarRoute)
                    ),
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(navigationBarRoute: NavigationBarRoute) {
    val imageVector = when (navigationBarRoute) {
        is HomesList -> Icons.AutoMirrored.Filled.List
        is UserSettings -> Icons.Filled.ManageAccounts
    }

    Icon(
        imageVector = imageVector,
        contentDescription = navigationBarRoute.toLabel(),
    )
}

@Composable
private fun NavigationLabel(navigationBarRoute: NavigationBarRoute) {
    Label(
        text = navigationBarRoute.toLabel(),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = AppScreenConstants.navigationLabelTopPadding)
    )
}

@Composable
private fun NavigationBarRoute.toLabel(): String =
    when (this) {
        is HomesList -> stringResource(Res.string.appScreen_navigationBar_homesList)
        is UserSettings -> stringResource(Res.string.appScreen_navigationBar_userSettings)
    }

data class AppScreenState(
    val navigationBar: NavigationBar?,
    val snackbarState: SnackbarState?,
) {
    sealed interface NavigationBar {
        val selectedRoute: NavigationBarRoute

        data class Start(override val selectedRoute: NavigationBarRoute) : NavigationBar
        data class Bottom(override val selectedRoute: NavigationBarRoute) : NavigationBar
    }

    data class SnackbarState(
        val text: String,
        val isError: Boolean,
    )
}

private object AppScreenConstants {
    val navigationBarRoutes: PersistentList<NavigationBarRoute> = persistentListOf(
        HomesList,
        UserSettings
    )

    val navigationLabelTopPadding = 8.dp
}

object AppScreenTestTags {
    fun navigationItem(navigationBarRoute: NavigationBarRoute): String =
        "AppScreen.NavigationItem.${navigationBarRoute.toTestTag()}"

    private fun NavigationBarRoute.toTestTag(): String =
        when (this) {
            HomesList -> "DevicesList"
            UserSettings -> "UserSettings"
        }
}

@Composable
fun AppScreenPreview(
    previewModel: PreviewModel<AppScreenState>,
    onNavigationBarClick: (navigationBarRoute: NavigationBarRoute) -> Unit = {},
    onSnackbarShown: () -> Unit,
) {
    ScreenPreviewComposable(previewModel) {
        AppScreen(
            state = previewModel.model,
            onNavigationBarClick = onNavigationBarClick,
            onSnackbarShown = onSnackbarShown,
            content = {
                MockContent(modifier = Modifier.fillMaxSize())
            }
        )
    }
}
