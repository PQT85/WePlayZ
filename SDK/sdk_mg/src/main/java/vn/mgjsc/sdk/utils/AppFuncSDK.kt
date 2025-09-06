package vn.mgjsc.sdk.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.InputType
import android.util.*
import android.util.Base64
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.mgjsc.sdk.R
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody

import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit


inline fun AppCompatActivity.loadFragment(isAddToBackStack: Boolean = false,
                                          transitionPairs: Map<String, View> = mapOf(),
                                          transaction: FragmentTransaction.() -> Unit) {
    val beginTransaction = supportFragmentManager.beginTransaction()

    //beginTransaction.setCustomAnimations(R.anim.open_screen_anim, R.anim.close_screen_anim, R.anim.open_screen_anim, R.anim.close_screen_anim)
    beginTransaction.transaction()
    for ((name, view) in transitionPairs) {
        ViewCompat.setTransitionName(view, name)
        beginTransaction.addSharedElement(view, name)
    }

    if (isAddToBackStack) beginTransaction.addToBackStack(null)
    beginTransaction.commit()
}

//inline fun AppCompatActivity.loadFragmentAnim(isAddToBackStack: Boolean = false,
//                                              transitionPairs: Map<String, View> = mapOf(),
//                                              transaction: FragmentTransaction.() -> Unit) {
//    val beginTransaction = supportFragmentManager.beginTransaction()
//
//    beginTransaction.setCustomAnimations(R.anim.open_screen_anim, R.anim.close_screen_anim, R.anim.open_screen_anim, R.anim.close_screen_anim)
//    beginTransaction.transaction()
//    for ((name, view) in transitionPairs) {
//        ViewCompat.setTransitionName(view, name)
//        beginTransaction.addSharedElement(view, name)
//    }
//
//    if (isAddToBackStack) beginTransaction.addToBackStack(null)
//    beginTransaction.commit()
//}

//@SuppressLint("PackageManagerGetSignatures")
//fun Activity.getHashKey() {
//    try {
//        for (signature in getApplicationSignature()) {
//            val md: MessageDigest = MessageDigest.getInstance("SHA")
//            md.update(signature.toByteArray())
//            val something = String(Base64.encode(md.digest(), 0))
//            Log.e("hash key", something)
//        }
//    } catch (e1: PackageManager.NameNotFoundException) {
//        Log.e("name not found", e1.toString())
//    } catch (e: NoSuchAlgorithmException) {
//        Log.e("no such an algorithm", e.toString())
//    } catch (e: Exception) {
//        Log.e("exception", e.toString())
//    }
//}

//@SuppressLint("PackageManagerGetSignatures")
//fun Activity.getApplicationSignature(packageName: String = this.packageName): List<String> {
//    val signatureList: List<String>
//    try {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            // New signature
//            val sig = this.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
//            signatureList = if (sig.hasMultipleSigners()) {
//                // Send all with apkContentsSigners
//                sig.apkContentsSigners.map {
//                    val digest = MessageDigest.getInstance("SHA")
//                    digest.update(it.toByteArray())
//                    bytesToHex(digest.digest())
//                }
//            } else {
//                // Send one with signingCertificateHistory
//                sig.signingCertificateHistory.map {
//                    val digest = MessageDigest.getInstance("SHA")
//                    digest.update(it.toByteArray())
//                    bytesToHex(digest.digest())
//                }
//            }
//        } else {
//            val sig = this.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
//            signatureList = sig.map {
//                val digest = MessageDigest.getInstance("SHA")
//                digest.update(it.toByteArray())
//                bytesToHex(digest.digest())
//            }
//        }
//
//        return signatureList
//    } catch (e: Exception) {
//        // Handle error
//    }
//    return emptyList()
//}

fun Activity.getDeviceWidth() = with(this) {
    val displayMetrics = DisplayMetrics()
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        this.display?.getRealMetrics(displayMetrics)
//    } else {
        @Suppress("DEPRECATION")
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
//    }
    displayMetrics.widthPixels
}

fun Activity.getDeviceHeight() = with(this) {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    displayMetrics.heightPixels
}

fun AppCompatActivity.removeFragmentByTag(tag: String): Boolean {
    return removeFragment(supportFragmentManager.findFragmentByTag(tag))
}

fun AppCompatActivity.removeFragmentByID(@IdRes containerID: Int): Boolean {
    return removeFragment(supportFragmentManager.findFragmentById(containerID))
}

