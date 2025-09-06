package vn.mgjsc.sdk.api.request

import android.graphics.Bitmap



import vn.mgjsc.sdk.constants.Constants
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class PostRequest(requestURL: String, private val charset: String) : BaseRequest() {
    companion object {
        private val LINE_FEED = "\r\n"
    }

    private val boundary: String
    private val httpConn: HttpURLConnection
    private val outputStream: OutputStream
    private val writer: PrintWriter

    init {
//        if (BuildConfig.DEBUG) {
//            Log.d(Constants.LOG_TAG, "POST_REQUEST_URL: $requestURL")
//
//        }
        Constants.showDataLog(Constants.LOG_TAG, "POST_REQUEST_URL: $requestURL")
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "==="

        val url = URL(requestURL)
        httpConn = url.openConnection() as HttpURLConnection
        //settimeout connection songpq
        httpConn.connectTimeout = 30000

        httpConn.useCaches = false
        httpConn.doOutput = true // indicates POST method
        httpConn.doInput = true
        httpConn.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=$boundary"
        )

        httpConn.setRequestProperty("User-Agent", "CodeJava Agent")
        //httpConn.setRequestProperty("Connection", "close")
        outputStream = httpConn.outputStream
        writer = PrintWriter(
            OutputStreamWriter(outputStream, charset),
            true
        )
        addHeader("Connection","close")
    }

    fun addField(name: String, value: String): PostRequest {
        Constants.showDataLog(Constants.LOG_TAG, "addField :: ${name}= ${value}")
        writer.append("--").append(boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
            .append(LINE_FEED)
        writer.append("Content-Type: text/plain; charset=").append(charset).append(
            LINE_FEED
        )
        writer.append(LINE_FEED)
        writer.append(value).append(LINE_FEED)
        writer.flush()
        return this
    }

    fun addFile(fieldName: String, uploadFile: File): PostRequest {
        val fileName = uploadFile.name
        writer.append("--").append(boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
            .append("\"; filename=\"").append(fileName).append("\"")
            .append(LINE_FEED)
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
            .append(LINE_FEED)
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()

        val inputStream = FileInputStream(uploadFile)
        val buffer = ByteArray(4096)
        val bytesRead = inputStream.read(buffer)
        while ((bytesRead) != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
        inputStream.close()

        writer.append(LINE_FEED)
        writer.flush()
        return this
    }

    fun addFile(fieldName: String, bitmap: Bitmap): PostRequest {
        val fileName = "image.png"
        writer.append("--").append(boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
            .append("\"; filename=\"").append(fileName).append("\"")
            .append(LINE_FEED)
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
            .append(LINE_FEED)
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val data = bos.toByteArray()
        outputStream.write(data, 0, data.size)
        outputStream.flush()
        bos.close()
        writer.append(LINE_FEED)
        writer.flush()
        return this
    }

    fun addHeader(name: String, value: String): PostRequest {
        Constants.showDataLog(Constants.LOG_TAG, "add Header :: ${name}= ${value}")
        writer.append(name).append(": ").append(value).append(LINE_FEED)
        writer.flush()
        return this
    }

    fun execute(): String {
        writer.append(LINE_FEED).flush()
        writer.append("--").append(boundary).append("--").append(LINE_FEED)
        writer.close()
        return excuteRequest(httpConn)
    }
}