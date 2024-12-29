package theoneclick.rules.extensions

import org.jetbrains.kotlin.psi.KtImportDirective

fun List<KtImportDirective>.hasAnnotationFullName(annotationFullName: String): Boolean =
    any { importDirective ->
        importDirective.importedFqName?.asString() == annotationFullName
    }
