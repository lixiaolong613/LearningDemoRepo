package com.example.bitmapdemo

import android.net.*
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
object NetworkUtils {

    fun isWifiAvailable() : Boolean{
        val caps = getActiveNetworkCapabilities()
        return caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    }

    /**
     * 提供关于路由、链接地址、接口名称、代理信息（如有）和 DNS 服务器的信息
     */
    private fun getActiveNetworkProperties(): LinkProperties? {
        val connectivityManager: ConnectivityManager? =
            CommonUtils.getSystemService<ConnectivityManager>("connectivity")
        val currentWork: Network? = connectivityManager?.activeNetwork//获取对应用当前默认网络的引用
        return connectivityManager?.getLinkProperties(currentWork)
    }

    /**
     * 封装了有关网络传输及其功能的信息。
     */
    private fun getActiveNetworkCapabilities(): NetworkCapabilities? {
        val connectivityManager: ConnectivityManager? =
            CommonUtils.getSystemService<ConnectivityManager>("connectivity")
        val currentWork: Network? = connectivityManager?.activeNetwork//获取对应用当前默认网络的引用
        return connectivityManager?.getNetworkCapabilities(currentWork)
    }
}