package vn.weplayz.sdk.api


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

//import vn.mgjsc.sdk.models.AdsMiGame

import org.json.JSONObject
import vn.weplayz.sdk.SDKWeplayZManager
import vn.weplayz.sdk.api.request.PostRequest
import vn.weplayz.sdk.constants.SDKParams
import vn.weplayz.sdk.models.BannerMiGame
import vn.weplayz.sdk.models.BaseConfigsDataModel


/**
 * Create by weed songpq on 20/11/2019.
 */
class ConfigApi : BaseInteractor() {
    companion object {

        @JvmStatic
        @Synchronized
        fun getBanner(
            versionCode : Long,
            zoneID : String,
            skipBanner : String,
            userID: String="",
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,

            listener: (ads: ArrayList<BannerMiGame>?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_USERID, userID)
                        .addField(SDKParams.PARAM_SDK_VERSION_GAME,versionCode.toString())
                        .addField(SDKParams.PARAME_ZONEID, zoneID)
                        .addField(SDKParams.PARAM_SKIP_ADS, skipBanner)
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
                        .execute()

                    mainThreadCallback(Runnable {
                        listener.invoke(
                            Gson().fromJson(
                                JSONObject(result).getString("r"),
                                object : TypeToken<ArrayList<BannerMiGame>>() {}.type
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


        @JvmStatic
        @Synchronized
        fun getConfig(
            deviceID: String,
            time: String,
            sign: String,
            listener: (config: BaseConfigsDataModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/config", "UTF-8")
//                    val result = PostRequest("${getDomainAPI()}/loadconfig", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_DEVICEID , deviceID)
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
                                BaseConfigsDataModel::class.java
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