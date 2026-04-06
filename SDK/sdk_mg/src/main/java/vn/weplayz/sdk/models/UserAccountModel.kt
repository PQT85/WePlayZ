package vn.weplayz.sdk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by songpq-toanpq on 21/11/2019.
 */

data class UserAccountModel(
    @SerializedName("UserID") val userId: String="",
    @SerializedName("UserName") var userName: String="",
    @SerializedName("DisplayName") val displayName: String?="",
    @SerializedName("Avatar") val avata: String="",
    @SerializedName("BirthDay") val birthDay: String?="",
    @SerializedName("Gender") val gender: Int=0,
    @SerializedName("PrimaryMobile") val primaryMobile: String="",
    @SerializedName("Email") val email: String="",
    @SerializedName("AccessToken") var accessToken: String="",
    @SerializedName("Address") val address: String="",
    @SerializedName("FBID") val fbId: String="",
    @SerializedName("isSave") var isSave: Boolean=false,
    @SerializedName("isSyn") val syncAccount: SyncAccountUser?=null,
	
	@SerializedName("DataWeb") val dataWeb: String,
	@SerializedName("LinkWeb") val linkWeb: String,

	
    @SerializedName("AccountType") val accountType: String=""


//    @SerializedName("isSyn") val isSyn: Int,
//    @SerializedName("isSynFB") val isSynFB: Int,
//    @SerializedName("isSynGG") val isSynGG: Int
):Serializable