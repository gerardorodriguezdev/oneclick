package theoneclick.server.core.plugins.koin

import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope

class RequestScope(private val koin: Koin) : KoinScopeComponent {
    override fun getKoin(): Koin = koin
    override val scope = createScope()
}
