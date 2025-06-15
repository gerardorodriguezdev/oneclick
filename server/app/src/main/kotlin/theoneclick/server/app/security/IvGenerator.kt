package theoneclick.server.app.security

interface IvGenerator {
    fun iv(size: Int): ByteArray
}

class DefaultIvGenerator(
    private val secureRandomProvider: SecureRandomProvider,
) : IvGenerator {

    override fun iv(size: Int): ByteArray {
        val secureRandom = secureRandomProvider.secureRandom()
        return ByteArray(size).apply {
            secureRandom.nextBytes(this)
        }
    }
}
