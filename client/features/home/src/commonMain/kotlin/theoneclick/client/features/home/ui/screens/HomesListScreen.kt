package theoneclick.client.features.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.features.home.generated.resources.*
import theoneclick.client.features.home.ui.events.HomesListEvent
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
        isRefreshing = state.isLoading,
        onRefresh = { onEvent(HomesListEvent.Refresh) },
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .testTag(HomesListScreenTestTags.LIST_CONTAINER)
    ) {
        if (state.homes.isEmpty()) {
            Empty()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = HomesListScreenConstants.itemCardMinSize),
                verticalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
                horizontalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
                contentPadding = PaddingValues(Tokens.containerPadding),
                modifier = Modifier.fillMaxSize(),
            ) {

            }
        }
    }
}

@Composable
private fun Empty() {
    ScreenBox {
        Title(
            text = stringResource(Res.string.homesListScreen_placeholder_noHomesFound),
            textAlign = TextAlign.Center,
        )
    }
}

internal data class HomesListScreenState(
    val homes: ImmutableList<Home> = persistentListOf(),
    val isLoading: Boolean = false,
) {
    data class Home(
        val rooms: ImmutableList<Room> = persistentListOf(),
    ) {
        data class Room(
            val devices: ImmutableList<Device> = persistentListOf(),
        ) {
            sealed interface Device {
                val name: String

                data class WaterSensor(
                    override val name: String,
                    val level: String,
                ) : Device
            }
        }
    }
}

private object HomesListScreenConstants {
    val itemCardMinSize: Dp = 250.dp
}

internal object HomesListScreenTestTags {
    const val LIST_CONTAINER = "HomesListScreen.ListContainer"

    fun labelTestTag(label: String): String = "HomesListScreen.Label.$label"
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