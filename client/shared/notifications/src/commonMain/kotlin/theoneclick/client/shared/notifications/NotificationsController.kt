package oneclick.client.shared.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import oneclick.client.shared.notifications.NotificationsController.Notification

interface NotificationsController {
    val notificationEvents: SharedFlow<Notification?>

    suspend fun showSuccessNotification(message: String)
    suspend fun showErrorNotification(message: String)
    suspend fun clearNotifications()

    sealed interface Notification {
        val message: String

        data class Success(override val message: String) : Notification
        data class Error(override val message: String) : Notification
    }
}

class DefaultNotificationsController : NotificationsController {
    private val mutableNotificationEvents = MutableSharedFlow<Notification?>()
    override val notificationEvents: SharedFlow<Notification?> = mutableNotificationEvents

    override suspend fun showSuccessNotification(message: String) {
        mutableNotificationEvents.emit(Notification.Success(message))
    }

    override suspend fun showErrorNotification(message: String) {
        mutableNotificationEvents.emit(Notification.Error(message))
    }

    override suspend fun clearNotifications() {
        mutableNotificationEvents.emit(null)
    }
}
