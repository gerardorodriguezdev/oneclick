package theoneclick.client.core.ui.screens.homeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.app.generated.resources.*
import theoneclick.client.core.mappers.toStringResource
import theoneclick.client.core.ui.components.DefaultButton
import theoneclick.client.core.ui.components.DefaultScaffold
import theoneclick.client.core.ui.components.SnackbarState
import theoneclick.client.core.ui.events.homeScreen.AddDeviceEvent
import theoneclick.client.core.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags.DEVICE_NAME_PLACEHOLDER_TEST_TAG
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags.DEVICE_NAME_TEXT_FIELD_TEST_TAG
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags.ROOM_NAME_PLACEHOLDER_TEST_TAG
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags.ROOM_NAME_TEXT_FIELD_TEST_TAG
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags.TITLE_TEST_TAG
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags.dropDownItemTestTag
import theoneclick.client.core.ui.states.homeScreen.AddDeviceState
import theoneclick.shared.core.models.entities.DeviceType

@Composable
fun AddDeviceScreen(
    state: AddDeviceState,
    onEvent: (event: AddDeviceEvent) -> Unit,
) {
    DefaultScaffold(
        snackbarState = SnackbarState(
            text = snackbarText(state.showError),
            isErrorType = state.showError,
            showSnackbar = state.showError || state.showSuccess,
        ),
        onSnackbarShown = { onEvent(AddDeviceEvent.ErrorShown) },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Card(modifier = Modifier.align(Alignment.Center)) {
                FormContent(
                    deviceName = state.deviceName,
                    onDeviceNameChange = { newDeviceName -> onEvent(AddDeviceEvent.DeviceNameChanged(newDeviceName)) },
                    isDeviceNameValid = state.isDeviceNameValid ?: true,
                    roomName = state.roomName,
                    onRoomNameChange = { newRoomName -> onEvent(AddDeviceEvent.RoomNameChanged(newRoomName)) },
                    isRoomNameValid = state.isRoomNameValid ?: true,
                    deviceType = state.deviceType,
                    onDeviceTypeChange = { newDeviceType -> onEvent(AddDeviceEvent.DeviceTypeChanged(newDeviceType)) },
                    isLoading = state.isLoading,
                    isAddDeviceButtonEnabled = state.isAddDeviceButtonEnabled,
                    onAddDeviceClick = { onEvent(AddDeviceEvent.AddDeviceButtonClicked) },
                )
            }
        }
    }
}

@Composable
private fun snackbarText(isErrorType: Boolean) =
    if (isErrorType) {
        stringResource(Res.string.addDevice_snackbar_unknownError)
    } else {
        stringResource(Res.string.addDevice_snackbar_deviceAdded)
    }

@Suppress("LongParameterList")
@Composable
private fun FormContent(
    deviceName: String,
    onDeviceNameChange: (newDeviceName: String) -> Unit,
    isDeviceNameValid: Boolean,
    roomName: String,
    onRoomNameChange: (newRoomName: String) -> Unit,
    isRoomNameValid: Boolean,
    deviceType: DeviceType,
    onDeviceTypeChange: (newDeviceType: DeviceType) -> Unit,
    isLoading: Boolean,
    isAddDeviceButtonEnabled: Boolean,
    onAddDeviceClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Title()

        DeviceNameTextField(
            deviceName = deviceName,
            onDeviceNameChange = onDeviceNameChange,
            isDeviceNameValid = isDeviceNameValid,
        )

        RoomNameTextField(
            roomName = roomName,
            onRoomNameChange = onRoomNameChange,
            isRoomNameValid = isRoomNameValid,
        )

        DeviceTypeDropdownList(
            selectedDeviceType = deviceType,
            onDeviceTypeChange = onDeviceTypeChange,
        )

        DefaultButton(
            text = stringResource(Res.string.addDevice_addDeviceButton_addDevice),
            onClick = onAddDeviceClick,
            isEnabled = isAddDeviceButtonEnabled,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceTypeDropdownList(
    selectedDeviceType: DeviceType,
    onDeviceTypeChange: (newDeviceType: DeviceType) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { newExpanse ->
            isExpanded = newExpanse
        },
    ) {
        OutlinedTextField(
            value = selectedDeviceType.toStringResource(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            DeviceType.entries.forEach { deviceType ->
                DropdownMenuItem(
                    text = {
                        Text(text = deviceType.toStringResource())
                    },
                    onClick = {
                        isExpanded = false
                        onDeviceTypeChange(deviceType)
                    },
                    modifier = Modifier.testTag(dropDownItemTestTag(deviceType))
                )
            }
        }
    }
}

@Composable
private fun Title() {
    Text(
        text = stringResource(Res.string.addDevice_title_addDevice),
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TITLE_TEST_TAG),
    )
}

@Composable
private fun DeviceNameTextField(
    deviceName: String,
    onDeviceNameChange: (newDeviceName: String) -> Unit,
    isDeviceNameValid: Boolean
) {
    OutlinedTextField(
        placeholder = {
            Text(
                text = stringResource(Res.string.addDevice_deviceNamePlaceholder_deviceName),
                modifier = Modifier.testTag(DEVICE_NAME_PLACEHOLDER_TEST_TAG)
            )
        },
        value = deviceName,
        onValueChange = onDeviceNameChange,
        isError = !isDeviceNameValid,
        maxLines = 1,
        modifier = Modifier.testTag(DEVICE_NAME_TEXT_FIELD_TEST_TAG)
    )
}

@Composable
private fun RoomNameTextField(
    roomName: String,
    onRoomNameChange: (newRoomName: String) -> Unit,
    isRoomNameValid: Boolean
) {
    OutlinedTextField(
        placeholder = {
            Text(
                text = stringResource(Res.string.addDevice_roomNamePlaceholder_roomName),
                modifier = Modifier.testTag(ROOM_NAME_PLACEHOLDER_TEST_TAG)
            )
        },
        value = roomName,
        onValueChange = onRoomNameChange,
        isError = !isRoomNameValid,
        maxLines = 1,
        modifier = Modifier.testTag(ROOM_NAME_TEXT_FIELD_TEST_TAG)
    )
}

object AddDeviceScreenTestTags {
    const val TITLE_TEST_TAG = "AddDeviceScreen.Title"

    const val DEVICE_NAME_TEXT_FIELD_TEST_TAG = "AddDeviceScreen.DeviceNameTextField"
    const val DEVICE_NAME_PLACEHOLDER_TEST_TAG = "AddDeviceScreen.DeviceNamePlaceholder"

    const val ROOM_NAME_TEXT_FIELD_TEST_TAG = "AddDeviceScreen.RoomNameTextField"
    const val ROOM_NAME_PLACEHOLDER_TEST_TAG = "AddDeviceScreen.RoomNamePlaceholder"

    fun dropDownItemTestTag(deviceType: DeviceType): String = "AddDeviceScreen.DropDownMenuItem.${deviceType.name}"
}

@Composable
fun AddDeviceScreenPreview(previewModel: PreviewModel<AddDeviceState>) {
    ScreenPreviewComposable(previewModel) {
        AddDeviceScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}
