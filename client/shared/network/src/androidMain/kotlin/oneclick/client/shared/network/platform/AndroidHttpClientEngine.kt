package oneclick.client.shared.network.platform

import android.net.TrafficStats
import io.ktor.client.engine.*
import oneclick.shared.timeProvider.TimeProvider
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

fun androidHttpClientEngine(timeProvider: TimeProvider): HttpClientEngine =
    okhttpHttpClientEngine {
        socketFactory(
            DelegatingSocketFactory(
                configureSocket = {
                    TrafficStats.setThreadStatsTag(timeProvider.currentTimeMillis().toInt())
                }
            )
        )
    }

private class DelegatingSocketFactory(
    private val configureSocket: Socket.() -> Unit,
) : SocketFactory() {

    override fun createSocket(): Socket {
        val socket = getDefault().createSocket()
        return socket.apply {
            configureSocket()
        }
    }

    override fun createSocket(host: String, port: Int): Socket {
        val socket = getDefault().createSocket(host, port)
        return socket.apply {
            configureSocket()
        }
    }

    override fun createSocket(host: String, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        val socket = getDefault().createSocket(host, port, localAddress, localPort)
        return socket.apply {
            configureSocket()
        }
    }

    override fun createSocket(host: InetAddress, port: Int): Socket {
        val socket = getDefault().createSocket(host, port)
        return socket.apply {
            configureSocket()
        }
    }

    override fun createSocket(host: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        val socket = getDefault().createSocket(host, port, localAddress, localPort)
        return socket.apply {
            configureSocket()
        }
    }
}
