package theoneclick.client.core.viewModels.homeScreen

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.client.core.testing.fakes.FakeDevicesRepository
import theoneclick.client.core.ui.events.homeScreen.DevicesListEvent
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels
import theoneclick.client.core.ui.states.homeScreen.DevicesListState
import theoneclick.shared.testing.dispatchers.CoroutinesTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicesListViewModelTest : CoroutinesTest() {

    private val dataSource = FakeDevicesRepository()

    @Test
    fun `GIVEN initial state THEN returns initial state`() {
        val devicesListViewModel = devicesListViewModel()

        assertEquals(
            expected = DevicesListState(),
            actual = devicesListViewModel.state.value,
        )
    }

    @Test
    fun `GIVEN devices available WHEN init THEN returns devices list state`() {
        dataSource.devicesFlow = MutableStateFlow(DevicesListScreenPreviewModels.devices)

        val devicesListViewModel = devicesListViewModel()

        assertEquals(
            expected = DevicesListState(
                devices = DevicesListScreenPreviewModels.devices,
            ),
            actual = devicesListViewModel.state.value,
        )
    }

    @Test
    fun `GIVEN devices not available WHEN init THEN returns empty list state`() {
        val devicesListViewModel = devicesListViewModel()

        assertEquals(
            expected = DevicesListState(
                devices = persistentListOf(),
            ),
            actual = devicesListViewModel.state.value,
        )
    }

    @Test
    fun `GIVEN failure WHEN init THEN returns failure`() {
        dataSource.refreshDevicesResultFlow = flowOf(
            DevicesResult.Failure,
        )

        val devicesListViewModel = devicesListViewModel()

        assertEquals(
            expected = DevicesListState(
                devices = persistentListOf(),
                showError = true,
            ),
            actual = devicesListViewModel.state.value,
        )
    }

    @Test
    fun `GIVEN failure WHEN update device THEN returns failure`() {
        dataSource.devicesFlow = MutableStateFlow(listOf(DevicesListScreenPreviewModels.closedBlind))
        dataSource.refreshDevicesResultFlow = flowOf(
            DevicesResult.Success(devices = listOf(DevicesListScreenPreviewModels.closedBlind))
        )
        dataSource.updateDeviceResultFlow = flowOf(
            UpdateDeviceResult.Failure,
        )
        val updatedDevice = DevicesListScreenPreviewModels.closedBlind.copy(
            isOpened = true,
        )

        val devicesListViewModel = devicesListViewModel()
        devicesListViewModel.onEvent(
            DevicesListEvent.UpdateDevice(
                updatedDevice = updatedDevice,
            )
        )

        assertEquals(
            expected = DevicesListState(
                devices = persistentListOf(
                    DevicesListScreenPreviewModels.closedBlind,
                ),
                showError = true,
            ),
            actual = devicesListViewModel.state.value,
        )
    }

    private fun devicesListViewModel(): DevicesListViewModel =
        DevicesListViewModel(
            devicesRepository = dataSource,
        )
}
