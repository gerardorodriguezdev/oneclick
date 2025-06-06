package theoneclick.client.shared.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import theoneclick.client.shared.notifications.NotificationsController.Notification

interface NotificationsController {
    val notificationEvents: SharedFlow<Notification>

    suspend fun sendSuccessNotification(message: String)
    suspend fun sendErrorNotification(message: String)

    sealed interface Notification {
        val message: String

        data class Success(override val message: String) : Notification
        data class Error(override val message: String) : Notification
    }
}

class DefaultNotificationsController : NotificationsController {
    private val _notificationEvents = MutableSharedFlow<Notification>()
    override val notificationEvents: SharedFlow<Notification> = _notificationEvents

    override suspend fun sendSuccessNotification(message: String) {
        _notificationEvents.emit(Notification.Success(message))
    }

    override suspend fun sendErrorNotification(message: String) {
        _notificationEvents.emit(Notification.Error(message))
    }
}