package vn.weplayz.sdk.api


import org.json.JSONObject
import vn.weplayz.sdk.SDKWeplayZManager
import vn.weplayz.sdk.api.request.PostRequest
import vn.weplayz.sdk.constants.SDKParams

/**
 * Create by weed songpq on 22/11/2019.
 */
class ForgotPasswordApi : BaseInteractor() {
    companion object {
        @JvmStatic
        @Synchronized


        fun getForgotPassword(
            email: String,
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,

            listener: (result: Boolean?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_ACCOUNT, email)
                        .addField(SDKParams.PARAM_USERNAME, email)
                        .addField(SDKParams.PARAM_DEVICEID,deviceID)
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
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
                        JSONObject(result).getString("r")?.toBoolean()?.let {
                            listener.invoke(
                                it, null
                            )
                        }
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