package vn.weplayz.sdk

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import com.myapplication.MainActivity
import com.facebook.FacebookSdk

import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vn.weplayz.sdk.api.PaymentApi
import vn.weplayz.sdk.constants.Encrypt
import vn.weplayz.sdk.constants.SDKParams
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import vn.weplayz.sdk.models.*
import vn.weplayz.sdk.utils.*

public class SDKWeplayZManager {


    interface AuthUserInterface {
        fun onUserLoginSuccess(userAccountModel: UserAccountModel)
        fun onUserLoginFail(e: String?)
        fun onUserLogoutSuccess()
        fun onUserLogoutFail(e : String?)
        fun onUserCancel()

        fun onUserSyncSuccess(userAccountModel: UserAccountModel)
        fun onUserSyncCancel(userAccountModel: UserAccountModel)

    }

    interface PaymentUserCallback {
        fun onUserPaymentSuccess(chargeToGameResult: PaymentDataGameModel?)
        fun onUserPaymentFail(e: String?)
        fun onUserCancel()
    }

    @Keep
    companion object {
        @JvmStatic
        public val PREFERENCE_ACCOUNT_MANAGER = "vn.mgjsc.sdk.loginManager"

        @JvmStatic
        public val PREFERENCE_FIRST_REQUEST_PERMISSION = "vn.mgjsc.sdk.firstRequestPermission"

        private val KEY_SHARED_PREFERENCES_SKIP_ADS = "vn.mgjsc.sdk.skipAdses"

        @JvmStatic
        public val KEY_SHARED_PREFERENCES_INFO_IAP = "vn.mgjsc.sdk.infoIAP"

        @JvmStatic
        public val KEY_SHARED_PREFERENCES_CONFIGS = "vn.mgjsc.sdk.configs"

        @JvmStatic
       // private var APP_KEY = "cvtq09754b9218cc9f9d6e9192be85f514cdK"
        private var APP_KEY = "animehero3c85030ae1b963fd180e55674de07614AK"
        @JvmStatic
      //  private var SECRET_KEY = "cvtq5f4feb3e6070a615c7ef49f801823d85S"
        private var SECRET_KEY = "animeheroe644ede84ec176e3924f3c020af7aa7cSK"
        @JvmStatic
        private var AFF_KEY = ""

        @JvmStatic
        private var TIKTOK_REDIRECT_URL = "https://nicasdk-2e394.web.app/"
        //private var TIKTOK_REDIRECT_URL = "https://tiktokcallback/mg19/"
        //private var TIKTOK_REDIRECT_URL = "weplayz19://tiktokcallback/"

        @JvmStatic
        private var TIKTOK_KEY = "sbaw895plmvn26rgjk"

        @JvmStatic
        private var TIKTOK_CLIENT_KEY = "123131"

        @JvmStatic
        private var GG_KEY = "652476991377-0i3go1odbbbb5dhg40hgcq0kg51r97d3.apps.googleusercontent.com"

        @JvmStatic
        private var ADJUST_KEY = "";
        private var bannerCallback : BannerCallback? = null

        @JvmStatic
        private val KEY_SHARED_PREFERENCES_USER = "vn.mgjsc.sdk.user"

        @JvmStatic
        private var APPLICATION : Application? = null

        @JvmStatic
        private val gson: Gson = Gson()

        @JvmStatic
        private var INITSDK : Boolean = false

        @JvmStatic
        private val KEY_SHARED_PREFERENCES_LAST_PAYMENT_ACTION = "last_time_payment"

        @JvmStatic
        @Synchronized
        fun getAPP_KEY() : String
        {
            return APP_KEY
        }
        @JvmStatic
        @Synchronized
        fun getSECRET_KEY() : String
        {
            return SECRET_KEY
        }
        @JvmStatic
        @Synchronized
        fun getGG_KEY() : String
        {
            return GG_KEY
        }

        @JvmStatic
        @Synchronized
        fun getTIKTOK_REDIRECT_URL() :String
        {
            return TIKTOK_REDIRECT_URL;
        }
        @JvmStatic
        @Synchronized
        fun getTIKTOK_KEY():String {
            return TIKTOK_KEY;
        }

        @JvmStatic
        @Synchronized
        fun getTIKTOK_CLIENT_KEY():String {
            return TIKTOK_CLIENT_KEY;
        }
        @JvmStatic
        @Synchronized
        fun getAFF_KEY() : String
        {
            return AFF_KEY
        }

        @JvmStatic
        fun getPackageName() : String
        {
            var pk = "unknown"
            APPLICATION?.let {
                pk = APPLICATION!!.packageName
            }
            return pk
        }
        @JvmStatic
        fun getVersionName() : String {
            APPLICATION?.let {
                return SDKParams.getVersionName(APPLICATION?.applicationContext)
            }
            return "";
        }
        @JvmStatic
        fun getVersionCode(): Long {
           APPLICATION?.let { return SDKParams.getVersionCode(APPLICATION?.applicationContext) }
            return 0;

        }

        @JvmStatic
        var isTracking = false
        @JvmStatic
        @Synchronized
        fun saveInfoIAP(data: JSONArray?,context:Context) {
            val sharedPreferences:SharedPreferences = context.getSharedPreferences(PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            if(data!=null && sharedPreferences!= null ) {
                val dataStr = data.toString()
                sharedPreferences!!.edit()!!.putString(KEY_SHARED_PREFERENCES_INFO_IAP,dataStr).apply()
            }
            else
            {

                sharedPreferences!!.edit()!!.putString(KEY_SHARED_PREFERENCES_INFO_IAP, JSONArray().toString()).apply()
            }
        }
        @JvmStatic
        @Synchronized

        fun getUser(context: Context?): UserAccountModel? {
            val sharedPreferences:SharedPreferences = context!!.getSharedPreferences(PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            if(sharedPreferences!=null) {
                val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_USER, "")
                return gson.fromJson(
                    data,
                    UserAccountModel::class.java
                )
            }
            return null
        }

        fun saveBannerSkip(context: Context?,idAds : String,isSkip : Boolean = true):Boolean
        {
            val sharedPreferences:SharedPreferences = context!!.getSharedPreferences(PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            if(sharedPreferences!=null) {
                val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_SKIP_ADS, "")
                var json : JSONObject = JSONObject()
                if(!TextUtils.isEmpty(data))
                {
                    json = JSONObject(data);
                }
                if(isSkip) {
                    json.put((idAds), isSkip)
                    sharedPreferences!!.edit()!!
                        .putString(KEY_SHARED_PREFERENCES_SKIP_ADS, json.toString()).apply()
                    return true;
                }
                else
                {
                    if(json.has(idAds))
                    {
                        json.remove(idAds);
                        sharedPreferences!!.edit()!!
                            .putString(KEY_SHARED_PREFERENCES_SKIP_ADS, json.toString()).apply()
                    }
                    return false;
                }

            }
            return false;
        }

        fun savePreviousConfig(context: Context?,config : BaseConfigsDataModel?)
        {
            val sharedPreferences:SharedPreferences = context!!.getSharedPreferences(PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
//        MiGameSDK.getApplicationContext()?.let {
//            val runnable = Runnable { MiGameSDK.trackerIAP(MiGameSDK.getApplicationContext()!!) }
//            val thread:Thread = Thread(runnable)
//            thread.start()
//
//        }


            if(config!=null && sharedPreferences!= null ) {
                val dataStr = gson.toJson(config!!)
                sharedPreferences!!.edit()!!.putString(SDKWeplayZManager.KEY_SHARED_PREFERENCES_CONFIGS,dataStr).apply()
            }
            else
            {
                sharedPreferences!!.edit()!!.putString(SDKWeplayZManager.KEY_SHARED_PREFERENCES_CONFIGS,"").apply()
            }


        }
        fun getBannerSkip(context:Context?): JSONObject {
            val sharedPreferences:SharedPreferences = context!!.getSharedPreferences(PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            if(sharedPreferences!=null) {
                val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_SKIP_ADS, "")
                if(!TextUtils.isEmpty(data))
                {
                    return JSONObject(data)
                }
            }
            return JSONObject()
        }
        interface BannerCallback {
            fun onShow(bannerMiGame: BannerMiGame?)
            fun onFailed(e: String?)
            fun onClose()
        }

        fun setBannerCallback(banerCallback: BannerCallback) {
            this.bannerCallback = bannerCallback
        }
    fun getBannerCallback():BannerCallback? {
        return this.bannerCallback
    }

        @JvmStatic
        @Synchronized
        public fun showBanner(activity:Activity ,zoneID : String, isBannerConfig: Boolean = false)
        {
            if(isBannerConfig==true) {
                //if (AccountManager.getInstance().configs != null && AccountManager.getInstance().configs!!.adsMigame!=null) {
                    if(baseConfigModel != null && baseConfigModel!!.bannerMigame != null){
                    updateConfigBanner(activity.applicationContext);
                    //if(AccountManager.getInstance().configs!!.adsMigame != null && AccountManager.getInstance().configs!!.adsMigame!!.size>0)
                        if(baseConfigModel!!.bannerMigame != null && baseConfigModel!!.bannerMigame!!.size > 0) {
                            val intent: Intent = Intent(activity, vn.weplayz.sdk.BannerMiGameActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra("isAdsConfig", isBannerConfig)
                            intent.putExtra("ZoneID", zoneID)
                            activity.startActivity(intent)
                        }
                    else
                        getBannerCallback()?.onFailed(activity.getString(R.string.mg_text_error_banner_config))
                }
                else
                {
                    getBannerCallback()?.onFailed(activity.getString(R.string.mg_text_error_banner_config))
                }
            }else
            {

                //if(AccountManager.getInstance().configs != null && AccountManager.getInstance().configs!!.adsMigame!=null)
                if(baseConfigModel != null && baseConfigModel!!.bannerMigame != null)
                {
                  //  if(AccountManager.getInstance().configs!!.SDKShowConfig.IsShowAds==0)
                    if(baseConfigModel!!.SDKShowConfig.isShowAccount == 0)
                    {
                        //AccountManager.getInstance().getAdsCallback()?.onFailed("Hidden ads")
                        getBannerCallback()?.onFailed("Hide Banner")
                        return;
                    }
                }

                val intent : Intent = Intent(activity, vn.weplayz.sdk.BannerMiGameActivity::class.java)

                intent.flags=Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("isAdsConfig",isBannerConfig)
                intent.putExtra("ZoneID",zoneID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent)
            }
        }
        public fun removeSkipBanner(context: Context?,listBanner : ArrayList<BannerMiGame>):ArrayList<BannerMiGame> {
            var newBanner = ArrayList<BannerMiGame>()
            var jsonBanner = SDKWeplayZManager.getBannerSkip(context!!)
                //AccountManager.getInstance().getAdsSkip()
            var i: Int = 0
            while (i < listBanner.size) {
                var bannerMigame: BannerMiGame = listBanner.get(i)

                if ((bannerMigame.isForceShow == 0 && jsonBanner.optBoolean(bannerMigame.idBanner.toString()) == true)) {

                } else {
                    newBanner.add(bannerMigame)
                }
                i++;

            }
            return newBanner
        }

   public fun updateConfigBanner(context: Context?) {
       //if (AccountManager.getInstance().configs == null || AccountManager.getInstance().configs!!.adsMigame == null) {
        //   return
      // }
       if(baseConfigModel == null || baseConfigModel!!.bannerMigame == null)
           return;
       var jsonAds = getBannerSkip(context);
       //var jsonAds = AccountManager.getInstance().getAdsSkip()
       var i: Int = 0
     //  while (i < AccountManager.getInstance().configs!!.adsMigame!!.size) {
       while(i < baseConfigModel!!.bannerMigame!!.size){

           var adsMigame: BannerMiGame? = baseConfigModel!!.bannerMigame?.get(i)

           if (adsMigame == null || (adsMigame!!.isForceShow == 0 && jsonAds.optBoolean(adsMigame!!.idBanner.toString()) == true)) {
               baseConfigModel!!.bannerMigame!!.removeAt(i)
           } else
               i++;

       }

   }
        private fun getInfoIAP(context:Context) : JSONArray
        {
            val sharedPreferences:SharedPreferences = context.getSharedPreferences(PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            if(sharedPreferences!=null) {
                val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_INFO_IAP, "")
                try {
                    var info : JSONArray = JSONArray(data)
                    return info
                }catch (e: JSONException)
                {
                    return JSONArray()
                }

            }
            return JSONArray()
        }
        fun getPreviousConfigs(context:Context) : BaseConfigsDataModel? {
            val sharedPreferences:SharedPreferences = context.getSharedPreferences(PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            if(sharedPreferences!=null) {
                val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_CONFIGS, "")

                return Gson().fromJson(
                    data,
                    BaseConfigsDataModel::class.java
                )
            }
            return null
        }

        @JvmStatic
        @Synchronized
        public fun trackerIAP(context : Context) {




            var linkAPI = ""
            SDKWeplayZManager.baseConfigModel?.let {
                linkAPI = it.URL_IAP_Track
//                if (!linkAPI.startsWith("http"))
//                    linkAPI = it.domainApi + "/" + linkAPI
            }
            if (linkAPI.isNullOrEmpty())
            {
                val config = getPreviousConfigs(context)
                config?.let {
                    linkAPI = config.URL_IAP_Track
//                    if (!linkAPI.startsWith("http"))
//                        linkAPI = it.domainApi + "/"+linkAPI
                }
            }
            if (linkAPI.isNullOrEmpty())
                return
            if(isTracking)
                return
            val data = getInfoIAP(context)
            if(data == null || data.length() == 0)
                return

//        val present = BasePresenter()

            if(NetworkUtils.isNetworkConnected(context!!)) {
//                showLoading()

                val dateTime = SDKParams.getCurrentTime()
                val deviceId = Device.getDeviceID(context!!)
                val clientOS = vn.weplayz.sdk.constants.Constants.CLIENT_OS
                val clientIP = Device.getIPAddress(true)
                isTracking = true
                var sign = Encrypt.getHashString("${deviceId}${clientOS}${dateTime}",SDKWeplayZManager.getSECRET_KEY())
                PaymentApi.trackerIAP(
                    data.toString(),
                    deviceId,
                    clientOS,
                    clientIP,
                    linkAPI,
                    dateTime,
                    sign
                ) { config, e ->


                    if (config != null) {

                        saveInfoIAP(null,context)
                    } else {
                    }
                }
            }else
            {
            }

        }

        @JvmStatic
        @Synchronized
        fun initSDK(application : Application, appKey : String? , secretKey : String? ,ggKey : String?, affKey : String?, adjustKey: String?) {
            this.APPLICATION = application
            if(appKey != null)
                this.APP_KEY = appKey
            if(secretKey != null)
                this.SECRET_KEY = secretKey
            if(ggKey != null)
                this.GG_KEY = ggKey
            if(affKey != null)
                this.AFF_KEY = affKey
            if(adjustKey != null)
                this.ADJUST_KEY = adjustKey
            //if()
            vn.weplayz.sdk.constants.Constants.trustAllCertificates()
            // PQT initalize firebase
            FirebaseApp.initializeApp(application.applicationContext)
            // init FB
            FacebookSdk.InitializeCallback {
                vn.weplayz.sdk.constants.Constants.showDataLog(vn.weplayz.sdk.constants.Constants.LOG_TAG, "Init facebook ok!!!")
            }
            FacebookSdk.setAutoInitEnabled(true)
            FacebookSdk.setAutoLogAppEventsEnabled(true)
            // init tracking
            vn.weplayz.sdk.utils.TrackingManager.init(application.applicationContext)
            registerEndPoint()
            INITSDK = true

            // reset payment trigger
            val sharedPreference = application!!.getSharedPreferences(SDKWeplayZManager.PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            sharedPreference.edit().putLong(KEY_SHARED_PREFERENCES_LAST_PAYMENT_ACTION,0).apply()

            initAdjustSDK(application.applicationContext)

        }

        @JvmStatic
        @Synchronized
        fun initAdjustSDK(context:Context)
        {
          //  if(!TextUtils.isEmpty(key))
            //    this.ADJUST_KEY = key;
            var json = JSONObject();
            isAdjustValid = true;
            if(baseConfigModel == null || baseConfigModel!!.adjustConfig == null) {

                try {
                    var assetManager = context.assets.open(vn.weplayz.sdk.constants.Constants.ADJUST_KEY_CONFIG);
                    var bufferReader = assetManager.bufferedReader(Charsets.UTF_8);


                    bufferReader.use { lines ->
                        lines.forEachLine { line ->

                            var data = line.split(":")
                            json.put(data[0], data[1])

                        }
                    }
                    //isAdjustValid = true;
                } catch (e: Exception) {
                    isAdjustValid = false;
                }
                if(isAdjustValid) {
                    adjustConfig = Gson().fromJson(json.toString(), MigaAdjustConfig::class.java);
                    if(baseConfigModel != null)
                        baseConfigModel!!.adjustConfig = adjustConfig!!;
                }
                else
                    vn.weplayz.sdk.constants.Constants.showDataLog(vn.weplayz.sdk.constants.Constants.LOG_TAG,"missing Adjust Key Config file");
            }else if(baseConfigModel != null && baseConfigModel!!.adjustConfig != null)
                adjustConfig = baseConfigModel!!.adjustConfig;

            if(adjustConfig != null && !TextUtils.isEmpty(adjustConfig!!.AdjustAppToken))
                this.ADJUST_KEY = adjustConfig!!.AdjustAppToken

            if(!TextUtils.isEmpty(this.ADJUST_KEY))
            {
                val appToken = this.ADJUST_KEY;
                val environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
                val config = AdjustConfig(context, appToken, environment);
                Adjust.initSdk(config);
            }else
            {
               // Toast.makeText(context,"Missing adjust key param from SDK int",2000);
                isAdjustValid = false;
                vn.weplayz.sdk.constants.Constants.showDataLog(vn.weplayz.sdk.constants.Constants.LOG_TAG,"missing adjust key param from SDK init");
            }
            if(!isAdjustValid)
            {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "<!!!!> Adjust SDK isn't initialized properly. <!!!!> ", Toast.LENGTH_SHORT).show()
                }
            }
            vn.weplayz.sdk.utils.TrackingManager.mappingAdjustKey();
          //  if(baseConfigModel != null)
            //    Log.d("PQT Debug-----","config::" + baseConfigModel!!.toString())
            //Log.d("PQT Debug-----","Adjust config::" + SDKManager.adjustConfig.toString())
            //Log.d("PQT Debug --- ","adjust token" + SDKManager.getADJUST_KEY())
        }

        @JvmStatic
        @Synchronized
        fun  requestPermissionForPN(activity: Activity) {

            //if(ContextCompat.checkSelfPermission(activity,android.Manifest.permission.R))
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                  //Log.d("PQT Debug","request PN");
                  if (ContextCompat.checkSelfPermission(
                                  activity,
                                  android.Manifest.permission.POST_NOTIFICATIONS
                          ) != PackageManager.PERMISSION_GRANTED
                  ) {
                      /*val sharedPreferences: SharedPreferences = activity.getSharedPreferences(
                              PREFERENCE_FIRST_REQUEST_PERMISSION,
                              Context.MODE_PRIVATE
                      )
                      val isFirstRequestPermissionPN =
                              sharedPreferences!!.getBoolean(PREFERENCE_FIRST_REQUEST_PERMISSION, true);

                    //  Log.d("PQT Debug", "is First Request::" + isFirstRequestPermissionPN);
                      if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                      activity,
                                      android.Manifest.permission.READ_EXTERNAL_STORAGE
                              ) && !isFirstRequestPermissionPN
                      ) {


                          // Log.i("PQT Debug", "-----------show dialog");
                          val str_title: String = activity.getString(R.string.mg_text_request_push_permission_title);
                          val str_content: String = activity.getString(R.string.mg_text_request_push_permision_content);
                          val str_cancel: String = activity.getString(R.string.mg_cancel_request);
                          val str_accept: String = activity.getString(R.string.mg_accept_request);
                          val dialog: AlertDialog.Builder = AlertDialog.Builder(activity);
                          dialog.setTitle(str_title).setCancelable(false)
                                  .setMessage(str_content);
                          dialog.setNegativeButton(
                                  str_cancel,
                                  DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() });

                          dialog.setPositiveButton(
                                  str_accept
                                  ,
                                  DialogInterface.OnClickListener { dialog, which ->
                                      dialog.dismiss()
                                      val intent: Intent =
                                              Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                      val uri: Uri = Uri.fromParts("package", getPackageName(), null);
                                      intent.setData(uri);
                                      activity.startActivity(intent);
                                  });
                          dialog.show();
                      } else {
                          sharedPreferences!!.edit().putBoolean(PREFERENCE_FIRST_REQUEST_PERMISSION, false).commit();
                          ActivityCompat.requestPermissions(
                                  activity,
                                  arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                  Constants.REQUEST_CODE_PUSH_PERMISSION
                          );*/

                      ActivityCompat.requestPermissions(
                              activity,
                              arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                              vn.weplayz.sdk.constants.Constants.REQUEST_CODE_PUSH_PERMISSION);
                          //ActivityResultLauncher.

                  }
              }
        }
        // register endpoint for PN
        fun registerEndPoint()
        {
            Thread(Runnable { FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                //  val token = task.result

                // Log and toast
                // val msg = getString(R.string.msg_token_fmt, token)
                // Log.d(TAG, msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }) }).start()


        }
        @JvmStatic
        var isAdjustValid: Boolean = true;

        @JvmStatic
        var baseConfigModel: BaseConfigsDataModel? = null


        @JvmStatic
        var authUserCallback: AuthUserInterface? = null


        @JvmStatic
        var adjustConfig: MigaAdjustConfig? = null;

        @JvmStatic
        @Synchronized
        fun login(context : Context, authUserCallback: AuthUserInterface) {
            if(INITSDK==false)
            {
                throw Exception("You must init SDK!!!!");
            }
            this.authUserCallback = authUserCallback
            var intent = Intent(context , MGActivity::class.java)
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_ACTION,
                vn.weplayz.sdk.constants.Constants.ACTION_LOGIN)
            context.startActivity(intent)
        }


        @JvmStatic
        var paymentUserCallback: PaymentUserCallback? = null

//        @JvmStatic
//        @Synchronized
//        fun login(context : Context, authUserCallback: AuthUserInterface) {
//            this.authUserCallback = authUserCallback
//            var intent = Intent(context , MGActivity::class.java)
//            intent.putExtra(Constants.KEY_ACTION,Constants.ACTION_LOGIN)
//            context.startActivity(intent)
//        }

        @JvmStatic
        @Synchronized
        fun logout(context : Context, authUserCallback: AuthUserInterface, showLogin: Boolean =false) {
            this.authUserCallback = authUserCallback
            var intent = Intent(context , MGActivity::class.java)
            var temp = vn.weplayz.sdk.constants.Constants.ACTION_LOGOUT;
            if(!showLogin)
                temp = vn.weplayz.sdk.constants.Constants.ACTION_LOGOUT_ONLY;
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_ACTION,
                temp)
            context.startActivity(intent)
        }

        @JvmStatic
        @Synchronized
        fun payment(context : Context,packageId : String,orderID:String,serverID:String,roleID:String?="",roleName:String?="",otherData:String?="", paymentUserCallback: PaymentUserCallback) {

            val sharedPreference = context!!.getSharedPreferences(SDKWeplayZManager.PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)
            var lastTimePayment = sharedPreference.getLong(KEY_SHARED_PREFERENCES_LAST_PAYMENT_ACTION,0);
            vn.weplayz.sdk.constants.Constants.showDataLog("MG_SDK","enter payment");
            if(SystemClock.elapsedRealtime() - lastTimePayment < 2000 )
            {
                vn.weplayz.sdk.constants.Constants.showDataLog("MG_SDK","skip payment due to 2 clicks too nearly");
                return;
            }

            sharedPreference.edit().putLong(KEY_SHARED_PREFERENCES_LAST_PAYMENT_ACTION,SystemClock.elapsedRealtime()).apply()

            this.paymentUserCallback = paymentUserCallback
            var intent = Intent(context , MGActivity::class.java)
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_ACTION,
                vn.weplayz.sdk.constants.Constants.ACTION_PAYMENT)

            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_DATA_PACKAGE_ID,
                packageId)
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_DATA_ORDER_ID,
                orderID)
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_DATA_SERVER_ID,
                serverID)
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_DATA_ROLE_ID,
                roleID)
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_DATA_ROLE_NAME,
                roleName)
            intent.putExtra(
                vn.weplayz.sdk.constants.Constants.KEY_DATA_OTHER_DATA,
                otherData)

            context.startActivity(intent)

            val js = JSONObject()
            js.put(vn.weplayz.sdk.constants.Constants.KEY_DATA_PACKAGE_ID,packageId)
            js.put(vn.weplayz.sdk.constants.Constants.KEY_DATA_ORDER_ID,orderID)
            js.put(vn.weplayz.sdk.constants.Constants.KEY_DATA_SERVER_ID,serverID)
            js.put(vn.weplayz.sdk.constants.Constants.KEY_DATA_ROLE_ID,roleID)
            js.put(vn.weplayz.sdk.constants.Constants.KEY_DATA_ROLE_NAME,roleName)
            js.put(vn.weplayz.sdk.constants.Constants.KEY_DATA_OTHER_DATA,otherData)
            vn.weplayz.sdk.utils.TrackingManager.trackEventCount(

                context?._getString(R.string.mg_event_click_payment),
                js
            )

        }

        @JvmStatic
        @Synchronized
        fun setIsDebug(isDebug:Boolean)
        {
            vn.weplayz.sdk.constants.Constants.isDebug = isDebug

            if(isDebug)
            {
                vn.weplayz.sdk.constants.Constants.showDataLog(vn.weplayz.sdk.constants.Constants.LOG_TAG, getAPP_KEY())
                vn.weplayz.sdk.constants.Constants.showDataLog(vn.weplayz.sdk.constants.Constants.LOG_TAG, getSECRET_KEY())
                if(APPLICATION != null) {
                    vn.weplayz.sdk.constants.Constants.showDataLog(
                        vn.weplayz.sdk.constants.Constants.LOG_TAG,
                        APPLICATION?.getString(R.string.facebook_app_id)
                    )
                    vn.weplayz.sdk.constants.Constants.showDataLog(
                        vn.weplayz.sdk.constants.Constants.LOG_TAG,
                        APPLICATION?.getString(R.string.facebook_client_token)
                    )
                    vn.weplayz.sdk.constants.Constants.showDataLog(
                        vn.weplayz.sdk.constants.Constants.LOG_TAG, FacebookSdk.getApplicationSignature(
                            APPLICATION
                        )
                    )
                    vn.weplayz.sdk.constants.Constants.getHashKey(APPLICATION)
                    vn.weplayz.sdk.constants.Constants.getSHA(APPLICATION,"SHA1")
                    vn.weplayz.sdk.constants.Constants.getSHA(APPLICATION,"SHA256")
                }
            }
        }

    }
}