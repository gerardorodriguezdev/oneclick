package theoneclick.server.core.endpoints.fulfillment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class FulfillmentRequest(
    val requestId: String,
    @SerialName("inputs")
    private val inputsStrings: List<InputString>,

    @Transient
    val input: Input = inputsStrings.toInput(),
) {
    @Serializable
    data class InputString(
        val intent: String
    )

    data class Input(
        val intent: Intent,
    ) {
        sealed interface Intent {
            data object Sync : Intent

            companion object {
                const val SYNC_INTENT = "action.devices.SYNC"
            }
        }
    }

    companion object {
        fun List<InputString>.toInput(): Input =
            firstNotNullOf { inputString ->
                when (inputString.intent) {
                    Input.Intent.SYNC_INTENT -> Input(
                        intent = Input.Intent.Sync
                    )

                    else -> null
                }
            }
    }
}
