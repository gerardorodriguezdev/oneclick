package theoneclick.server.shared.email.base

interface EmailService {
    suspend fun sendEmail(subject: String, body: String): Boolean
}