package vn.mgjsc.sdk.constants

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import vn.mgjsc.sdk.utils.Device
import java.math.BigInteger
import java.security.MessageDigest
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object Encrypt {
    private const val HMAC_SHA256 = "HmacSHA256"
    fun hashCode256(input: String, key: String): String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), HMAC_SHA256)
        val mac = Mac.getInstance(HMAC_SHA256)
        mac.init(secretKeySpec)
        return toHexString(mac.doFinal(input.toByteArray()))
    }

    private fun toHexString(bytes: ByteArray): String {
        val formatter = Formatter()
        for (b in bytes) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }


    // Config
//    fun getHashCodeConfig(dateTime: String,appKey : String,appSecretKey : String) =
//        hashCode256 (
//            input = "${appKey}${Constants.VERSION_SDK}${dateTime}",
//            key = appSecretKey
//        )
    fun getHashCodeConfig(DeviceID : String ,RequestTime : String,appSecretKey : String) =
        hashCode256 (
//            input = "${appKey}${Constants.VERSION_SDK}${dateTime}",
            input = "${Constants.VERSION_SDK}${DeviceID}${Constants.CLIENT_OS}${RequestTime}",
            key = appSecretKey
        )

    // Login FB: FacebookAccessToken + FacebookID + Time
    fun getHashCodeLoginFB(accessToken: String, userId: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${accessToken}${userId}${dateTime}",
            key = appSecretKey
        )

    // Login GG: (GoogleTokenID + GoogleID + Time)
    fun getHashCodeLoginGG(idToken: String, googleId: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${idToken}${googleId}${dateTime}",
            key = appSecretKey
        )

    // Login PlayNow (DeviceID + ClientOS + Time)
    fun getHashCodeLoginQuickDevice(deviceId: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${deviceId}${dateTime}",
            key = appSecretKey
        )

    // Login (UserName + Password + Time)
    fun getHashCodeLogin(userName: String, password: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${userName}${password}${dateTime}",
            key = appSecretKey
        )

    // GetUser (AccessToken + Time)
    fun getHashCodeGetUser(accessToken: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${accessToken}${dateTime}",
            key = appSecretKey
        )
    fun getHashCodeLogout(accessToken: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${accessToken}${dateTime}",
            key = appSecretKey
        )

    // Register (UserName + Password + Time)
    fun getHashCodeRegister(userName: String, password: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${userName}${password}${dateTime}",
            key = appSecretKey
        )

    // ForgotPassword ( UserName + Time , SecretKey)
    fun getHashCodeForgotPassword(email: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${email}${dateTime}",
            key = appSecretKey
        )

    // Syn Quick Device (UserName + Email + Password + DeviceID+ Time)
    fun getHashCodeSyncAccount(
        userName: String,
        email : String,
        password: String,
        deviceId: String,
        dateTime: String,
        appSecretKey: String
    ) =
        hashCode256(
            input = "${deviceId}${userName}${password}${dateTime}",
            key = appSecretKey
        )
    fun getHashCodeSyncAccountFacebook(
        fbAccessToken: String,
        facebookID: String,
        deviceId: String,
        dateTime: String,
        appSecretKey: String
    ) =
        hashCode256(
            input = "${fbAccessToken}${facebookID}${deviceId}${dateTime}",
            key = appSecretKey
        )
    fun getHashCodeSyncAccountGoogle(
        googleTokenID: String,
        googleID: String,
        deviceId: String,
        dateTime: String,
        appSecretKey: String
    ) =
        hashCode256(
            input = "${googleTokenID}${googleID}${deviceId}${dateTime}",
            key = appSecretKey
        )

    // Payment
    fun getHashCodeGetListDefinePackage(deviceId: String, dateTime: String,appSecretKey: String) =
        hashCode256(
            input = "${deviceId}${Constants.CLIENT_OS}${dateTime}",
            key = appSecretKey
        )

    fun getHashCodeGetCreateTrans(
        accessToken: String,
        packageId: String,
        deviceId: String,
        dateTime: String,
        appSecretKey: String
    ) =
        hashCode256(
            input = "${accessToken}${packageId}${deviceId}${dateTime}",
            key = appSecretKey
        )

    fun getHashCodeGetChargeToGame(
        accessToken: String,
        transactionID : String,
        orderId: String,
        orderIDIAP: String,
        productId: String,
        dateTime: String,
        appSecretKey: String
    ) =
        hashCode256(
            input = "${accessToken}${transactionID}${orderId}${orderIDIAP}${productId}${dateTime}",
            key = appSecretKey
        )
    fun getHashString(data : String,appSecretKey: String)
            = hashCode256(input = data,key = appSecretKey)

    fun md5(data : String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(data.toByteArray())).toString(16).padStart(32, '0')
    }
}


