package vn.weplayz.sdk.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by songpq-toanpq on 20/11/2019.
 */

//@Keep
data class ExtraDataValueModel(

    @SerializedName("HashKeyHack") val HashKeyHack: ArrayList<String>,
    @SerializedName("URL_Intro") val urlIntro: String,
    @SerializedName("InfoStore_Autologin") val InfoStore_Autologin: String,
    @SerializedName("IAP_Android_PublicKey") val IAP_Android_PublicKey: String,
    @Keep
    @SerializedName("FlagNews") val flagNews: Event

)


