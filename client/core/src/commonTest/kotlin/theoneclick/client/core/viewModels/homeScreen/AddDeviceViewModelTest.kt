package theoneclick.client.core.viewModels.homeScreen

import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.client.core.routes.NavigationController.NavigationEvent.Navigate
import theoneclick.client.core.testing.fakes.FakeLoggedDataSource
import theoneclick.client.core.testing.fakes.FakeNavigationController
import theoneclick.client.core.ui.events.homeScreen.AddDeviceEvent.*
import theoneclick.client.core.ui.states.homeScreen.AddDeviceState
import theoneclick.shared.core.dataSources.models.entities.DeviceType
import theoneclick.shared.core.dataSources.models.results.AddDeviceResult
import theoneclick.shared.core.routes.AppRoute
import theoneclick.shared.testing.dispatchers.CoroutinesTest
import theoneclick.shared.testing.extensions.assertContains
import theoneclick.shared.testing.extensions.assertIsEmpty
import kotlin.test.Test
import kotlin.test.assertEquals

class AddDeviceViewModelTest : CoroutinesTest() {

    private val fakeNavigationController = FakeNavigationController()
    private val dataSource = FakeLoggedDataSource()
    private val viewModel =
        AddDeviceViewModel(navigationController = fakeNavigationController, loggedDataSource = dataSource)

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
        dataSource.addDeviceResultFlow = flowOf(AddDeviceResult.Success)
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
    fun `GIVEN not logged WHEN add device button clicked event THEN navigates back`() {
        dataSource.addDeviceResultFlow = flowOf(AddDeviceResult.Failure.NotLogged)
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

        fakeNavigationController.events.assertContains(
            Navigate(
                destinationRoute = AppRoute.Login,
                popUpTo = popUpToInclusive(
                    startRoute = AppRoute.Home,
                )
            )
        )
    }

    @Test
    fun `GIVEN unknown error WHEN add device button clicked event THEN returns updated state`() {
        dataSource.addDeviceResultFlow = flowOf(AddDeviceResult.Failure.UnknownError)
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
        dataSource.addDeviceResultFlow = flowOf(AddDeviceResult.Failure.UnknownError)
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
        fakeNavigationController.events.assertIsEmpty()
    }

    @Test
    fun `WHEN success shown event THEN returns updated state`() {
        dataSource.addDeviceResultFlow = flowOf(AddDeviceResult.Success)
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
        fakeNavigationController.events.assertIsEmpty()
    }
}
