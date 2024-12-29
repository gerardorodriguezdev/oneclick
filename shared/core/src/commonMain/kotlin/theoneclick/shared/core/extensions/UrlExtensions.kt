package theoneclick.shared.core.extensions

import io.ktor.http.*

fun urlString(block: URLBuilder.() -> Unit = {}): String = URLBuilder().apply(block).buildString()

fun urlBuilder(block: URLBuilder.() -> Unit = {}): URLBuilder = URLBuilder().apply(block)
