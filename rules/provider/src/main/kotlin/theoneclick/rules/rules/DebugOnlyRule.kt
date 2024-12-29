package theoneclick.rules.rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import theoneclick.rules.extensions.hasAnnotationFullName

class DebugOnlyRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "DebugOnly",
        severity = Severity.CodeSmell,
        description = "Restricts the use of anything annotated with @DebugOnly",
        debt = Debt.FIVE_MINS,
    )

    override fun visitAnnotationEntry(annotation: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotation)

        val annotationShortName = annotation.shortName?.asString()
        if (annotationShortName == ANNOTATION_SHORT_NAME) {
            val annotationImportDirectives = annotation.containingKtFile.importDirectives
            val hasAnnotationFullName = annotationImportDirectives.hasAnnotationFullName(ANNOTATION_FULL_NAME)

            if (hasAnnotationFullName) {
                report(annotation)
            }
        }
    }

    private fun report(annotation: KtAnnotationEntry) {
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(annotation),
                message = "Usage of @DebugOnly annotation found. This should not be used in production code",
            )
        )
    }

    private companion object {
        const val ANNOTATION_FULL_NAME = "theoneclick.rules.models.DebugOnly"
        const val ANNOTATION_SHORT_NAME = "DebugOnly"
    }
}
