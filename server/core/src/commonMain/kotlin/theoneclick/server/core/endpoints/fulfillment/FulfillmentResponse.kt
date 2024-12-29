package theoneclick.server.core.endpoints.fulfillment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FulfillmentResponse(
    val requestId: String,
    val payload: Payload
) {
    @Serializable
    data class Payload(
        val agentUserId: String,
        @SerialName("devices")
        val genericDevices: List<GenericDevice>
    ) {
        @Serializable
        data class GenericDevice(
            val id: String,
            val type: String,
            val traits: List<String>,
            val name: DeviceName,
            val roomHint: String,
            val willReportState: Boolean,
            val attributes: List<Attribute>,
        ) {
            @Serializable
            data class DeviceName(
                val name: String,
            )

            @Serializable
            data class Attribute(
                val rotationDegreesRange: RotationDegreesRange,
                val supportsDegrees: Boolean,
                val supportsPercent: Boolean,
            ) {
                @Serializable
                data class RotationDegreesRange(
                    val rotationDegreesMax: Int,
                    val rotationDegreesMin: Int,
                )
            }
        }
    }
}
