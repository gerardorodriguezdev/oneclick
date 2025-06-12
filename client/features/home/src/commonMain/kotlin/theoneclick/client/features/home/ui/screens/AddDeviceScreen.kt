package theoneclick.client.features.home.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.features.home.generated.resources.*
import theoneclick.client.features.home.states.AddDeviceState
import theoneclick.client.features.home.ui.events.AddDeviceEvent
import theoneclick.client.shared.ui.components.*
import theoneclick.client.shared.ui.previews.dev.ScreenPreviewComposable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.shared.core.models.entities.DeviceType

@Composable
internal fun AddDeviceScreen(
    state: AddDeviceState,
    onEvent: (event: AddDeviceEvent) -> Unit,
) {
    ScreenBox {
        DialogBox(header = stringResource(Res.string.addDeviceScreen_title_addDevice)) {
            DeviceNameTextField(
                deviceName = state.deviceName,
                onDeviceNameChange = { newDeviceName -> onEvent(AddDeviceEvent.DeviceNameChanged(newDeviceName)) },
                isDeviceNameValid = state.isDeviceNameValid ?: true,
            )

            RoomNameTextField(
                roomName = state.roomName,
                onRoomNameChange = { newRoomName -> onEvent(AddDeviceEvent.RoomNameChanged(newRoomName)) },
                isRoomNameValid = state.isRoomNameValid ?: true,
            )

            DeviceTypeDropdownList(
                selectedDeviceType = state.deviceType,
                onDeviceTypeChange = { newDeviceType -> onEvent(AddDeviceEvent.DeviceTypeChanged(newDeviceType)) },
            )

            DefaultButton(
                text = stringResource(Res.string.addDeviceScreen_addDeviceButton_addDevice),
                onClick = { onEvent(AddDeviceEvent.AddDeviceButtonClicked) },
                isEnabled = state.isAddDeviceButtonEnabled,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
                        Label(text = deviceType.toStringResource())
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
private fun DeviceNameTextField(
    deviceName: String,
    onDeviceNameChange: (newDeviceName: String) -> Unit,
    isDeviceNameValid: Boolean
) {
    OutlinedTextField(
        placeholder = {
            Body(
                text = stringResource(Res.string.addDeviceScreen_deviceNamePlaceholder_deviceName),
                modifier = Modifier.testTag(AddDeviceScreenTestTags.DEVICE_NAME_PLACEHOLDER)
            )
        },
        value = deviceName,
        onValueChange = onDeviceNameChange,
        isError = !isDeviceNameValid,
        maxLines = 1,
        modifier = Modifier.testTag(AddDeviceScreenTestTags.DEVICE_NAME_TEXT_FIELD)
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
            Body(
                text = stringResource(Res.string.addDeviceScreen_roomNamePlaceholder_roomName),
                modifier = Modifier.testTag(AddDeviceScreenTestTags.ROOM_NAME_PLACEHOLDER)
            )
        },
        value = roomName,
        onValueChange = onRoomNameChange,
        isError = !isRoomNameValid,
        maxLines = 1,
        modifier = Modifier.testTag(AddDeviceScreenTestTags.ROOM_NAME_TEXT_FIELD)
    )
}

@Composable
private fun DeviceType.toStringResource(): String =
    when (this) {
        DeviceType.BLIND -> stringResource(Res.string.addDeviceScreen_dropdown_deviceType)
    }


internal object AddDeviceScreenTestTags {
    const val DEVICE_NAME_TEXT_FIELD = "AddDeviceScreen.DeviceNameTextField"
    const val DEVICE_NAME_PLACEHOLDER = "AddDeviceScreen.DeviceNamePlaceholder"

    const val ROOM_NAME_TEXT_FIELD = "AddDeviceScreen.RoomNameTextField"
    const val ROOM_NAME_PLACEHOLDER = "AddDeviceScreen.RoomNamePlaceholder"

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