package theoneclick.server.core.platform

interface IvGenerator {
    fun iv(size: Int): ByteArray
}