fun AppCompatActivity.removeFragment(fragment: Fragment?): Boolean {
    fragment?.let {
        val commit = supportFragmentManager.beginTransaction().remove(fragment).commit()
        return true
    } ?: return false
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

inline fun <R, A> ifIsNotNull(a: A?, block: (A) -> R): R? =
        if (a != null) {
            block(a)
        } else null

inline fun <R, A> ifIsNull(a: A?, block: () -> R): R? =
        if (a == null) {
            block()
        } else null

inline fun <R, A> ifIsNotNull(a: A?, isNotNull: (A) -> R, isNull: () -> R) =
        if (a != null) {
            isNotNull(a)
        } else isNull()

inline fun <R, A : Any> A?.checkNotNull(isNotNull: (A) -> R, isNull: () -> R) =
        let {
            if (it != null) {
                isNotNull(it)
            } else isNull()
        }

//fun convertRequestBodyImages(partName: String, images: List<String>): RequestBody {
//    val builder = MultipartBody.Builder()
//    builder.setType(MultipartBody.FORM)
//    for (i in images.indices) {
//        val path = images[i]
//        val file = File(path)
//
//        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//
//        builder.addFormDataPart(partName, file.name, requestBody)
//    }
//    return builder.build()
//}
//
//fun convertRequestBody(value: String): RequestBody = value.toRequestBody("text/plain".toMediaTypeOrNull())
//fun String.convertToRequestBody(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())
//
//fun convertRequestBodyImage(partName: String, path: String): MultipartBody.Part {
//    val file = File(path)
//    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//    return MultipartBody.Part.createFormData(partName, file.name, requestBody)
//}

fun getTimeStamp(): String {
    return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
}

fun Activity.openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        this.startActivity(intent)
    } catch (ignored: Exception) {
    }
}

fun Context?._getString(resId: Int): String {
    if(this == null)
        return "";
    else
        return this.getString(resId)
}
//fun Activity.openMaps(lat: String, lng: String, name: String) {
//    try {
//
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:<$lat>,<$lng>?q=<$lat>,<$lng>($name)"))
//        intent.setPackage("com.google.android.apps.maps")
//
//        if (intent.resolveActivity(Objects.requireNonNull(this).packageManager) != null) {
//            startActivity(intent)
//        }
//    } catch (ignored: Exception) {
//    }
//
//}

fun String?.checkNull(): String {
    return if (this.isNullOrEmpty())
        "" else this
}

fun String?.getValue(): String {
    if (this.isNullOrEmpty())
        return ""
    else
        return this
}


