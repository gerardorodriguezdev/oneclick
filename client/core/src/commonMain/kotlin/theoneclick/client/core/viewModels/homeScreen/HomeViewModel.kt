package theoneclick.client.core.viewModels.homeScreen

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope
import theoneclick.client.core.extensions.typed

class HomeViewModel(scopeId: String) : ViewModel(), KoinScopeComponent {

    override val scope: Scope = getKoin().createScope(
        scopeId = scopeId,
        qualifier = typed(HomeViewModel::class),
    )

    override fun onCleared() {
        super.onCleared()

        scope.close()
    }
}
