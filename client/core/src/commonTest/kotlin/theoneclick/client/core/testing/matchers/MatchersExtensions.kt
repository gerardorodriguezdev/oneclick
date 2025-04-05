package theoneclick.client.core.testing.matchers

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*

fun hasRole(role: Role): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

fun SemanticsNodeInteractionCollection.onNodeWithTag(testTag: String): SemanticsNodeInteraction =
    filterToOne(hasTestTag(testTag))
