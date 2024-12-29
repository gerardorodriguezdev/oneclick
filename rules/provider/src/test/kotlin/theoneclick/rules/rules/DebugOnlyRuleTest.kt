package theoneclick.rules.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class DebugOnlyRuleTest {
    private val subject = DebugOnlyRule(Config.empty)

    @Test
    fun `GIVEN debugOnly applied to function WHEN used THEN returns finding`() {
        val findings = subject.lint(functionWithDebugOnly)
        assertEquals(1, findings.size)
    }

    @Test
    fun `GIVEN debugOnly not applied to function but imported WHEN used THEN returns no finding`() {
        val findings = subject.lint(functionWithoutDebugOnly)
        assertEquals(0, findings.size)
    }

    companion object {
        private val functionWithDebugOnly = """
            
        import theoneclick.rules.models.DebugOnly
        
        @DebugOnly
        fun function() {}

        fun otherFunction() {
            function()
        }
        """.trimIndent()

        private val functionWithoutDebugOnly = """
            
        import theoneclick.rules.models.DebugOnly
        
        fun function() {}

        fun otherFunction() {
            function()
        }
        """.trimIndent()
    }
}
