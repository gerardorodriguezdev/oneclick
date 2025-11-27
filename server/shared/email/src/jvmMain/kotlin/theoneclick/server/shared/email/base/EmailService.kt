package theoneclick.server.shared.email.base

interface EmailService {
    suspend fun sendEmail(toEmail: String, subject: String, body: String): Boolean
}