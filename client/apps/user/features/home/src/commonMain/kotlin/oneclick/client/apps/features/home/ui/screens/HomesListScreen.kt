package oneclick.client.apps.features.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import oneclick.client.apps.features.home.ui.screens.HomesListScreenState.UiHome.UiDevice
import oneclick.client.apps.user.features.home.generated.resources.*
import oneclick.client.shared.ui.components.Body
import oneclick.client.shared.ui.components.Label
import oneclick.client.shared.ui.components.ScreenBox
import oneclick.client.shared.ui.components.Title
import oneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import oneclick.client.shared.ui.previews.providers.base.PreviewModel
import oneclick.client.shared.ui.theme.Tokens
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomesListScreen(
    state: HomesListScreenState,
    onEvent: (model: HomesListEvent) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = state.isFullScreenLoading,
        onRefresh = { onEvent(HomesListEvent.Refresh) },
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .testTag(HomesListScreenTestTags.LIST_CONTAINER)
    ) {
        if (state.homes.isNotEmpty()) {
            val lazyGridState = rememberLazyGridState()
            LazyVerticalGrid(
                state = lazyGridState,
                columns = GridCells.Adaptive(minSize = HomesListScreenConstants.itemCardMinSize),
                verticalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
                horizontalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
                contentPadding = PaddingValues(Tokens.containerPadding),
                modifier = Modifier.fillMaxSize(),
            ) {
                state.homes.forEachIndexed { _, home ->
                    itemsIndexed(
                        items = home.devices,
                        key = { _, device -> device.id },
                        contentType = { _, device -> HomesListContentType.deviceCard(device) },
                    ) { _, device ->
                        DeviceCard(device = device)
                    }
                }

                item {
                    Paginator(
                        isLoading = state.isPaginationLoading,
                        onShown = { onEvent(HomesListEvent.EndReached) })
                }
            }
        } else {
            Empty()
        }
    }
}

@Composable
private fun DeviceCard(device: UiDevice) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.containerPadding),
        ) {
            Body(
                text = stringResource(
                    Res.string.homesListScreen_deviceName_room,
                )
            )

            when (device) {
                is UiDevice.UiWaterSensor -> WaterSensorInfo(device)
            }
        }
    }
}

@Composable
private fun WaterSensorInfo(waterSensor: UiDevice.UiWaterSensor) {
    Body(text = stringResource(Res.string.homesListScreen_waterSensor_type))

    Label(
        text = stringResource(
            Res.string.homesListScreen_waterSensor_level,
            waterSensor.level
        )
    )
}

@Composable
private fun Empty() {
    ScreenBox {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Title(
                text = stringResource(Res.string.homesListScreen_placeholder_noHomesFound),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun Paginator(isLoading: Boolean, onShown: () -> Unit) {
    if (isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator()
        }
    } else {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .onLayoutRectChanged {
                    onShown()
                }
        )
    }
}

@Immutable
internal data class HomesListScreenState(
    val homes: ImmutableList<UiHome> = persistentListOf(),
    val isFullScreenLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
) {
    @Immutable
    data class UiHome(
        val id: String,
        val devices: ImmutableList<UiDevice> = persistentListOf(),
    ) {
        @Immutable
        sealed interface UiDevice {
            val id: String

            @Immutable
            data class UiWaterSensor(
                override val id: String,
                val level: String,
            ) : UiDevice
        }
    }
}

internal sealed interface HomesListEvent {
    data object Refresh : HomesListEvent
    data object EndReached : HomesListEvent
}

private object HomesListContentType {
    fun deviceCard(device: UiDevice): Int =
        when (device) {
            is UiDevice.UiWaterSensor -> 1
        }
}

private object HomesListScreenConstants {
    val itemCardMinSize: Dp = 200.dp
}

internal object HomesListScreenTestTags {
    const val LIST_CONTAINER = "HomesListScreen.ListContainer"
}

@Composable
internal fun HomesListScreenPreview(previewModel: PreviewModel<HomesListScreenState>) {
    ScreenPreviewComposable(previewModel) {
        HomesListScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}
