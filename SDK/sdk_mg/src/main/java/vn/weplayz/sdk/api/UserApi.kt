package vn.weplayz.sdk.api


import com.google.gson.Gson


import org.json.JSONObject
import vn.weplayz.sdk.SDKWeplayZManager
import vn.weplayz.sdk.api.request.PostRequest
import vn.weplayz.sdk.constants.SDKParams
import vn.weplayz.sdk.models.UserAccountModel

/**
 * Create by weed songpq on 13/2/2020.
 */
class UserApi : BaseInteractor() {
    companion object {
        @JvmStatic
        @Synchronized


        fun getUserByAccessToken(
            accessToken: String,
            deviceId : String,
            payment : String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (user: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_ACCESSTOKEN, accessToken)
                        .addField(SDKParams.PARAM_DEVICEID, deviceId)
                        .addField(SDKParams.PARAM_VERIFY_PAYMENT, payment)
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
                    val ee = BaseInteractor.proccessExceptionConnection(e)
                    mainThreadCallback(Runnable { listener.invoke(null, ee) })
                }
            }
            )
        }
    }
}