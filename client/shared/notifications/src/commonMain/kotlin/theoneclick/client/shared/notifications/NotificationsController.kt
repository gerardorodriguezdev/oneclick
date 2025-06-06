package theoneclick.client.shared.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import theoneclick.client.shared.notifications.NotificationsController.Notification

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
    private val _notificationEvents = MutableSharedFlow<Notification?>()
    override val notificationEvents: SharedFlow<Notification?> = _notificationEvents

    override suspend fun showSuccessNotification(message: String) {
        _notificationEvents.emit(Notification.Success(message))
    }

    override suspend fun showErrorNotification(message: String) {
        _notificationEvents.emit(Notification.Error(message))
    }

    override suspend fun clearNotifications() {
        _notificationEvents.emit(null)
    }
}