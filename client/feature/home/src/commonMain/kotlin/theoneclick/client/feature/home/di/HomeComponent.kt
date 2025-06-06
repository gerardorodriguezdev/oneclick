package theoneclick.client.feature.home.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import theoneclick.client.feature.home.dataSources.LoggedDataSource
import theoneclick.client.feature.home.dataSources.RemoteLoggedDataSource
import theoneclick.client.feature.home.repositories.DevicesRepository
import theoneclick.client.feature.home.repositories.InMemoryDevicesRepository
import theoneclick.client.feature.home.viewModels.AddDeviceViewModel
import theoneclick.client.feature.home.viewModels.DevicesListViewModel
import theoneclick.client.feature.home.viewModels.UserSettingsViewModel
import theoneclick.client.shared.di.CoreComponent

@HomeScope
@Component
abstract class HomeComponent(@Component val coreComponent: CoreComponent) {

    @HomeScope
    @Provides
    internal fun loggedDataSource(bind: RemoteLoggedDataSource): LoggedDataSource = bind

    @HomeScope
    @Provides
    internal fun devicesRepository(bind: InMemoryDevicesRepository): DevicesRepository = bind

    internal abstract val devicesListViewModelFactory: () -> DevicesListViewModel
    internal abstract val addDeviceViewModelFactory: () -> AddDeviceViewModel
    internal abstract val userSettingsViewModelFactory: () -> UserSettingsViewModel
}

@KmpComponentCreate
expect fun createHomeComponent(coreComponent: CoreComponent): HomeComponent

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
private annotation class HomeScope