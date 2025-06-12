package theoneclick.client.features.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Blinds
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.features.home.generated.resources.*
import theoneclick.client.features.home.states.DevicesListState
import theoneclick.client.features.home.ui.events.DevicesListEvent
import theoneclick.client.features.home.ui.screens.DevicesListScreenTestTags.DEVICE_CONTAINER
import theoneclick.client.features.home.ui.screens.DevicesListScreenTestTags.DEVICE_NAME_TEXT
import theoneclick.client.features.home.ui.screens.DevicesListScreenTestTags.OPENING_STATE_SWITCH
import theoneclick.client.features.home.ui.screens.DevicesListScreenTestTags.ROOM_NAME_TEXT
import theoneclick.client.features.home.ui.screens.DevicesListScreenTestTags.ROTATION_SLIDER
import theoneclick.client.features.home.ui.screens.DevicesListScreenTestTags.labelTestTag
import theoneclick.client.shared.ui.components.Body
import theoneclick.client.shared.ui.components.Label
import theoneclick.client.shared.ui.components.ScreenBox
import theoneclick.client.shared.ui.components.Title
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.theme.Tokens
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceFeature.Openable
import theoneclick.shared.core.models.entities.DeviceFeature.Rotateable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DevicesListScreen(
    state: DevicesListState,
    onEvent: (model: DevicesListEvent) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { onEvent(DevicesListEvent.Refresh) },
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .testTag(DevicesListScreenTestTags.LIST_CONTAINER)
    ) {
        if (state.devices.isEmpty()) {
            Empty()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = DevicesListScreenConstants.deviceCardMinWidth),
                verticalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
                horizontalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
                contentPadding = PaddingValues(Tokens.containerPadding),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    items = state.devices,
                    key = { device -> device.id.value },
                    contentType = { item -> item },
                ) { device ->
                    DeviceCard(
                        device = device,
                        updateDevice = { updatedDevice -> onEvent(DevicesListEvent.UpdateDevice(updatedDevice)) },
                        modifier = Modifier.testTag(DEVICE_CONTAINER),
                    )
                }
            }
        }
    }
}

@Composable
private fun Empty() {
    ScreenBox {
        Title(
            text = stringResource(Res.string.devicesListScreen_placeholder_noDevicesFound),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DeviceCard(
    device: Device,
    updateDevice: (updatedDevice: Device) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Tokens.itemsSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Tokens.containerPadding),
        ) {
            val icon by remember {
                derivedStateOf {
                    when (device) {
                        is Device.Blind -> Icons.Filled.Blinds
                    }
                }
            }
            DeviceIcon(icon = icon)

            DeviceSection(
                label = stringResource(Res.string.devicesListScreen_deviceCardDeviceNameLabel_deviceName),
                content = {
                    Body(text = device.deviceName, modifier = Modifier.testTag(DEVICE_NAME_TEXT))
                }
            )

            DeviceSection(
                label = stringResource(Res.string.devicesListScreen_deviceCardRoomNameLabel_room),
                content = { Body(text = device.room, modifier = Modifier.testTag(ROOM_NAME_TEXT)) }
            )

            if (device is Openable) {
                OpenableDeviceSection(
                    isOpened = device.isOpened,
                    onToggleDevice = { newCheckedState -> updateDevice(device.toggle(newCheckedState)) }
                )
            }

            if (device is Rotateable) {
                RotatableDeviceSection(
                    rotation = device.rotation,
                    onRotateDevice = { newRotation -> updateDevice(device.rotate(newRotation)) }
                )
            }
        }
    }
}

@Composable
private fun DeviceIcon(icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Icon(imageVector = icon, contentDescription = null)
    }
}

@Composable
private fun DeviceSection(
    label: String,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Label(
            text = label,
            modifier = Modifier
                .padding(end = 16.dp)
                .testTag(labelTestTag(label))
        )

        content()
    }
}

@Composable
private fun OpenableDeviceSection(isOpened: Boolean, onToggleDevice: (newCheckedState: Boolean) -> Unit) {
    DeviceSection(
        label = if (isOpened) {
            stringResource(Res.string.devicesListScreen_deviceCardOpenedLabel_opened)
        } else {
            stringResource(Res.string.devicesListScreen_deviceCardClosedLabel_closed)
        },
        content = {
            Switch(
                checked = isOpened,
                onCheckedChange = onToggleDevice,
                modifier = Modifier.testTag(OPENING_STATE_SWITCH)
            )
        }
    )
}

@Composable
private fun RotatableDeviceSection(rotation: Int, onRotateDevice: (newRotation: Int) -> Unit) {
    var currentRotation by remember { mutableIntStateOf(rotation) }

    DeviceSection(
        label = stringResource(Res.string.devicesListScreen_deviceCardRotationLabel_rotation),
        content = {
            Slider(
                value = currentRotation.toFloat(),
                valueRange = Device.Blind.blindRange.toClosedFloatingPointRange(),
                steps = 10,
                onValueChange = { newRotation ->
                    currentRotation = newRotation.toInt()
                },
                onValueChangeFinished = { onRotateDevice(currentRotation) },
                modifier = Modifier
                    .weight(1f)
                    .testTag(ROTATION_SLIDER),
            )
        }
    )
}

private object DevicesListScreenConstants {
    val deviceCardMinWidth: Dp = 250.dp
}

internal object DevicesListScreenTestTags {
    const val LIST_CONTAINER = "DevicesListScreen.ListContainer"
    const val DEVICE_CONTAINER = "DevicesListScreen.DeviceContainer"
    const val DEVICE_NAME_TEXT = "DevicesListScreen.DeviceNameText"
    const val ROOM_NAME_TEXT = "DevicesListScreen.RoomNameText"
    const val OPENING_STATE_SWITCH = "DevicesListScreen.OpeningStateSwitch"
    const val ROTATION_SLIDER = "DevicesListScreen.RotationSlider"

    fun labelTestTag(label: String): String = "DevicesListScreen.Label.$label"
}

@Composable
internal fun DevicesListScreenPreview(previewModel: PreviewModel<DevicesListState>) {
    ScreenPreviewComposable(previewModel) {
        DevicesListScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}