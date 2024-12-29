package theoneclick.client.core.testing.matchers

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*

fun hasRole(role: Role): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

fun SemanticsNodeInteractionCollection.onNodeWithTag(testTag: String): SemanticsNodeInteraction =
    filterToOne(hasTestTag(testTag))

fun SemanticsNodeInteraction.assertProgressBarRangeInfo(progressBarRangeInfo: ProgressBarRangeInfo) {
    assert(
        SemanticsMatcher.expectValue(
            SemanticsProperties.ProgressBarRangeInfo,
            progressBarRangeInfo,
        )
    )
}
