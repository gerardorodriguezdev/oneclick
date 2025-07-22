package theoneclick.client.app.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import theoneclick.client.app.viewModels.InitViewModel
import theoneclick.client.app.viewModels.LoginViewModel
import theoneclick.client.shared.di.CoreComponent

@Component
abstract class AppComponent(
    @Component val coreComponent: CoreComponent
) {
    abstract val initViewModelFactory: () -> InitViewModel
    abstract val loginViewModelFactory: () -> LoginViewModel
}

@KmpComponentCreate
expect fun createAppComponent(coreComponent: CoreComponent): AppComponent
