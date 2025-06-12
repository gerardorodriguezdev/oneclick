package theoneclick.client.features.home.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import theoneclick.client.features.home.dataSources.LoggedDataSource
import theoneclick.client.features.home.dataSources.RemoteLoggedDataSource
import theoneclick.client.features.home.repositories.DevicesRepository
import theoneclick.client.features.home.repositories.InMemoryDevicesRepository
import theoneclick.client.features.home.viewModels.DevicesListViewModel
import theoneclick.client.features.home.viewModels.UserSettingsViewModel
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
    internal abstract val userSettingsViewModelFactory: () -> UserSettingsViewModel
}

@KmpComponentCreate
expect fun createHomeComponent(coreComponent: CoreComponent): HomeComponent

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
private annotation class HomeScope