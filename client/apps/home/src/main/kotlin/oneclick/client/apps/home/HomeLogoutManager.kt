package oneclick.client.apps.home

import oneclick.client.apps.home.dataSources.base.DevicesStore
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.platform.LogoutManager

internal class HomeLogoutManager(
    private val devicesStore: DevicesStore,
    private val tokenDataSource: TokenDataSource,
) : LogoutManager {
    override suspend fun logout() {
        devicesStore.clear()
        tokenDataSource.clear()
    }
}