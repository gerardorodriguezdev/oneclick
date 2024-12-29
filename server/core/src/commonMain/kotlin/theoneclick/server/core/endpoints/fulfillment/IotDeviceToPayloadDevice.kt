package theoneclick.server.core.endpoints.fulfillment

import theoneclick.server.core.endpoints.fulfillment.FulfillmentResponse.Payload
import theoneclick.server.core.endpoints.fulfillment.FulfillmentResponse.Payload.GenericDevice.Attribute.RotationDegreesRange
import theoneclick.shared.core.dataSources.models.entities.Device

fun Device.toDevice(): Payload.GenericDevice =
    when (this) {
        is Device.Blind -> Payload.GenericDevice(
            id = id.value,
            type = "action.devices.types.BLINDS",
            traits = listOf(
                "action.devices.traits.OpenClose",
                "action.devices.traits.Rotation"
            ),
            name = Payload.GenericDevice.DeviceName(
                name = deviceName,
            ),
            roomHint = room,
            willReportState = false,
            attributes = listOf(
                Payload.GenericDevice.Attribute(
                    rotationDegreesRange = RotationDegreesRange(
                        rotationDegreesMin = Device.Blind.blindRange.start,
                        rotationDegreesMax = Device.Blind.blindRange.end,
                    ),
                    supportsDegrees = true,
                    supportsPercent = true,
                )
            )
        )
    }
