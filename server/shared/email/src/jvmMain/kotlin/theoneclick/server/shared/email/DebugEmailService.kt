package theoneclick.server.shared.email

import theoneclick.server.shared.email.base.EmailService
import java.util.logging.Level
import java.util.logging.Logger

class DebugEmailService(private val logger: Logger) : EmailService {
    override suspend fun sendEmail(
        toEmail: String,
        subject: String,
        body: String
    ): Boolean {
        logger.log(
            Level.INFO,
            """
                toEmail: $toEmail
                subject: $subject
                body: $body
            """.trimIndent()
        )
        return true
    }
}