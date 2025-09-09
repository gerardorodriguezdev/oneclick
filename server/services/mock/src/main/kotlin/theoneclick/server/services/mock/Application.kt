package theoneclick.server.services.mock

import theoneclick.server.services.mock.entrypoint.server

fun main() {
    server().start(wait = true)
}
