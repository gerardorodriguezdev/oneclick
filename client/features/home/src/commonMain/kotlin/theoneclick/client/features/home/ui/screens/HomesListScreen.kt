package theoneclick.client.features.home.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.features.home.generated.resources.*
import theoneclick.client.features.home.ui.screens.HomesListScreenState.UiHome.UiRoom.UiDevice
import theoneclick.client.shared.ui.components.Body
import theoneclick.client.shared.ui.components.Label
import theoneclick.client.shared.ui.components.ScreenBox
import theoneclick.client.shared.ui.components.Title
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.theme.Tokens

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
                state.homes.forEachIndexed { homeIndex, home ->
                    homeName(homeId = home.id, homeName = home.name)

                    home.rooms.forEachIndexed { roomIndex, room ->
                        roomName(roomId = room.id, roomName = room.name)

                        itemsIndexed(
                            items = room.devices,
                            key = { deviceIndex, device -> device.id },
                            contentType = { _, device -> HomesListContentType.deviceCard(device) },
                        ) { deviceIndex, device ->
                            DeviceCard(device = device)
                        }
                    }
                }

                item {
                    Paginator(isLoading = state.isPaginationLoading, onShown = { onEvent(HomesListEvent.EndReached) })
                }
            }
        } else {
            Empty()
        }
    }
}

private fun LazyGridScope.homeName(homeId: String, homeName: String) {
    stickyHeader(key = homeId, contentType = HomesListContentType.HOME_NAME) {
        Title(
            text = stringResource(Res.string.homesListScreen_homeName_home, homeName),
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        )
    }
}

private fun LazyGridScope.roomName(roomId: String, roomName: String) {
    stickyHeader(key = roomId, contentType = HomesListContentType.ROOM_NAME) {
        Title(
            text = stringResource(
                Res.string.homesListScreen_roomName_room,
                roomName
            ),
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        )
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
                    device.name
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

internal data class HomesListScreenState(
    val homes: ImmutableList<UiHome> = persistentListOf(),
    val isFullScreenLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
) {
    data class UiHome(
        val id: String,
        val name: String,
        val rooms: ImmutableList<UiRoom> = persistentListOf(),
    ) {
        data class UiRoom(
            val id: String,
            val name: String,
            val devices: ImmutableList<UiDevice> = persistentListOf(),
        ) {
            sealed interface UiDevice {
                val id: String
                val name: String

                data class UiWaterSensor(
                    override val id: String,
                    override val name: String,
                    val level: String,
                ) : UiDevice
            }
        }
    }
}

internal sealed interface HomesListEvent {
    data object Refresh : HomesListEvent
    data object EndReached : HomesListEvent
}

private object HomesListContentType {
    const val HOME_NAME = 1

    const val ROOM_NAME = 2

    fun deviceCard(device: UiDevice): Int =
        when (device) {
            is UiDevice.UiWaterSensor -> 3
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
