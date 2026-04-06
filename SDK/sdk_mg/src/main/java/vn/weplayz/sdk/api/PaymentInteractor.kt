package vn.weplayz.sdk.api


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import vn.weplayz.sdk.SDKWeplayZManager
import vn.weplayz.sdk.api.request.PostRequest
import vn.weplayz.sdk.constants.SDKParams
import vn.weplayz.sdk.models.*

/**
 * Create by weed songpq on 31/01/2020.
 */
class PaymentApi: BaseInteractor() {
    companion object {
        @JvmStatic
        @Synchronized


        fun getListDefinePackage(
            accessToken: String,
            deviceID: String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (result: List<Package>?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_ACCESSTOKEN, accessToken)
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,
                            vn.weplayz.sdk.constants.Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,
                            vn.weplayz.sdk.constants.Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKWeplayZManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, vn.weplayz.sdk.constants.Constants.MAC_ADDRESS)
                        .execute()
                    mainThreadCallback(Runnable {
                        listener.invoke(
                            Gson().fromJson(
                                JSONObject(result).getString("r"),
                                object : TypeToken<List<Package>>() {}.type
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

        fun getCreateTrans(
            accessToken: String,
            packageID: String,
            serverID: String,
            deviceID: String,
            storeProductID : String?,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (result: TransactionPaymentModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    var _storeProductID = storeProductID
                    if (_storeProductID == null)
                        _storeProductID = ""
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_ACCESSTOKEN, accessToken)
                        .addField(SDKParams.PARAM_PACKAGE_ID, packageID)
                        .addField(SDKParams.PARAM_SERVERID, serverID)
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_STORE_PRODUCT_ID, _storeProductID)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT,
                            vn.weplayz.sdk.constants.Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION,
                            vn.weplayz.sdk.constants.Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKWeplayZManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, vn.weplayz.sdk.constants.Constants.MAC_ADDRESS)
                        .execute()
                    mainThreadCallback(Runnable {
                        listener.invoke(
                            Gson().fromJson(
                                JSONObject(result).getString("r"),
                                TransactionPaymentModel::class.java
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

        fun getChargeToGame(
            accessToken: String,
            transactionID: String,
            packageID: String,
            orderIDIAP: String,
            productID: String,
            signatureIAP: String,
            dataReceipt : String,
            orderID: String,
            serverID: String,
            roleID : String,
            roleName : String,
            clientOS : String,
            deviceID: String,
            otherData : String,
            useCoin : String,
            linkAPI: String,
            time: String,
            sign: String,
            listener: (result: PaymentDataGameModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_ACCESSTOKEN, accessToken)
                        .addField(SDKParams.PARAM_TRANSACTION_ID, transactionID)
                        .addField(SDKParams.PARAM_PACKAGE_ID, packageID)
                        .addField(SDKParams.PARAM_ORDER_ID_IAP, orderIDIAP)
                        .addField(SDKParams.PARAM_PRODUCT_ID, productID)
                        .addField(SDKParams.PARAM_SIGNATURE_IAP, signatureIAP)
                        .addField(SDKParams.PARAM_DATA_RECEIPT, dataReceipt)
                        .addField(SDKParams.PARAM_ORDER_ID, orderID)
                        .addField(SDKParams.PARAM_SERVERID, serverID)
                        .addField(SDKParams.PARAM_ROLE_ID, roleID)
                        .addField(SDKParams.PARAM_ROLE_NAME, roleName)
                        .addField(SDKParams.PARAM_OTHER_DATA, otherData)
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_CLIENTOS, clientOS)
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_USE_COIN,useCoin)
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
                                PaymentDataGameModel::class.java
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


        fun trackerIAP(

            data: String,
            deviceID : String,
            clientOS : String,
            clientIP : String,
            linkAPI : String,
            time: String,
            sign: String,
            listener: (config: BaseConfigsDataModel?, e: Exception?) -> Unit
        ) {
            submitSubTask(Runnable {
                try {
                    val result = PostRequest("${getDomainAPI()}/${linkAPI}", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addField(SDKParams.PARAM_APP_KEY, SDKWeplayZManager.getAPP_KEY())
                        .addField(SDKParams.PARAM_CLIENTOS, vn.weplayz.sdk.constants.Constants.CLIENT_OS)
                        .addField(SDKParams.PARAM_TIME, time)
                        .addField(SDKParams.PARAM_SIGN, sign)
                        .addField(SDKParams.PARAM_ENVIROMENT, vn.weplayz.sdk.constants.Constants.ENVIROMENT)
                        .addField(SDKParams.PARAM_SDK_VERSION, vn.weplayz.sdk.constants.Constants.VERSION_SDK)
                        .addField(SDKParams.PARAM_PACKAGENAME, SDKWeplayZManager.getPackageName())
                        .addField(SDKParams.PARAM_CLIENT_IP, SDKParams.IPLOCAL)
                        .addField(SDKParams.PARAM_MAC_ADDRESS, vn.weplayz.sdk.constants.Constants.MAC_ADDRESS)

                        .addField(SDKParams.PARAM_DATA_TRACK_IAP, data)
                        .addField(SDKParams.PARAM_CLIENT_IP, clientIP)
                        .addField(SDKParams.PARAM_DEVICEID, deviceID)
                        .addField(SDKParams.PARAM_CLIENTOS, clientOS)
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
                    val ee = proccessExceptionConnection(e)
                    mainThreadCallback(Runnable { listener.invoke(null, ee) })
                }
            }
            )
        }

    }
}