package theoneclick.client.core.mappers

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import theoneclick.client.core.generated.resources.Res
import theoneclick.client.core.generated.resources.general_deviceType_blind
import theoneclick.shared.core.models.entities.DeviceType

@Composable
fun DeviceType.toStringResource(): String =
    when (this) {
        DeviceType.BLIND -> stringResource(Res.string.general_deviceType_blind)
    }
