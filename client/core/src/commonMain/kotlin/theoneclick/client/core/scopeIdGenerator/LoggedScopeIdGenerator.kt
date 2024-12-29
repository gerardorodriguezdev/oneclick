package theoneclick.client.core.scopeIdGenerator

interface LoggedScopeIdGenerator {
    fun scopeId(): String
}

class StaticLoggedScopeIdGenerator : LoggedScopeIdGenerator {
    override fun scopeId(): String = "LoggedScopeId"
}
