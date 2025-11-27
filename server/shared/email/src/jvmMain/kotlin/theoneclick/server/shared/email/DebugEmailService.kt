package theoneclick.server.shared.email

import oneclick.shared.logging.AppLogger
import theoneclick.server.shared.email.base.EmailService

class DebugEmailService(private val appLogger: AppLogger) : EmailService {
    override suspend fun sendEmail(
        toEmail: String,
        subject: String,
        body: String
    ): Boolean {
        appLogger.i(
            """
                toEmail: $toEmail
                subject: $subject
                body: $body
            """.trimIndent()
        )
        return true
    }
}