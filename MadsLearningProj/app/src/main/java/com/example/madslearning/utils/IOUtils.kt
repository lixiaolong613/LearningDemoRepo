package com.example.bitmapdemo

import java.io.*
import java.net.Socket

object IOUtils {
    fun closeQuietly(input: Reader?) {
        closeQuietly(input as Closeable?)
    }

    fun closeQuietly(output: Writer?) {
        closeQuietly(output as Closeable?)
    }

    fun closeQuietly(input: InputStream?) {
        closeQuietly(input as Closeable?)
    }

    fun closeQuietly(output: OutputStream?) {
        closeQuietly(output as Closeable?)
    }

    fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (var2: IOException) {
        }
    }

    fun closeQuietly(closeables: Array<Closeable>?) {
        if (closeables != null) {
            val var1: Array<Closeable> = closeables
            val var2 = closeables.size
            for (var3 in 0 until var2) {
                val closeable = var1[var3]
                closeQuietly(closeable)
            }
        }
    }

    fun closeQuietly(sock: Socket?) {
        if (sock != null) {
            try {
                sock.close()
            } catch (var2: IOException) {
            }
        }
    }
}