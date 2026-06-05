package com.payloadSender

import java.net.Socket
import java.net.SocketTimeoutException

object PayloadSender {

    data class Result(val success: Boolean, val message: String)

    fun send(ip: String, port: Int, data: ByteArray): Result {
        return try {
            Socket().use { socket ->
                socket.connect(java.net.InetSocketAddress(ip, port), 5000)
                socket.soTimeout = 10000

                val out = socket.getOutputStream()
                out.write(data)
                out.flush()
            }
            Result(true, "OK")
        } catch (e: SocketTimeoutException) {
            Result(false, "Timeout: can't connect to $ip:$port")
        } catch (e: java.net.ConnectException) {
            Result(false, "Connection refused on $ip:$port")
        } catch (e: java.net.UnknownHostException) {
            Result(false, "Host unknown: $ip")
        } catch (e: Exception) {
            Result(false, e.message ?: "Unknown error")
        }
    }
}
