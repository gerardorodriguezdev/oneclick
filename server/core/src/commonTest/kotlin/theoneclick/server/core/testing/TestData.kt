package theoneclick.server.core.testing

import kotlinx.serialization.json.Json
import theoneclick.server.core.models.EncryptedToken
import theoneclick.server.core.models.HashedPassword
import theoneclick.server.core.models.User
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.platform.Environment
import theoneclick.shared.core.models.entities.Device.Blind
import theoneclick.shared.core.models.entities.Uuid

@Suppress("MaxLineLength")
object TestData {
    const val CURRENT_TIME_IN_MILLIS = 1718895344535L
    const val UUID = "aad99301-5ede-481a-979a-3dfe32af40e8"

    // Secrets
    const val SECRET_SIGN_KEY = "30db10b1baa440d7da9a68bee86e8a0b4f90d83c057ea490688a6da3da0f4a79"
    const val SECRET_ENCRYPTION_KEY = "00112233445566778899aabbccddeeff"

    // Devices
    const val DEVICE_NAME = "Blind1"
    const val ROOM = "Bedroom"

    // User
    const val USERNAME = "Username"
    const val RAW_PASSWORD = "StrongPassword---65+_!"
    const val HASHED_PASSWORD = """$2a$12${'$'}a7SYs7gas/jtBtrmitjLY.Gd6LAFrO2WDfhJjrnri9/Fp7.wQ5.rK"""
    const val ENCRYPTED_USER_SESSION_DATA_STRING =
        "73d51abbd89cb8196f0efb6892f94d68%2Fc203c3b8761834f2702cd8823db843f40f34d484679937bdcb432d475128d919916e5fde54df55da11c27f4580ecd5201485d1c6e527bb7e7aee468c74416430b02715bf6184deafca5c9a438582ae4ac2961ab8df47053e06c3dba4c7c5f731d4eb3eb94a0d5b500ccad876165e955da02708d92dd4aa7f522ffdc674660228%3A1ac9b1ee3a938b9c5b82d7b72d6c2e64e637f3330f74bc7629d99ec0be952fee"
    const val SECURE_RANDOM_SEED = 1L
    const val ENCRYPTED_TOKEN_VALUE =
        "c9Uau9icuBlvDvtokvlNaMMQBXUiGxSN5oDSfERIfy5aGFKNOyYlGkM2dwkHiBExZSyi2fpP6kHh3Z9+9nzVE+58aMCLWi7FoZa+g1h80/Q="

    val validUserSession = UserSession(
        sessionToken = ENCRYPTED_TOKEN_VALUE
    )

    val encryptedToken = EncryptedToken(
        value = ENCRYPTED_TOKEN_VALUE,
        creationTimeInMillis = CURRENT_TIME_IN_MILLIS,
    )

    val blind = Blind(
        id = Uuid(UUID),
        deviceName = DEVICE_NAME,
        room = ROOM,
        isOpened = false,
        rotation = 180,
    )
    val devices = listOf(blind)

    val user = User(
        id = Uuid(UUID),
        username = USERNAME,
        hashedPassword = HashedPassword(HASHED_PASSWORD),
        sessionToken = encryptedToken,
        devices = devices,
    )

    val userString = Json.encodeToString(user)

    val userByteArray = userString.encodeToByteArray()

    val environment = Environment(
        secretSignKey = SECRET_SIGN_KEY,
        secretEncryptionKey = SECRET_ENCRYPTION_KEY,
        host = "localhost",
        enableQAAPI = true,
        disableRateLimit = true,
    )
}
