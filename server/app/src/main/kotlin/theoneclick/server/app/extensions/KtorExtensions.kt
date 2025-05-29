package theoneclick.server.app.extensions

import io.ktor.http.*

fun urlString(block: URLBuilder.() -> Unit = {}): String = URLBuilder().apply(block).buildString()
