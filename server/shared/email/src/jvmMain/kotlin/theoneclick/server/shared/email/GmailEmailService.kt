package theoneclick.server.shared.email

import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.withContext
import oneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.server.shared.email.base.EmailService
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class GmailEmailService(
    private val fromEmail: String,
    private val password: String,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : EmailService {

    override suspend fun sendEmail(toEmail: String, subject: String, body: String): Boolean =
        withContext(dispatchersProvider.io()) {
            val session = session()
            val message = message(session = session, toEmail = toEmail, subject = subject, body = body)
            try {
                Transport.send(message)
                true
            } catch (e: Exception) {
                logger.log(Level.SEVERE, "Error sending email", e)
                false
            }
        }

    private fun session(): Session =
        Session.getInstance(
            sessionProperties,
            PasswordAuthenticator()
        )

    private fun message(session: Session, toEmail: String, subject: String, body: String): MimeMessage =
        MimeMessage(session).apply {
            setFrom(InternetAddress(fromEmail))
            setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
            )
            setSubject(subject)
            setText(body, CONTENT_CHARSET)
        }

    private inner class PasswordAuthenticator : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(fromEmail, password)
        }
    }

    private companion object {
        const val CONTENT_CHARSET = "UTF-8"

        val sessionProperties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
            put("mail.smtp.connectiontimeout", "10000")
            put("mail.smtp.timeout", "10000")
            put("mail.smtp.writetimeout", "10000")
        }
    }
}