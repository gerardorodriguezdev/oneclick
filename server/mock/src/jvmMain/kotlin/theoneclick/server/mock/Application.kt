package theoneclick.server.mock

import theoneclick.server.mock.entrypoint.server

fun main() {
    server().start(wait = true)
}
