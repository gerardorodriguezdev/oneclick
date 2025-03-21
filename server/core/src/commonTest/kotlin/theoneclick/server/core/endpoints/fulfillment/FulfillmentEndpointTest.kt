package theoneclick.server.core.endpoints.fulfillment

import io.ktor.client.call.*
import io.ktor.http.*
import theoneclick.server.core.endpoints.fulfillment.FulfillmentRequest.InputString
import theoneclick.server.core.endpoints.fulfillment.FulfillmentResponse.Payload
import theoneclick.server.core.endpoints.fulfillment.FulfillmentResponse.Payload.GenericDevice.Attribute.RotationDegreesRange
import theoneclick.server.core.testing.TestData.DEVICE_NAME
import theoneclick.server.core.testing.TestData.ROOM
import theoneclick.server.core.testing.TestData.UUID
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.helpers.TestEndpointsHelper.requestFulfillment
import theoneclick.shared.core.models.entities.Device
import kotlin.test.Test
import kotlin.test.assertEquals

class FulfillmentEndpointTest : IntegrationTest() {

    @Test
    fun `GIVEN valid token WHEN fulfillment request THEN returns ok`() {
        testApplication {
            val response = client.requestFulfillment(
                fulfillmentRequest = FulfillmentRequest(
                    requestId = EXPECTED_REQUEST_ID,
                    inputsStrings = listOf(
                        InputString(FulfillmentRequest.Input.Intent.SYNC_INTENT)
                    ),
                )
            )

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(expected = expectedFulfillmentResponse, actual = response.body<FulfillmentResponse>())
        }
    }

    private companion object {
        const val EXPECTED_REQUEST_ID = "1"

        val expectedFulfillmentResponse = FulfillmentResponse(
            requestId = EXPECTED_REQUEST_ID,
            payload = Payload(
                agentUserId = UUID,
                genericDevices = listOf(
                    Payload.GenericDevice(
                        id = UUID,
                        type = "action.devices.types.BLINDS",
                        traits = listOf(
                            "action.devices.traits.OpenClose",
                            "action.devices.traits.Rotation"
                        ),
                        name = Payload.GenericDevice.DeviceName(
                            name = DEVICE_NAME,
                        ),
                        roomHint = ROOM,
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
                        ),
                    )
                )
            )
        )
    }
}
