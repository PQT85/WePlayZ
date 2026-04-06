package vn.weplayz.sdk.api.request




import android.annotation.SuppressLint
import android.text.TextUtils
import okhttp3.OkHttpClient

import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import org.json.JSONException
import vn.weplayz.sdk.constants.Constants
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

open class BaseRequest {

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<X509TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(java.security.cert.CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(java.security.cert.CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                val acceptedIssuers: Array<Any?>?
                    get() = arrayOfNulls(0)
            })

            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
    protected fun excuteRequest(connection: HttpURLConnection): String {
        connection.connect()
        val status = connection.responseCode
        val response = StringBuilder()
        if (status in 200..299) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            do {
                val line = reader.readLine()
                response.append(line)
            } while (line != null)
            reader.close()
            connection.disconnect()
            throwInternalExceptionIfHas(response.toString())
        } else if (status == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            throw java.lang.Exception("Server Error")
        } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw java.lang.Exception("Authorized Error")
        } else {
            throw java.lang.Exception("Network error")
        }
        return response.toString()
    }

    protected fun executeRequestPut(connection: HttpURLConnection): Boolean {
        connection.connect()
        val status = connection.responseCode
        return if (status in 200..299) {
            true
        } else {
            val response = StringBuilder()
            val reader = BufferedReader(InputStreamReader(connection.errorStream))
            val line = reader.readLine()
            while (line != null) {
                response.append(line)
            }
            reader.close()
            connection.disconnect()
            throwInternalExceptionIfHas(response.toString())
            false
        }
    }

    private fun throwInternalExceptionIfHas(json: String?) {
//        Log.d(Constants.LOG_TAG, "POST_RESULT: ${json.toString()}")
        vn.weplayz.sdk.constants.Constants.showDataLog(vn.weplayz.sdk.constants.Constants.LOG_TAG, "POST_RESULT: ${json.toString()}")
        try {
            val jsonObject = JSONObject(json)
            val status = jsonObject.getInt("e")
            if (status != 0) {
                val messageR = jsonObject.optString("r")
                if ( !TextUtils.isEmpty(messageR) && !messageR.equals("null")) {
                    throw CodeError(status, messageR + " -- statusCode: "+status)
                } else {
                    throw CodeError(status, jsonObject.optString("m","unknow") + " -- statusCode: "+status)
                }
            }
        }catch (e : JSONException)
        {
            throw CodeError(-1,"Dữ liệu lỗi.Vui lòng thử lại sau!" + " -- statusCode: -1")
        }

    }
}