fun Context.dpFromPx(dp: Float): Float {
    val resources = this.resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.convertPixelsToDp(px: Float): Float {
    return px / (this.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

@SuppressLint("all")
fun Context.getDeviceId(): String {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
}

fun Fragment.toast(message: String, isLong: Boolean = false) {
    Toast.makeText(this.activity, message, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String, isLong: Boolean = false) {
    Toast.makeText(this, message, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Context.inflateView(layout: Int, parent: ViewGroup, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this).inflate(layout, parent, attachToRoot)
}

fun Activity.setClipBoard(text: String) {
    try {
        val clipboard: android.content.ClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip: android.content.ClipData = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        this.toast("Copied Text")
    } catch (e: java.lang.Exception) {
    }
}

fun Context.setClipBoard(text: String) {
    try {
        val clipboard: android.content.ClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip: android.content.ClipData = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        this.toast("Copied Text")
    } catch (e: java.lang.Exception) {
    }
}

fun Activity.showSnackBar(title: String) {
    val parentLayout: View = this.findViewById(android.R.id.content)
    val snackBar = Snackbar.make(parentLayout, title, Snackbar.LENGTH_LONG)
    snackBar.setActionTextColor(Color.BLUE)
    val snackBarView = snackBar.view
    val textView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    textView.setTextColor(Color.WHITE)
    textView.textSize = 18F
    snackBar.show()
}

/**
 * Performs [R] when [T] is not null. Block [R] will have context of [T]
 */
inline fun <T : Any, R> ifNotNull(input: T?, callback: (T) -> R): R? {
    return input?.let(callback)
}

fun Activity.showLoadingDialog(): Dialog {
    val progressDialog = Dialog(this)
    progressDialog.show()
    if (progressDialog.window != null) {
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    progressDialog.setContentView(R.layout.progress_dialog_sdk)
    progressDialog.setCancelable(false)
    progressDialog.setCanceledOnTouchOutside(false)

    return progressDialog
}

//fun Activity?.handleSizeContent() {
//    ifNotNull(this, { activity ->
//        if (this?.window != null) {
//            val orientation: Int = activity.resources.configuration.orientation
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                val deviceWidth = activity.getDeviceWidth()
//                val dp12 = activity.dpFromPx(12F).toInt()
//                val paddingStartEnd = deviceWidth * 10 / 100
//                val color = ColorDrawable(Color.TRANSPARENT)
//                val inset = InsetDrawable(color, paddingStartEnd, dp12, paddingStartEnd, dp12)
//                this.window!!.setBackgroundDrawable(inset)
//                if (activity.resources.getBoolean(R.bool.isTablet)) {
//                    val dp600 = activity.dpFromPx(600F).toInt()
//                    val params = this.window?.attributes
//                    params?.width = WindowManager.LayoutParams.MATCH_PARENT
//                    params?.height = dp600
//                    params?.gravity = Gravity.CENTER
//                    this.window?.attributes = params
//                } else {
//                    val params = this.window?.attributes
//                    params?.width = WindowManager.LayoutParams.MATCH_PARENT
//                    params?.height = WindowManager.LayoutParams.MATCH_PARENT
//                    params?.gravity = Gravity.CENTER
//                    this.window?.attributes = params
//                }
//            } else {
//                if (activity.resources.getBoolean(R.bool.isTablet)) {
//                    val dp16 = activity.dpFromPx(16F).toInt()
//                    val color = ColorDrawable(Color.TRANSPARENT)
//                    val inset = InsetDrawable(color, dp16, dp16, dp16, dp16)
//                    this.window!!.setBackgroundDrawable(inset)
//                } else {
//                    val dp10 = activity.dpFromPx(10F).toInt()
//                    val color = ColorDrawable(Color.TRANSPARENT)
//                    val inset = InsetDrawable(color, dp10, dp10, dp10, dp10)
//                    this.window!!.setBackgroundDrawable(inset)
//                }
//
//                val dp600 = activity.dpFromPx(600F).toInt()
//                val params = this.window?.attributes
//                params?.width = WindowManager.LayoutParams.MATCH_PARENT
//                params?.height = dp600
//                params?.gravity = Gravity.CENTER
//                this.window?.attributes = params
//            }
//        }
//    })
//}

fun getLocalIpAddress(): String {
    try {
        val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf: NetworkInterface = en.nextElement()
            val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                val inetAddress: InetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress) {
                    return inetAddress.hostAddress.toString()
                }
            }
        }
    } catch (ex: Exception) {
        Log.e("IP Address", ex.toString())
        return ""
    }
    return ""
}

//fun Activity.showPopupMessage(
//        handler: Handler,
//        title: String,
//        message: String,
//        textButton: String = "OK",
//        isAutoClose: Boolean = true,
//        callback: () -> Unit
//) {
//    if (isAutoClose) {
//        var timeClose = 3
//        val dialog = AppMessage.ShowMessage.Builder(this)
//                .setTitle(title)
//                .setMessage(message)
//                .setTextConfirm("$textButton ($timeClose)")
//                .setListener(object : AppMessage.EventMessage {
//                    override fun onClickConfirm() {
//                        super.onClickConfirm()
//                        handler.removeCallbacksAndMessages(null)
//                        callback()
//                    }
//                }).buildNotShow()
//        handler.removeCallbacksAndMessages(null)
//        val runnable = object : Runnable {
//            override fun run() {
//                timeClose--
//                if (timeClose == 0) {
//                    handler.removeCallbacksAndMessages(null)
//                    callback()
//                    dialog.closeDialog()
//                    return
//                }
//                dialog.setTextConfirm("$textButton ($timeClose)")
//                handler.postDelayed(this, 1000)
//            }
//        }
//        dialog.show()
//        handler.postDelayed(runnable, 1000)
//    } else {
//        AppMessage.ShowMessage.Builder(this)
//                .setTitle(title)
//                .setMessage(message)
//                .setTextConfirm(textButton)
//                .setListener(object : AppMessage.EventMessage {
//                    override fun onClickConfirm() {
//                        super.onClickConfirm()
//                        handler.removeCallbacksAndMessages(null)
//                        callback()
//                    }
//                }).build()
//    }
//}

fun Context.handleHideShowPassword(textLayout: TextInputLayout, textInput: TextInputEditText) {
    var isHideShow = true
    //val textSizeInSp = textInput.textSize
    textLayout.setEndIconOnClickListener {
        try {
            isHideShow = !isHideShow
            if (isHideShow) {
                textInput.inputType =
                        InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            } else {
                textInput.inputType = InputType.TYPE_CLASS_TEXT
            }
            /*textInput.typeface = Typeface.createFromAsset(this.assets, "font/text_regular.ttf")
            textInput.textSize = textSizeInSp*/
            textInput.setSelection(textInput.length())
        } catch (e: Exception) {
        }
    }
}

fun Context.makeCallPhone(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phone")
    this.startActivity(intent)
}

//fun Context.sendMail(mail: String) {
//    val i = Intent(Intent.ACTION_SENDTO)
//    i.type = "message/rfc822"
//    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_support_account))
//    i.putExtra(Intent.EXTRA_TEXT, "")
//    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    i.data = Uri.parse("mailto:$mail")
//    try {
//        this.startActivity(Intent.createChooser(i, "Send mail..."))
//    } catch (ex: ActivityNotFoundException) {
//    }
//}

fun String.isPhoneNumber(): Boolean {
    return Patterns.PHONE.matcher(this).matches()
}

fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

//fun filterParamsAPI(appKey: String, url: String): Boolean {
//    return if (appKey.isNotEmpty() && url.isNotEmpty()) {
//        true
//    } else {
//        Log.d(ConstantSDK.LOG_TAG_ERROR, "Error: API filter failed")
//        false
//    }
//}

//fun getJsonParser(): Json {
//    return Json {
//        encodeDefaults = true
//        isLenient = true
//        coerceInputValues = true
//        ignoreUnknownKeys = true
//    }
//}

