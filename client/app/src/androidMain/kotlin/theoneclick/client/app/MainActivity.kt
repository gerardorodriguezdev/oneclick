package oneclick.client.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import oneclick.client.app.application.OneClickApplication

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val entrypoint = (application as OneClickApplication).appEntrypoint

        setContent {
            entrypoint.App()
        }
    }
}
