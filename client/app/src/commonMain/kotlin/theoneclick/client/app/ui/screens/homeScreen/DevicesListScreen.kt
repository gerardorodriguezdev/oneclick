package theoneclick.client.app.ui.screens.homeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Blinds
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.app.generated.resources.*
import theoneclick.client.app.ui.components.DefaultScaffold
import theoneclick.client.app.ui.components.SnackbarState
import theoneclick.client.app.ui.events.homeScreen.DevicesListEvent
import theoneclick.client.app.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenConstants.deviceCardMinWidth
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenTestTags.DEVICE_CONTAINER
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenTestTags.DEVICE_NAME_TEXT
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenTestTags.LIST_CONTAINER
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenTestTags.OPENING_STATE_SWITCH
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenTestTags.ROOM_NAME_TEXT
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenTestTags.ROTATION_SLIDER
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenTestTags.labelTestTag
import theoneclick.client.app.ui.states.homeScreen.DevicesListState
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.DeviceFeature.Openable
import theoneclick.shared.core.models.entities.DeviceFeature.Rotateable

@Composable
fun DevicesListScreen(
    state: DevicesListState,
    onEvent: (model: DevicesListEvent) -> Unit,
) {
    DefaultScaffold(
        snackbarState = SnackbarState(
            text = stringResource(Res.string.devicesList_snackbar_unknownError),
            isErrorType = true,
            showSnackbar = state.showError,
        ),
        onSnackbarShow = { onEvent(DevicesListEvent.ErrorShown) }
    ) {
        Content(
            state = state,
            onEvent = onEvent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: DevicesListState,
    onEvent: (devicesListEvent: DevicesListEvent) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { onEvent(DevicesListEvent.Refresh) },
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .testTag(LIST_CONTAINER)
    ) {
        if (state.devices.isEmpty()) {
            Empty()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = deviceCardMinWidth),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.devicesList_placeholder_noDevicesFound),
            style = MaterialTheme.typography.titleLarge,
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                label = stringResource(Res.string.devicesList_deviceCardDeviceNameLabel_deviceName),
                content = {
                    DeviceSectionBodyText(text = device.deviceName, modifier = Modifier.testTag(DEVICE_NAME_TEXT))
                }
            )

            DeviceSection(
                label = stringResource(Res.string.devicesList_deviceCardRoomNameLabel_room),
                content = { DeviceSectionBodyText(text = device.room, modifier = Modifier.testTag(ROOM_NAME_TEXT)) }
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
        DeviceSectionLabelText(
            text = label,
            modifier = Modifier
                .padding(end = 16.dp)
                .testTag(labelTestTag(label))
        )

        content()
    }
}

@Composable
private fun DeviceSectionLabelText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier,
    )
}

@Composable
private fun DeviceSectionBodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Light,
        ),
        modifier = modifier,
    )
}

@Composable
private fun OpenableDeviceSection(isOpened: Boolean, onToggleDevice: (newCheckedState: Boolean) -> Unit) {
    DeviceSection(
        label = if (isOpened) {
            stringResource(Res.string.devicesList_deviceCardOpenedLabel_opened)
        } else {
            stringResource(Res.string.devicesList_deviceCardClosedLabel_closed)
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
        label = stringResource(Res.string.devicesList_deviceCardRotationLabel_rotation),
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

object DevicesListScreenConstants {
    val deviceCardMinWidth: Dp = 250.dp
}

object DevicesListScreenTestTags {
    const val LIST_CONTAINER = "DevicesListScreen.ListContainer"
    const val DEVICE_CONTAINER = "DevicesListScreen.DeviceContainer"
    const val DEVICE_NAME_TEXT = "DevicesListScreen.DeviceNameText"
    const val ROOM_NAME_TEXT = "DevicesListScreen.RoomNameText"
    const val OPENING_STATE_SWITCH = "DevicesListScreen.OpeningStateSwitch"
    const val ROTATION_SLIDER = "DevicesListScreen.RotationSlider"

    fun labelTestTag(label: String): String = "DevicesListScreen.Label.$label"
}

@Composable
fun DevicesListScreenPreview(previewModel: PreviewModel<DevicesListState>) {
    ScreenPreviewComposable(previewModel) {
        DevicesListScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}