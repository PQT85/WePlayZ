package vn.weplayz.sdk.api


import com.google.gson.Gson
import org.json.JSONObject
import vn.weplayz.sdk.SDKWeplayZManager
import vn.weplayz.sdk.api.request.PostRequest
import vn.weplayz.sdk.constants.SDKParams
import vn.weplayz.sdk.models.UserAccountModel

/**
 * Create by weed songpq on 31/01/2020.
 */
class SynQuickDeviceApi : BaseInteractor() {
    companion object {
        @JvmStatic
        @Synchronized


        fun getSynQuickDevice(
            typeSyn : String,

            deviceID: String,
            userName: String,
            password: String,
            email: String,
            primaryMobile: String,
            displayName: String,
            dob : String,
            cardID : String,
            dateCard : String,
            address : String,
            gender: String,
            accessToken : String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (result: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_USERNAME, userName)
                        .addField(SDKParams.PARAM_PASSWORD, password)

                        .addField(SDKParams.PARAM_TYPE_SYN, typeSyn)
                        .addField(SDKParams.PARAM_EMAIL, email)
                        .addField(SDKParams.PARAM_PRIMARY_MOBILE, primaryMobile)
                        .addField(SDKParams.PARAM_DISPLAY_NAME, displayName)
                        .addField(SDKParams.PARAM_DOB, dob)
                        .addField(SDKParams.PARAM_CARD_ID, cardID)
                        .addField(SDKParams.PARAM_CARD_DATE_ID, dateCard)
                        .addField(SDKParams.PARAM_GENDER, gender)
                        .addField(SDKParams.PARAM_ACCESSTOKEN, accessToken)
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,
                            vn.weplayz.sdk.constants.Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,
                            vn.weplayz.sdk.constants.Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKWeplayZManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, vn.weplayz.sdk.constants.Constants.MAC_ADDRESS)
                        .addField(SDKParams.PARAM_SDK_VERSION_GAME,SDKWeplayZManager.getVersionName())
                        .addField(SDKParams.PARAM_SDK_BUILD_GAME,""+SDKWeplayZManager.getVersionCode())
                        .execute()
                    mainThreadCallback(Runnable {
                        listener.invoke(
                            Gson().fromJson(
                                JSONObject(result).getString("r"),
                                UserAccountModel::class.java
                            ), null
                        )
                    })
                } catch (e: Exception) {
//                    mainThreadCallback(Runnable { listener.invoke(null, e) })
                    e.message?.let{
                        if (!it.contains(" -- statusCode: ",ignoreCase = true))
                        {
                            var message = "Lỗi kết nối mạng.Vui lòng kiểm tra mạng và thử lại";
                            if(vn.weplayz.sdk.constants.Constants.isDebug)
                                message = message + it;
                            BaseInteractor.isHTTPS = false
                            val ee= java.lang.Exception(message)
                            mainThreadCallback(Runnable { listener.invoke(null, ee) })

                        }else{
                            mainThreadCallback(Runnable { listener.invoke(null, e) })
                        }
                    }
                }
            }
            )
        }


        fun getSynQuickDeviceGoogle(
            googleID: String,
            googleTokenID: String,
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (result: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_GOOGLEID, googleID)
                        .addField(SDKParams.PARAM_GOOGLETOKENID,googleTokenID)
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,
                            vn.weplayz.sdk.constants.Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,
                            vn.weplayz.sdk.constants.Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKWeplayZManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, vn.weplayz.sdk.constants.Constants.MAC_ADDRESS)
                        .execute()
                    mainThreadCallback(Runnable {
                        listener.invoke(
                            Gson().fromJson(
                                JSONObject(result).getString("r"),
                                UserAccountModel::class.java
                            ), null
                        )
                    })
                } catch (e: Exception) {
//                    mainThreadCallback(Runnable { listener.invoke(null, e) })
                    e.message?.let{
                        if (!it.contains(" -- statusCode: ",ignoreCase = true))
                        {
                            var message = "Lỗi kết nối mạng.Vui lòng kiểm tra mạng và thử lại";
                            if(vn.weplayz.sdk.constants.Constants.isDebug)
                                message = message + it;
                            BaseInteractor.isHTTPS = false
                            val ee= java.lang.Exception(message)
                            mainThreadCallback(Runnable { listener.invoke(null, ee) })

                        }else{
                            mainThreadCallback(Runnable { listener.invoke(null, e) })
                        }
                    }
                }
            }
            )
        }

        fun getSynQuickDeviceFacebook(
            facebookID: String,
            fbAcessToken: String,
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (result: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_FACEBOOKID, facebookID)
                        .addField(SDKParams.PARAM_FACEBOOKACCESSTOKEN,fbAcessToken)
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,
                            vn.weplayz.sdk.constants.Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,
                            vn.weplayz.sdk.constants.Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKWeplayZManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, vn.weplayz.sdk.constants.Constants.MAC_ADDRESS)
                        .execute()
                    mainThreadCallback(Runnable {
                        listener.invoke(
                            Gson().fromJson(
                                JSONObject(result).getString("r"),
                                UserAccountModel::class.java
                            ), null
                        )
                    })
                } catch (e: Exception) {
                    val ee = BaseInteractor.proccessExceptionConnection(e)
                    mainThreadCallback(Runnable { listener.invoke(null, ee) })
                }
            }
            )
        }
    }
}