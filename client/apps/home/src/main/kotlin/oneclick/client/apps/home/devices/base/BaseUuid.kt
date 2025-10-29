package oneclick.client.apps.home.devices.base

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class BaseUuid(private val mostSignificantBits: Long) {
    private val uuid = Uuid.fromLongs(mostSignificantBits, LEAST_SIGNIFICANT_BITS)

    operator fun plus(shortUuid: Int): Uuid = plus(shortUuid.toLong())

    operator fun plus(shortUuid: Long): Uuid =
        Uuid.fromLongs(mostSignificantBits + (shortUuid and 0xFFFF_FFFF shl 32), LEAST_SIGNIFICANT_BITS)

    override fun toString(): String = uuid.toString()

    private companion object {
        const val LEAST_SIGNIFICANT_BITS = -5764607523034234880L // b000-000000000000
    }
}