object SDKParams {
    const val PARAM_ACCOUNT = "Account"
    const val PARAM_USERNAME = "UserName"
    const val PARAM_USERID = "UserID"
    const val PARAM_SKIP_ADS = "SkipAds"
    const val PARAM_APP_KEY = "AppKey"
    const val PARAM_EMAIL = "Email"
    const val PARAME_ZONEID = "ZoneID"
    const val PARAM_PASSWORD= "Password"
    const val PARAM_DEVICEID = "DeviceID"
    const val PARAM_USE_COIN = "UseCoin"
    const val PARAM_VERIFY_PAYMENT = "Payment"
    const val PARAM_CLIENTOS = "ClientOS"
    const val PARAM_TIME = "RequestTime"
    const val PARAM_SIGN = "Sign"
    const val PARAM_ACCESSTOKEN = "AccessToken"
    const val PARAM_TYPE_SYN= "AccountType"
    const val PARAM_FACEBOOKACCESSTOKEN = "FacebookAccessToken"
    const val PARAM_GOOGLETOKENID = "GoogleTokenID"
    const val PARAM_GOOGLEID= "GoogleID"
    const val PARAM_FACEBOOKID = "FacebookID"
    const val PARAM_ENVIROMENT = "Enviroment"
    const val PARAM_PACKAGENAME = "PackageName"
    const val PARAM_PACKAGE_IAP = "Package"
    const val PARAM_CLIENT_IP = "ClientIP"
    const val PARAM_MAC_ADDRESS = "MacAddress"
    const val PARAM_SDK_VERSION = "VersionSDK"
    const val PARAM_SDK_VERSION_GAME = "VersionGame"
    const val PARAM_SDK_BUILD_GAME = "BuildGame"

    const val PARAM_PRIMARY_MOBILE = "PrimaryMobile"
    const val PARAM_DISPLAY_NAME = "DisplayName"
    const val PARAM_GENDER = "Gender"
    const val PARAM_PACKAGE_ID = "Package"
    const val PARAM_SERVERID = "ServerID"

    const val PARAM_ROLE_ID = "RoleID"
    const val PARAM_ROLE_NAME = "RoleName"
    const val PARAM_OTHER_DATA = "Other"

    const val PARAM_DATA_TRACK_IAP = "Data"

    const val PARAM_DOB = "BirthDay"
    const val PARAM_CARD_ID = "CardID"
    const val PARAM_ADDRESS = "Address"
    const val PARAM_CARD_DATE_ID = "DateCard"

    const val PARAM_TRANSACTION_ID = "TransactionID"
    const val PARAM_ORDER_ID_IAP = "OrderIDIAP"
    const val PARAM_ORDER_ID = "OrderID"
    const val PARAM_PRODUCT_ID = "ProductID"
    const val PARAM_STORE_PRODUCT_ID = "StoreProductID"
    const val PARAM_SIGNATURE_IAP = "SignatureIAP"
    const val PARAM_DATA_RECEIPT = "DataReceipt"

    var IPLOCAL = Device.getIPAddress(true)


    private const val TIME_DATE_FORMAT = "yyyyMMddHHmmss"


    fun invalidDate(date: String): Date? {
        val df = SimpleDateFormat("dd/MM/yyyy")
        df.isLenient = false
        try {
            return df.parse(date)

        } catch (e: ParseException) {
            return null
        }

    }


    fun getVersionName(context: Context?) : String {
        try {
            val packageManager = context!!.packageManager;
            val packageName = context!!.packageName;
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0);
            }
            return packageInfo.versionName;

        }catch (io : Exception)
        {

        }
        return "";
    }
    fun getVersionCode(context: Context?): Long {
      /*  try {
            val manager: PackageManager = context!!.getPackageManager()
            val info: PackageInfo =
                manager.getPackageInfo(context!!.getPackageName(), PackageManager.GET_ACTIVITIES)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.getLongVersionCode() else info.versionCode.toLong()
        } catch (e: Exception) {
        }*/

        try{
            val packageManager = context!!.packageManager;
            val packageName = context!!.packageName;
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU )
            {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            }else
            {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName,0);
            }
            val versionName = packageInfo.versionName;
            val versionCode = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            {
                packageInfo.longVersionCode;
            }else
                 packageInfo.versionCode
            return versionCode.toLong();
        }catch (e: Exception)
        {

        }

        return 0;

        //return 0
//        Toast.makeText(this,
//                "PackageName = " + info.packageName + "\nVersionCode = "
//                        + info.versionCode + "\nVersionName = "
//                        + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show();
    }
    fun getCurrentTime(): String {
        if(Build.VERSION.SDK_INT < 26)
            return SimpleDateFormat(TIME_DATE_FORMAT).format(Date())
        else
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIME_DATE_FORMAT))
    }
    fun formatTimeToString(date: Date): String {
        if(Build.VERSION.SDK_INT < 26)
            return SimpleDateFormat(TIME_DATE_FORMAT).format(date.time)
        return ""
    }
}