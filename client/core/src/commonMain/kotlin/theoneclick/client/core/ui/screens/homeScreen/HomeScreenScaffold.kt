package theoneclick.client.core.ui.screens.homeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.core.generated.resources.Res
import theoneclick.client.core.generated.resources.homeScreen_navigationBar_addDevice
import theoneclick.client.core.generated.resources.homeScreen_navigationBar_devicesList
import theoneclick.client.core.ui.previews.dev.MockContent
import theoneclick.client.core.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.screenProperties.LocalScreenProperties
import theoneclick.shared.core.routes.HomeRoute
import theoneclick.shared.core.routes.HomeRoute.AddDevice
import theoneclick.shared.core.routes.HomeRoute.DevicesList

@Composable
fun HomeScreenScaffold(
    selectedHomeRoute: HomeRoute,
    onHomeRouteClick: (homeRoute: HomeRoute) -> Unit,
    content: @Composable () -> Unit,
) {
    val homeRoutes = remember { persistentListOf(DevicesList, AddDevice) }
    val screenProperties = LocalScreenProperties.current
    val isCompact = screenProperties.isCompact
    Scaffold(
        content = { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (!isCompact) {
                    StartNavigationBar(
                        selectedHomeRoute = selectedHomeRoute,
                        homeRoutes = homeRoutes,
                        onHomeRouteClick = onHomeRouteClick
                    )
                }

                content()
            }
        },
        bottomBar = {
            if (isCompact) {
                BottomNavigation(
                    selectedHomeRoute = selectedHomeRoute,
                    homeRoutes = homeRoutes,
                    onHomeRouteClick = onHomeRouteClick
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun StartNavigationBar(
    selectedHomeRoute: HomeRoute,
    homeRoutes: ImmutableList<HomeRoute>,
    onHomeRouteClick: (homeRoute: HomeRoute) -> Unit,
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
            homeRoutes.forEach { homeRoute ->
                NavigationRailItem(
                    selected = homeRoute == selectedHomeRoute,
                    onClick = { onHomeRouteClick(homeRoute) },
                    icon = { NavigationIcon(homeRoute) },
                    label = { NavigationLabel(homeRoute) },
                    modifier = Modifier.testTag(HomeScreenScaffoldTestTags.navigationItemTestTag(homeRoute)),
                )
            }
        }
    }
}

@Composable
private fun BottomNavigation(
    selectedHomeRoute: HomeRoute,
    homeRoutes: ImmutableList<HomeRoute>,
    onHomeRouteClick: (homeRoute: HomeRoute) -> Unit,
) {
    NavigationBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            homeRoutes.forEach { homeRoute ->
                NavigationBarItem(
                    selected = homeRoute == selectedHomeRoute,
                    onClick = { onHomeRouteClick(homeRoute) },
                    icon = { NavigationIcon(homeRoute) },
                    label = { NavigationLabel(homeRoute) },
                    modifier = Modifier.testTag(HomeScreenScaffoldTestTags.navigationItemTestTag(homeRoute)),
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(homeRoute: HomeRoute) {
    val imageVector = when (homeRoute) {
        is DevicesList -> Icons.AutoMirrored.Filled.List
        is AddDevice -> Icons.Filled.Add
    }

    Icon(
        imageVector = imageVector,
        contentDescription = homeRoute.toLabel(),
    )
}

@Composable
private fun NavigationLabel(homeRoute: HomeRoute) {
    Text(
        text = homeRoute.toLabel(),
        fontSize = MaterialTheme.typography.labelSmall.fontSize,
    )
}

@Composable
private fun HomeRoute.toLabel(): String =
    when (this) {
        is DevicesList -> stringResource(Res.string.homeScreen_navigationBar_devicesList)
        is AddDevice -> stringResource(Res.string.homeScreen_navigationBar_addDevice)
    }

object HomeScreenScaffoldTestTags {

    fun navigationItemTestTag(homeRoute: HomeRoute): String =
        "HomeScreenScaffold.NavigationItem.${homeRoute.toTestTag()}"

    private fun HomeRoute.toTestTag(): String =
        when (this) {
            AddDevice -> "AddDevice"
            DevicesList -> "DevicesList"
        }
}

@Composable
fun HomeScreenScaffoldPreview(previewModel: PreviewModel<HomeRoute>) {
    ScreenPreviewComposable(previewModel) {
        HomeScreenScaffold(
            selectedHomeRoute = previewModel.model,
            onHomeRouteClick = {},
            content = { MockContent(modifier = Modifier.fillMaxSize()) },
        )
    }
}
