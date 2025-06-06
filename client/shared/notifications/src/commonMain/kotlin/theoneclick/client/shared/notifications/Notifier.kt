package theoneclick.client.shared.notifications

interface Notifier {
    fun sendSuccessNotification(message: String)
    fun sendErrorNotification(message: String)
}