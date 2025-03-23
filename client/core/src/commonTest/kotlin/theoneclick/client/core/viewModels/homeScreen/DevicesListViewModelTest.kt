package theoneclick.client.core.viewModels.homeScreen

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.models.results.DevicesResult
import theoneclick.client.core.models.results.UpdateDeviceResult
import theoneclick.client.core.testing.fakes.FakeLoggedDataSource
import theoneclick.client.core.ui.events.homeScreen.DevicesListEvent
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels
import theoneclick.client.core.ui.states.homeScreen.DevicesListState
import theoneclick.shared.testing.dispatchers.CoroutinesTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicesListViewModelTest : CoroutinesTest() {

    private val dataSource = FakeLoggedDataSource()

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
        dataSource.devicesResultFlow = flowOf(
            DevicesResult.Success(devices = DevicesListScreenPreviewModels.devices)
        )

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
        dataSource.devicesResultFlow = flowOf(
            DevicesResult.Success(devices = emptyList())
        )

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
        dataSource.devicesResultFlow = flowOf(
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
    fun `GIVEN no devices at start WHEN refresh THEN returns devices`() {
        dataSource.devicesResultFlow = flowOf(DevicesResult.Success(devices = persistentListOf()))

        val devicesListViewModel = devicesListViewModel()
        dataSource.devicesResultFlow = flowOf(DevicesResult.Success(devices = DevicesListScreenPreviewModels.devices))

        devicesListViewModel.onEvent(DevicesListEvent.Refresh)

        assertEquals(
            expected = DevicesListState(
                devices = DevicesListScreenPreviewModels.devices,
            ),
            actual = devicesListViewModel.state.value,
        )
    }

    @Test
    fun `GIVEN devices available WHEN update device THEN returns updated device`() {
        dataSource.devicesResultFlow = flowOf(
            DevicesResult.Success(devices = persistentListOf(DevicesListScreenPreviewModels.closedBlind))
        )
        dataSource.updateDeviceResultFlow = flowOf(
            UpdateDeviceResult.Success,
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
                    updatedDevice,
                ),
            ),
            actual = devicesListViewModel.state.value,
        )
    }

    @Test
    fun `GIVEN failure WHEN update device THEN returns failure`() {
        dataSource.devicesResultFlow = flowOf(
            DevicesResult.Success(devices = persistentListOf(DevicesListScreenPreviewModels.closedBlind))
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
                    updatedDevice,
                ),
                showError = true,
            ),
            actual = devicesListViewModel.state.value,
        )
    }

    private fun devicesListViewModel(): DevicesListViewModel =
        DevicesListViewModel(
            loggedDataSource = dataSource,
        )
}
