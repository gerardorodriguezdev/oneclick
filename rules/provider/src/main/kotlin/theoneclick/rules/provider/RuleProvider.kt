package theoneclick.rules.provider

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import theoneclick.rules.rules.DebugOnlyRule

class RuleProvider : RuleSetProvider {

    override val ruleSetId: String = "theoneclick-rules"

    override fun instance(config: Config): RuleSet = RuleSet(
        id = ruleSetId,
        rules = listOf(
            DebugOnlyRule(config),
        ),
    )
}
