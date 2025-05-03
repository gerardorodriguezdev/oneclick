package theoneclick.client.core.ui.screens

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
import theoneclick.client.core.ui.screens.AppScreenConstants.navigationBarRoutes
import theoneclick.shared.core.models.routes.HomeRoute
import theoneclick.shared.core.models.routes.HomeRoute.*

@Composable
fun AppScreen(
    state: AppScreenState,
    onNavigationBarClicked: (navigationBarRoute: HomeRoute) -> Unit,
    modifier: Modifier = Modifier,
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
                        onNavigationBarClicked = onNavigationBarClicked,
                    )
                }

                content()
            }
        },
        bottomBar = {
            if (state.navigationBar is AppScreenState.NavigationBar.Bottom) {
                state.navigationBar.BottomNavigationBar(
                    onNavigationBarClicked = onNavigationBarClicked,
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun AppScreenState.NavigationBar.Start.StartNavigationBar(
    onNavigationBarClicked: (navigationBarRoute: HomeRoute) -> Unit,
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
                    onClick = { onNavigationBarClicked(navigationBarRoute) },
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
    onNavigationBarClicked: (navigationBarRoute: HomeRoute) -> Unit,
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
                    onClick = { onNavigationBarClicked(navigationBarRoute) },
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
private fun NavigationIcon(navigationBarRoute: HomeRoute) {
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
private fun NavigationLabel(navigationBarRoute: HomeRoute) {
    Text(
        text = navigationBarRoute.toLabel(),
        fontSize = MaterialTheme.typography.labelSmall.fontSize,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun HomeRoute.toLabel(): String =
    when (this) {
        is DevicesList -> stringResource(Res.string.homeScreen_navigationBar_devicesList)
        is AddDevice -> stringResource(Res.string.homeScreen_navigationBar_addDevice)
        is UserSettings -> stringResource(Res.string.homeScreen_navigationBar_userSettings)
    }

data class AppScreenState(val navigationBar: NavigationBar?) {
    sealed interface NavigationBar {
        val selectedRoute: HomeRoute

        data class Start(override val selectedRoute: HomeRoute) : NavigationBar
        data class Bottom(override val selectedRoute: HomeRoute) : NavigationBar
    }
}

private object AppScreenConstants {
    val navigationBarRoutes: PersistentList<HomeRoute> = persistentListOf(DevicesList, AddDevice, UserSettings)
}

object AppScreenTestTags {
    fun navigationItemTestTag(navigationBarRoute: HomeRoute): String =
        "AppScaffold.NavigationItem.${navigationBarRoute.toTestTag()}"

    private fun HomeRoute.toTestTag(): String =
        when (this) {
            AddDevice -> "AddDevice"
            DevicesList -> "DevicesList"
            UserSettings -> "UserSettings"
        }
}
