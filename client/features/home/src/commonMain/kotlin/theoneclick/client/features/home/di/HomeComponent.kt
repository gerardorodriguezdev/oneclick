package theoneclick.client.features.home.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import theoneclick.client.features.home.dataSources.LoggedDataSource
import theoneclick.client.features.home.dataSources.RemoteLoggedDataSource
import theoneclick.client.features.home.repositories.HomesRepository
import theoneclick.client.features.home.repositories.InMemoryHomesRepository
import theoneclick.client.features.home.viewModels.HomesListViewModel
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
    internal fun homesRepository(bind: InMemoryHomesRepository): HomesRepository = bind

    internal abstract val homesListViewModelFactory: () -> HomesListViewModel
    internal abstract val userSettingsViewModelFactory: () -> UserSettingsViewModel
}

@KmpComponentCreate
expect fun createHomeComponent(coreComponent: CoreComponent): HomeComponent

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
private annotation class HomeScope