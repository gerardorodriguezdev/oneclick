package theoneclick.client.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import theoneclick.client.core.application.TheOneClickApplication

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val entrypoint = (application as TheOneClickApplication).appEntrypoint

        setContent {
            entrypoint.App()
        }
    }
}
