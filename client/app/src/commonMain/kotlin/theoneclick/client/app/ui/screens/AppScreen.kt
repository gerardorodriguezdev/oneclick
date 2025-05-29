package theoneclick.client.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.app.generated.resources.Res
import theoneclick.client.app.generated.resources.homeScreen_navigationBar_addDevice
import theoneclick.client.app.generated.resources.homeScreen_navigationBar_devicesList
import theoneclick.client.app.generated.resources.homeScreen_navigationBar_userSettings
import theoneclick.client.app.ui.previews.dev.MockContent
import theoneclick.client.app.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.screens.AppScreenConstants.navigationBarRoutes
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute.*

@Composable
fun AppScreen(
    state: AppScreenState,
    onNavigationBarClick: (navigationBarRoute: NavigationBarRoute) -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
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
                .padding(horizontal = 16.dp),
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
                        AppScreenTestTags.navigationItemTestTag(navigationBarRoute)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            navigationBarRoutes.forEach { navigationBarRoute ->
                NavigationBarItem(
                    selected = navigationBarRoute == selectedRoute,
                    onClick = { onNavigationBarClick(navigationBarRoute) },
                    icon = { NavigationIcon(navigationBarRoute) },
                    label = { NavigationLabel(navigationBarRoute) },
                    modifier = Modifier.testTag(
                        AppScreenTestTags.navigationItemTestTag(navigationBarRoute)
                    ),
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(navigationBarRoute: NavigationBarRoute) {
    val imageVector = when (navigationBarRoute) {
        is DevicesList -> Icons.AutoMirrored.Filled.List
        is AddDevice -> Icons.Filled.Add
        is UserSettings -> Icons.Filled.ManageAccounts
    }

    Icon(
        imageVector = imageVector,
        contentDescription = navigationBarRoute.toLabel(),
    )
}

@Composable
private fun NavigationLabel(navigationBarRoute: NavigationBarRoute) {
    Text(
        text = navigationBarRoute.toLabel(),
        fontSize = MaterialTheme.typography.labelSmall.fontSize,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun NavigationBarRoute.toLabel(): String =
    when (this) {
        is DevicesList -> stringResource(Res.string.homeScreen_navigationBar_devicesList)
        is AddDevice -> stringResource(Res.string.homeScreen_navigationBar_addDevice)
        is UserSettings -> stringResource(Res.string.homeScreen_navigationBar_userSettings)
    }

data class AppScreenState(val navigationBar: NavigationBar?) {
    sealed interface NavigationBar {
        val selectedRoute: NavigationBarRoute

        data class Start(override val selectedRoute: NavigationBarRoute) : NavigationBar
        data class Bottom(override val selectedRoute: NavigationBarRoute) : NavigationBar
    }
}

private object AppScreenConstants {
    val navigationBarRoutes: PersistentList<NavigationBarRoute> = persistentListOf(
        DevicesList,
        AddDevice,
        UserSettings
    )
}

object AppScreenTestTags {
    fun navigationItemTestTag(navigationBarRoute: NavigationBarRoute): String =
        "AppScreen.NavigationItem.${navigationBarRoute.toTestTag()}"

    private fun NavigationBarRoute.toTestTag(): String =
        when (this) {
            AddDevice -> "AddDevice"
            DevicesList -> "DevicesList"
            UserSettings -> "UserSettings"
        }
}

@Composable
fun AppScreenPreview(
    previewModel: PreviewModel<AppScreenState>,
    onNavigationBarClick: (navigationBarRoute: NavigationBarRoute) -> Unit = {}
) {
    ScreenPreviewComposable(previewModel) {
        AppScreen(
            state = previewModel.model,
            onNavigationBarClick = onNavigationBarClick,
            content = {
                MockContent(modifier = Modifier.fillMaxSize())
            }
        )
    }
}
