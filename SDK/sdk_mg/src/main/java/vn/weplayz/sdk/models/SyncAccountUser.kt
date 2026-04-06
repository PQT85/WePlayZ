package vn.weplayz.sdk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by songpq-toanpq on 21/11/2019.
 */
data class SyncAccountUser(
    @SerializedName("fb") val syncFB: Int=0,
    @SerializedName("gg") val synGG: Int=0,
    @SerializedName("dv") val SyncQP: Int=0

):Serializable
