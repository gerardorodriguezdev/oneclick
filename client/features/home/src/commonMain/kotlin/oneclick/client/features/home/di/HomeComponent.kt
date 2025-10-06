package oneclick.client.features.home.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import oneclick.client.features.home.dataSources.HomesDataSource
import oneclick.client.features.home.dataSources.RemoteHomesDataSource
import oneclick.client.features.home.repositories.DefaultHomesRepository
import oneclick.client.features.home.repositories.HomesRepository
import oneclick.client.features.home.viewModels.HomesListViewModel
import oneclick.client.features.home.viewModels.UserSettingsViewModel
import oneclick.client.shared.di.CoreComponent

@HomeScope
@Component
abstract class HomeComponent(@Component val coreComponent: CoreComponent) {

    @HomeScope
    @Provides
    internal fun loggedDataSource(bind: RemoteHomesDataSource): HomesDataSource = bind

    @HomeScope
    @Provides
    internal fun homesRepository(bind: DefaultHomesRepository): HomesRepository = bind

    internal abstract val homesListViewModelFactory: () -> HomesListViewModel
    internal abstract val userSettingsViewModelFactory: () -> UserSettingsViewModel
}

@KmpComponentCreate
expect fun createHomeComponent(coreComponent: CoreComponent): HomeComponent

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
private annotation class HomeScope
