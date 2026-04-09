package vn.weplayz.sdk.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SDKShowConfigModel(
    @SerializedName("IsShowAccount") val isShowAccount: Int,
    @SerializedName("IsShowQuickPlay") val isShowQuickPlay: Int,
    @SerializedName("IsShowFB") val isShowFB: Int,
    @SerializedName("IsShowGG") val isShowGG: Int,
    @SerializedName("IsShowApple") val isShowApple: Int,
    @SerializedName("IsShowAds") val IsShowAds: Int = 0,
    @SerializedName("IsCollapseInfo") val IsCollapseInfo: Int,
    @SerializedName("IsShowTikTok") val isShowTiktok: Int
)
