package theoneclick.client.core.viewModels.homeScreen

import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.models.results.AddDeviceResult
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.fakes.FakeDevicesRepository
import theoneclick.client.core.ui.events.homeScreen.AddDeviceEvent.*
import theoneclick.client.core.ui.states.homeScreen.AddDeviceState
import theoneclick.shared.core.models.entities.DeviceType
import theoneclick.shared.testing.dispatchers.CoroutinesTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddDeviceViewModelTest : CoroutinesTest() {

    private val devicesRepository = FakeDevicesRepository()
    private val viewModel =
        AddDeviceViewModel(devicesRepository = devicesRepository)

    @Test
    fun `GIVEN initial state THEN returns initial state`() {
        assertEquals(
            expected = AddDeviceState(),
            actual = viewModel.state.value,
        )
    }

    @Test
    fun `GIVEN valid deviceName WHEN deviceName changed event THEN returns updated state`() {
        viewModel.onEvent(DeviceNameChanged("DeviceName"))

        assertEquals(
            expected = AddDeviceState(
                deviceName = "DeviceName",
                isDeviceNameValid = true,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN invalid deviceName WHEN deviceName changed event THEN returns updated state`() {
        viewModel.onEvent(DeviceNameChanged("1"))

        assertEquals(
            expected = AddDeviceState(
                deviceName = "1",
                isDeviceNameValid = false,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN valid roomName WHEN roomName changed event THEN returns updated state`() {
        viewModel.onEvent(RoomNameChanged("RoomName"))

        assertEquals(
            expected = AddDeviceState(
                roomName = "RoomName",
                isRoomNameValid = true,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN invalid roomName WHEN roomName changed event THEN returns updated state`() {
        viewModel.onEvent(RoomNameChanged("$"))

        assertEquals(
            expected = AddDeviceState(
                roomName = "$",
                isRoomNameValid = false,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN deviceType WHEN deviceType changed event THEN returns updated state`() {
        viewModel.onEvent(DeviceTypeChanged(DeviceType.BLIND))

        assertEquals(
            expected = AddDeviceState(
                deviceType = DeviceType.BLIND,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN valid request with success WHEN add device button clicked event THEN returns updated state`() {
        devicesRepository.addDeviceResultFlow = flowOf(AddDeviceResult.Success(TestData.device))
        viewModel.onEvent(DeviceNameChanged("DeviceName"))
        viewModel.onEvent(RoomNameChanged("RoomName"))

        viewModel.onEvent(AddDeviceButtonClicked)

        assertEquals(
            expected = AddDeviceState(
                deviceName = "DeviceName",
                isDeviceNameValid = true,
                roomName = "RoomName",
                isRoomNameValid = true,
                deviceType = DeviceType.BLIND,
                isAddDeviceButtonEnabled = true,
                showSuccess = true,
            ),
            actual = viewModel.state.value,
        )
    }

    @Test
    fun `GIVEN failure WHEN add device button clicked event THEN returns failure`() {
        devicesRepository.addDeviceResultFlow = flowOf(AddDeviceResult.Failure)
        viewModel.onEvent(DeviceNameChanged("DeviceName"))
        viewModel.onEvent(RoomNameChanged("RoomName"))

        viewModel.onEvent(AddDeviceButtonClicked)

        assertEquals(
            expected = AddDeviceState(
                deviceName = "DeviceName",
                isDeviceNameValid = true,
                roomName = "RoomName",
                isRoomNameValid = true,
                deviceType = DeviceType.BLIND,
                isAddDeviceButtonEnabled = true,
                showError = true,
                showSuccess = false,
            ),
            actual = viewModel.state.value,
        )
    }

    @Test
    fun `WHEN error shown event THEN returns updated state`() {
        devicesRepository.addDeviceResultFlow = flowOf(AddDeviceResult.Failure)
        viewModel.onEvent(DeviceNameChanged("DeviceName"))
        viewModel.onEvent(RoomNameChanged("RoomName"))
        viewModel.onEvent(AddDeviceButtonClicked)
        viewModel.onEvent(ErrorShown)

        assertEquals(
            expected = AddDeviceState(
                deviceName = "DeviceName",
                isDeviceNameValid = true,
                roomName = "RoomName",
                isRoomNameValid = true,
                deviceType = DeviceType.BLIND,
                isAddDeviceButtonEnabled = true,
                showError = false
            ),
            actual = viewModel.state.value,
        )
    }

    @Test
    fun `WHEN success shown event THEN returns updated state`() {
        devicesRepository.addDeviceResultFlow = flowOf(AddDeviceResult.Success(TestData.device))
        viewModel.onEvent(DeviceNameChanged("DeviceName"))
        viewModel.onEvent(RoomNameChanged("RoomName"))
        viewModel.onEvent(AddDeviceButtonClicked)
        viewModel.onEvent(SuccessShown)

        assertEquals(
            expected = AddDeviceState(
                deviceName = "DeviceName",
                isDeviceNameValid = true,
                roomName = "RoomName",
                isRoomNameValid = true,
                deviceType = DeviceType.BLIND,
                isAddDeviceButtonEnabled = true,
                showSuccess = false
            ),
            actual = viewModel.state.value,
        )
    }
}
