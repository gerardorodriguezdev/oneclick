package oneclick.client.apps.home

import oneclick.client.apps.home.commands.DefaultCommandsHandler
import oneclick.client.apps.home.dataSources.MemoryDevicesStore
import oneclick.client.apps.home.dataSources.RemoteHomeDataSource
import oneclick.client.apps.home.devices.BluetoothDevicesController
import oneclick.client.apps.home.devices.FakeDevicesController
import oneclick.client.shared.network.dataSources.DataStoreEncryptedPreferences
import oneclick.client.shared.network.dataSources.LocalTokenDataSource
import oneclick.client.shared.network.dataSources.RemoteAuthenticationDataSource
import oneclick.client.shared.network.extensions.urlProtocol
import oneclick.client.shared.network.nativeHttpClient
import oneclick.client.shared.network.platform.okhttpHttpClientEngine
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.dispatchers.platform.dispatchersProvider
import oneclick.shared.logging.appLogger
import oneclick.shared.security.DefaultSecureRandomProvider
import oneclick.shared.security.encryption.FileKeystoreEncryptor
import java.io.File

fun main() {
    val environment = Environment()
    val dispatchersProvider = dispatchersProvider()
    val appLogger = appLogger()
    val secureRandomProvider = DefaultSecureRandomProvider()
    val tokenDataSource = LocalTokenDataSource(
        preferences = DataStoreEncryptedPreferences(
            preferencesFileProvider = {
                val localDirectory = File("local")
                if (!localDirectory.exists()) localDirectory.mkdirs()

                val preferencesFile = File(localDirectory, "settings.preferences_pb")
                if (!preferencesFile.exists()) preferencesFile.createNewFile()
                preferencesFile
            },
            appLogger = appLogger,
            dispatchersProvider = dispatchersProvider,
            encryptor = FileKeystoreEncryptor(
                keyStorePath = environment.keyStorePath,
                keyStorePassword = environment.keyStorePassword.toCharArray(),
                secureRandomProvider = secureRandomProvider,
            ),
        )
    )
    val devicesStore = MemoryDevicesStore()
    val httpClient = nativeHttpClient(
        appLogger = appLogger,
        urlProtocol = environment.protocol.urlProtocol(),
        host = environment.host,
        port = environment.port,
        clientType = ClientType.DESKTOP,
        httpClientEngine = okhttpHttpClientEngine(),
        tokenDataSource = tokenDataSource,
        logoutManager = HomeLogoutManager(
            devicesStore = devicesStore,
            tokenDataSource = tokenDataSource,
        ),
    )
    val authenticationDataSource = RemoteAuthenticationDataSource(
        dispatchersProvider = dispatchersProvider,
        httpClient = httpClient,
        tokenDataSource = tokenDataSource,
        appLogger = appLogger,
    )
    val homeDataSource = RemoteHomeDataSource(
        httpClient = httpClient,
        dispatchersProvider = dispatchersProvider,
        appLogger = appLogger,
    )
    Entrypoint(
        dispatchersProvider = dispatchersProvider,
        authenticationDataSource = authenticationDataSource,
        devicesStore = devicesStore,
        homeDataSource = homeDataSource,
        appLogger = appLogger,
        commandsHandler = DefaultCommandsHandler(
            authenticationDataSource = authenticationDataSource,
            appLogger = appLogger,
            homeId = environment.homeId,
        ),
        devicesController = if (environment.useFakeDevicesController) {
            FakeDevicesController(
                appLogger = appLogger,
                devicesStore = devicesStore,
            )
        } else {
            BluetoothDevicesController(
                appLogger = appLogger,
                devicesStore = devicesStore,
            )
        }
    ).start()
}

private data class Environment(
    val homeId: Uuid = Uuid.unsafe(System.getenv("HOME_ID")),
    val keyStorePath: String = System.getenv("KEYSTORE_PATH"),
    val keyStorePassword: String = System.getenv("KEYSTORE_PASSWORD"),
    val protocol: String = System.getenv("PROTOCOL"),
    val host: String = System.getenv("HOST"),
    val port: Int? = System.getenv("PORT")?.toIntOrNull(),
    val useFakeDevicesController: Boolean = System.getenv("USE_FAKE_DEVICES_CONTROLLER") == "true",
)
