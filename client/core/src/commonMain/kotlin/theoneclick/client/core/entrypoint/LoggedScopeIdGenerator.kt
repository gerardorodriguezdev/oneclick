package theoneclick.client.core.entrypoint

interface LoggedScopeIdGenerator {
    fun scopeId(): String
}

class StaticLoggedScopeIdGenerator : LoggedScopeIdGenerator {
    override fun scopeId(): String = "LoggedScopeId"
}
