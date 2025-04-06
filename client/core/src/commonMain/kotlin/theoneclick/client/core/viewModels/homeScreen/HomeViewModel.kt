package theoneclick.client.core.viewModels.homeScreen

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope

class HomeViewModel : ViewModel(), KoinScopeComponent {
    override val scope: Scope = createScope(SCOPE_ID)

    override fun onCleared() {
        super.onCleared()

        scope.close()
    }

    companion object {
        const val SCOPE_ID = "HomeViewModel"
    }
}