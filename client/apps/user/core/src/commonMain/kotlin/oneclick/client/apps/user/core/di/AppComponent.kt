package oneclick.client.apps.user.core.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import oneclick.client.apps.user.core.viewModels.InitViewModel
import oneclick.client.apps.user.core.viewModels.LoginViewModel
import oneclick.client.shared.di.CoreComponent

@Component
abstract class AppComponent(
    @Component val coreComponent: CoreComponent
) {
    abstract val initViewModelFactory: () -> InitViewModel
    abstract val loginViewModelFactory: () -> LoginViewModel
}

@KmpComponentCreate
expect fun createAppComponent(coreComponent: CoreComponent): AppComponent
