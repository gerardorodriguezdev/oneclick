package oneclick.client.apps.home

import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        println("Starting")

        while (isActive) {
            print("> ")
            val input = readlnOrNull()?.trim()
        }
    }
}