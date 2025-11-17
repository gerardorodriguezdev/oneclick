package oneclick.client.apps.user.core.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import oneclick.client.apps.user.core.generated.resources.Res
import oneclick.client.apps.user.core.generated.resources.appScreen_navigationBar_homesList
import oneclick.client.apps.user.core.generated.resources.appScreen_navigationBar_userSettings
import oneclick.client.shared.navigation.models.routes.HomeRoute
import oneclick.client.shared.ui.components.DefaultSnackbar
import oneclick.client.shared.ui.components.DefaultSnackbarState
import oneclick.client.shared.ui.components.Label
import oneclick.client.shared.ui.previews.dev.MockContent
import oneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import oneclick.client.shared.ui.previews.providers.base.PreviewModel
import oneclick.client.shared.ui.theme.Tokens
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass

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
            NavigationBarRoute.entries.forEach { navigationBarRoute ->
                NavigationRailItem(
                    selected = navigationBarRoute == selectedRoute,
                    onClick = { onNavigationBarClick(navigationBarRoute) },
                    icon = { navigationBarRoute.NavigationIcon() },
                    label = { navigationBarRoute.NavigationLabel() },
                    modifier = Modifier.testTag(navigationBarRoute.testTag),
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
            NavigationBarRoute.entries.forEach { navigationBarRoute ->
                NavigationBarItem(
                    selected = navigationBarRoute == selectedRoute,
                    onClick = { onNavigationBarClick(navigationBarRoute) },
                    icon = { navigationBarRoute.NavigationIcon() },
                    label = { navigationBarRoute.NavigationLabel() },
                    modifier = Modifier.testTag(navigationBarRoute.testTag),
                )
            }
        }
    }
}

@Composable
private fun NavigationBarRoute.NavigationIcon() {
    Icon(
        imageVector = icon,
        contentDescription = stringResource(label),
    )
}

@Composable
private fun NavigationBarRoute.NavigationLabel() {
    Label(
        text = stringResource(label),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = AppScreenConstants.navigationLabelTopPadding)
    )
}

@Immutable
data class AppScreenState(
    val navigationBar: NavigationBar?,
    val snackbarState: SnackbarState?,
) {
    @Immutable
    sealed interface NavigationBar {
        val selectedRoute: NavigationBarRoute

        @Immutable
        data class Start(override val selectedRoute: NavigationBarRoute) : NavigationBar

        @Immutable
        data class Bottom(override val selectedRoute: NavigationBarRoute) : NavigationBar
    }

    @Immutable
    data class SnackbarState(
        val text: String,
        val isError: Boolean,
    )
}

enum class NavigationBarRoute(
    val icon: ImageVector,
    val label: StringResource,
    val route: KClass<*>,
    val testTag: String,
) {
    HOMES_LIST(
        icon = Icons.AutoMirrored.Filled.List,
        label = Res.string.appScreen_navigationBar_homesList,
        route = HomeRoute.HomesList::class,
        testTag = "AppScreen.NavigationItem.HomesList"
    ),
    USER_SETTINGS(
        icon = Icons.Filled.ManageAccounts,
        label = Res.string.appScreen_navigationBar_userSettings,
        route = HomeRoute.UserSettings::class,
        testTag = "AppScreen.NavigationItem.UserSettings"
    ),
}

private object AppScreenConstants {
    val navigationLabelTopPadding = 8.dp
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
