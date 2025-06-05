package theoneclick.client.app.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import theoneclick.client.app.dataSources.LoggedDataSource
import theoneclick.client.app.dataSources.RemoteLoggedDataSource
import theoneclick.client.app.repositories.DevicesRepository
import theoneclick.client.app.repositories.InMemoryDevicesRepository
import theoneclick.client.app.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.app.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.app.viewModels.homeScreen.UserSettingsViewModel
import theoneclick.client.shared.di.CoreComponent

@HomeScope
@Component
abstract class HomeComponent(@Component val coreComponent: CoreComponent) {

    @HomeScope
    @Provides
    fun loggedDataSource(bind: RemoteLoggedDataSource): LoggedDataSource = bind

    @HomeScope
    @Provides
    fun devicesRepository(bind: InMemoryDevicesRepository): DevicesRepository = bind

    abstract val devicesListViewModelFactory: () -> DevicesListViewModel
    abstract val addDeviceViewModelFactory: () -> AddDeviceViewModel
    abstract val userSettingsViewModelFactory: () -> UserSettingsViewModel
}

@KmpComponentCreate
expect fun createHomeComponent(coreComponent: CoreComponent): HomeComponent

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class HomeScope