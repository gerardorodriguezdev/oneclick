package theoneclick.client.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TheOneClickTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme(),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
            extraLarge = RoundedCornerShape(24.dp)
        ),
        typography = Typography(
            labelSmall = TextStyle.Default.copy(
                fontSize = 10.sp,
            )
        ),
        content = content,
    )
}

//TODO: Report errors from vm bubble to parent
//TODO: Sep ui state from logic state
//TODO: Update AddDevice + DevicesList (vm + ui)
//TODO: Update naming strings
//TODO: Update public/private methods
//TODO: Make AppScreen cleaner
//TODO: UI to other module + Presentation to other module
//TODO: Static di
//TODO: Separate home module
//TODO: Maybe sources abstraction
//TODO: Common models to safe validated ones? token, deviceName, roomName, rotation range, uuid, username, password
//TODO: Naming TheOneClick vs OneClick
//TODO: App icon
//TODO: Proper error handling
//TODO: Server update endpoint (addDevice, devices, updateDevice)