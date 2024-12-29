@file:Suppress("NoCallbacksInFunctions")

package theoneclick.shared.core.extensions

fun <T1, T2, R> ifNotNull(p1: T1?, p2: T2?, block: (T1, T2) -> R): R? =
    if (p1 != null && p2 != null) {
        block(p1, p2)
    } else {
        null
    }

fun <T1, T2, T3, R> ifNotNull(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R): R? =
    if (p1 != null && p2 != null && p3 != null) {
        block(p1, p2, p3)
    } else {
        null
    }

@Suppress("ComplexCondition")
fun <T1, T2, T3, T4, R> ifNotNull(p1: T1?, p2: T2?, p3: T3?, p4: T4?, block: (T1, T2, T3, T4) -> R): R? =
    if (p1 != null && p2 != null && p3 != null && p4 != null) {
        block(p1, p2, p3, p4)
    } else {
        null
    }

@Suppress("LongParameterList", "ComplexCondition")
fun <T1, T2, T3, T4, T5, R> ifNotNull(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    block: (T1, T2, T3, T4, T5) -> R
): R? =
    if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) {
        block(p1, p2, p3, p4, p5)
    } else {
        null
    }
