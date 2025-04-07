package theoneclick.client.core.testing

import theoneclick.client.core.security.Encryptor

class FakeEncryptor(
    var encryptResult: Result<ByteArray>? = null,
    var decryptResult: Result<ByteArray?>? = null,
) : Encryptor {
    override fun encrypt(bytes: ByteArray): Result<ByteArray> = encryptResult ?: Result.success(bytes)
    override fun decrypt(bytes: ByteArray): Result<ByteArray?> = decryptResult ?: Result.success(bytes)
}
