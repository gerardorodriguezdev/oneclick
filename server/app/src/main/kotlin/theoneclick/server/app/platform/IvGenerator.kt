package theoneclick.server.app.platform

interface IvGenerator {
    fun iv(size: Int): ByteArray
}

class JvmIvGenerator(
    private val jvmSecureRandomProvider: JvmSecureRandomProvider,
) : IvGenerator {

    override fun iv(size: Int): ByteArray {
        val secureRandom = jvmSecureRandomProvider.secureRandom()
        return ByteArray(size).apply {
            secureRandom.nextBytes(this)
        }
    }
}
