package vn.mgjsc.sdk.api



import com.google.gson.Gson
import vn.mgjsc.sdk.models.UserAccountModel


import org.json.JSONObject
import vn.mgjsc.sdk.SDKManager
import vn.mgjsc.sdk.api.BaseInteractor

import vn.mgjsc.sdk.api.request.PostRequest
import vn.mgjsc.sdk.constants.Constants
import vn.mgjsc.sdk.constants.SDKParams

/**
 * Create by weed songpq on 26/2/2020.
 */
class AuthenApi: BaseInteractor() {

    companion object {
        @JvmStatic
        @Synchronized
        fun getLoginUser(
            deviceID: String,
            userName: String,
            password: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (user: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_ACCOUNT, userName)
                        .addField(SDKParams.PARAM_PASSWORD, password)
                        .addField(SDKParams.PARAM_CLIENTOS, Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, Constants.MAC_ADDRESS)
                        .addField(SDKParams.PARAM_SDK_VERSION_GAME,SDKManager.getVersionName())
                        .addField(SDKParams.PARAM_SDK_BUILD_GAME,""+SDKManager.getVersionCode())
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

        // PlayNow
        fun getLoginQuickDevice(
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (user: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_CLIENTOS, Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKManager.getPackageName())

                        .addField(SDKParams.PARAM_MAC_ADDRESS, Constants.MAC_ADDRESS)
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

        fun getLoginFacebook(
            facebookID: String,
            facebookAccessToken: String,
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (user: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_FACEBOOKID, facebookID)
                        .addField(SDKParams.PARAM_FACEBOOKACCESSTOKEN, facebookAccessToken)
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_CLIENTOS, Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, Constants.MAC_ADDRESS)
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

        fun getLoginGoogle(
            googleID: String,
            googleTokenID: String,
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (user: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_GOOGLEID, googleID)
                        .addField(SDKParams.PARAM_GOOGLETOKENID, googleTokenID)
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_CLIENTOS, Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, Constants.MAC_ADDRESS)
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





        fun getLogoutByAccessToken(
            accessToken: String,
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
                        .addField(SDKParams.PARAM_APP_KEY, SDKManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_ACCESSTOKEN, accessToken)
                        .addField(SDKParams.PARAM_DEVICEID,deviceID)
                        .addField(SDKParams.PARAM_CLIENTOS,Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, Constants.MAC_ADDRESS)
                        .execute()
                    mainThreadCallback(Runnable {
                        listener.invoke(
                            JSONObject(result).getBoolean("r"), null
                        )
                    })
                } catch (e: Exception) {
                    val ee = BaseInteractor.proccessExceptionConnection(e)
                    mainThreadCallback(Runnable { listener.invoke(null, ee) })
                }
            }
            )
        }

        fun getRegister(
            deviceID: String,
            userName: String,
            password: String,
            primaryMobile: String,
            displayName: String,
            email : String,
            dob : String,
            cardID : String,
            dateCard : String,
            address : String,
            gender: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (user: UserAccountModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_USERNAME, userName)
                        .addField(SDKParams.PARAM_PASSWORD, password)
                        .addField(SDKParams.PARAM_PRIMARY_MOBILE, primaryMobile)
                        .addField(SDKParams.PARAM_DISPLAY_NAME, displayName)
                        .addField(SDKParams.PARAM_EMAIL, email)
                        .addField(SDKParams.PARAM_DOB, dob)
                        .addField(SDKParams.PARAM_CARD_ID, cardID)
                        .addField(SDKParams.PARAM_CARD_DATE_ID, dateCard)
                        .addField(SDKParams.PARAM_ADDRESS, address)
                        .addField(SDKParams.PARAM_GENDER, gender)
                        .addField(SDKParams.PARAM_CLIENTOS, Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_DEVICEID,deviceID)
                        .addField(SDKParams.PARAM_SDK_VERSION,Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, Constants.MAC_ADDRESS)
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