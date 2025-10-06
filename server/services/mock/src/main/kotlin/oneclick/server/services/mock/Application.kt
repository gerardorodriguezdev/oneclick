package oneclick.server.services.mock

import oneclick.server.services.mock.entrypoint.server

fun main() {
    server().start(wait = true)
}
