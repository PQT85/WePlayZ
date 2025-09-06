package vn.mgjsc.sdk.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager



object NetworkUtils {
    @JvmStatic
    fun hasNetworkStatePermission(context: Context): Boolean {
        return hasPermission(
            context,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    }
    @JvmStatic
    fun isNetworkConnected(context: Context?): Boolean {
        if(context == null)
            return false;
        if (hasNetworkStatePermission(context)) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if(cm!=null)
                return cm?.activeNetworkInfo != null && cm?.activeNetworkInfo!!.isConnected
            else
                return true
        }
        return true
    }
    @JvmStatic
    fun hasPermission(
        context: Context,
        permission: String
    ): Boolean {
        return context.checkCallingOrSelfPermission(permission) ==
                PackageManager.PERMISSION_GRANTED
    }
//    fun isNetworkConnected(context: Context): Boolean {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        var activeNetwork: NetworkInfo? = null
//        if (cm != null) {
//            activeNetwork = cm.activeNetworkInfo
//        }
//        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
//    }
}