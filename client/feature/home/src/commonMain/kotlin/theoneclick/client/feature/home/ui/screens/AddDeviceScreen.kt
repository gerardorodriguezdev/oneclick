package theoneclick.client.feature.home.ui.screens

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
import theoneclick.client.feature.home.generated.resources.*
import theoneclick.client.feature.home.states.AddDeviceState
import theoneclick.client.feature.home.ui.events.AddDeviceEvent
import theoneclick.client.shared.ui.components.DefaultButton
import theoneclick.client.shared.ui.components.DefaultScaffold
import theoneclick.client.shared.ui.components.SnackbarState
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.shared.core.models.entities.DeviceType

@Composable
internal fun AddDeviceScreen(
    state: AddDeviceState,
    onEvent: (event: AddDeviceEvent) -> Unit,
) {
    DefaultScaffold(
        snackbarState = SnackbarState(
            text = snackbarText(state.showError),
            isErrorType = state.showError,
            showSnackbar = state.showError || state.showSuccess,
        ),
        onSnackbarShow = { onEvent(AddDeviceEvent.ErrorShown) },
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
        stringResource(Res.string.addDeviceScreen_snackbar_unknownError)
    } else {
        stringResource(Res.string.addDeviceScreen_snackbar_deviceAdded)
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
            text = stringResource(Res.string.addDeviceScreen_addDeviceButton_addDevice),
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
                    modifier = Modifier.testTag(AddDeviceScreenTestTags.dropDownItemTestTag(deviceType))
                )
            }
        }
    }
}

@Composable
private fun Title() {
    Text(
        text = stringResource(Res.string.addDeviceScreen_title_addDevice),
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(AddDeviceScreenTestTags.TITLE_TEST_TAG),
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
                text = stringResource(Res.string.addDeviceScreen_deviceNamePlaceholder_deviceName),
                modifier = Modifier.testTag(AddDeviceScreenTestTags.DEVICE_NAME_PLACEHOLDER_TEST_TAG)
            )
        },
        value = deviceName,
        onValueChange = onDeviceNameChange,
        isError = !isDeviceNameValid,
        maxLines = 1,
        modifier = Modifier.testTag(AddDeviceScreenTestTags.DEVICE_NAME_TEXT_FIELD_TEST_TAG)
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
                text = stringResource(Res.string.addDeviceScreen_roomNamePlaceholder_roomName),
                modifier = Modifier.testTag(AddDeviceScreenTestTags.ROOM_NAME_PLACEHOLDER_TEST_TAG)
            )
        },
        value = roomName,
        onValueChange = onRoomNameChange,
        isError = !isRoomNameValid,
        maxLines = 1,
        modifier = Modifier.testTag(AddDeviceScreenTestTags.ROOM_NAME_TEXT_FIELD_TEST_TAG)
    )
}

@Composable
private fun DeviceType.toStringResource(): String =
    when (this) {
        DeviceType.BLIND -> stringResource(Res.string.general_deviceType_blind)
    }


internal object AddDeviceScreenTestTags {
    const val TITLE_TEST_TAG = "AddDeviceScreen.Title"

    const val DEVICE_NAME_TEXT_FIELD_TEST_TAG = "AddDeviceScreen.DeviceNameTextField"
    const val DEVICE_NAME_PLACEHOLDER_TEST_TAG = "AddDeviceScreen.DeviceNamePlaceholder"

    const val ROOM_NAME_TEXT_FIELD_TEST_TAG = "AddDeviceScreen.RoomNameTextField"
    const val ROOM_NAME_PLACEHOLDER_TEST_TAG = "AddDeviceScreen.RoomNamePlaceholder"

    fun dropDownItemTestTag(deviceType: DeviceType): String = "AddDeviceScreen.DropDownMenuItem.${deviceType.name}"
}

@Composable
internal fun AddDeviceScreenPreview(previewModel: PreviewModel<AddDeviceState>) {
    ScreenPreviewComposable(previewModel) {
        AddDeviceScreen(
            state = previewModel.model,
            onEvent = {}
        )
    }
}