package vn.mgjsc.sdk.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

import vn.mgjsc.sdk.models.SDKShowConfigModel
import java.util.*

/**
 * Created by songpq-toanpq on 20/11/2019.
 */


@Keep
data class BannerMiGame (

   @SerializedName("IdAds") val idBanner: Int,
   @SerializedName("Type") val Type: Int=0,//1 link html , 2 Source html, 3Source Image
   @SerializedName("IsForceShow") val isForceShow: Int=0,
   @SerializedName("LinkHTML") val LinkHtml: String?="",
   @SerializedName("SourceHTML") val sourceHtml: String?="",
   @SerializedName("SourceLink") val linkOpen: String?="",
   @SerializedName("SourceImage") val sourceImage: String?="",
   @SerializedName("SkipAds") var isSkipBanner: Boolean=false

)

//@Keep
data class Event (
    @SerializedName("Name") val name: String,
    @SerializedName("Link") val link: String
    )

//data class LoadAds(
//    @SerializedName("Ads") val adsMigame: List<AdsMiGame>
//)

data class BaseConfigsDataModel(
    @SerializedName("DomainAPI") val DomainAPI: String,
    @SerializedName("User_Login") val User_Login: String,
    @SerializedName("IAP_Track") val URL_IAP_Track: String,

    @SerializedName("User_RegisterDevice") val User_RegisterDevice: String,
    @SerializedName("User_Register") val User_Register: String,
    @SerializedName("User_SynDevice_FromUser") val User_SynDevice_FromUser: String,
    @SerializedName("User_SynDevice_FromFacebook") val User_SynDevice_FromFacebook: String,
    @SerializedName("User_SynDevice_FromGoogle") val User_SynDevice_FromGoogle: String,
    @SerializedName("User_GetByToken") val User_GetByToken: String="https://absdf",
    @SerializedName("User_Logout") val User_Logout: String,
    @SerializedName("User_LostPassword") val User_LostPassword: String,
    @SerializedName("User_FaceBookLogin") val User_FaceBookLogin: String,
    @SerializedName("User_GoogleLogin") val User_GoogleLogin: String,
    @SerializedName("User_TiktokLogin") val User_TiktokLogin: String ,
    @SerializedName("User_SynUser") val User_SynUser: String,

    @SerializedName("Ads_List") val Get_Banner: String,
	
	@SerializedName("DataWeb") val dataWeb: String,
	@SerializedName("LinkWeb") val linkWeb: String,



    @SerializedName("IAP_PackageList") val urlIAPDefinePackage: String,
    @SerializedName("IAP_CreateTransaction") val urlIAPCreateTrans: String,
    @SerializedName("IAP_CreateTransactionStore") val urlIAPCreateTransStore: String,
    @SerializedName("IAP_TopupProcess") val urlIAPChargeToGame: String,
    @SerializedName("User_AppleLogin") val urlAppleLogin: String,
    @SerializedName("URLPolicy") val urlPolicy: String,
    @Keep
    @SerializedName("ExtraValue") val extraValue: ExtraDataValueModel,
    @SerializedName("isRequireMoreInfo") val isRequireMoreInfo: Int=0,
    @SerializedName("disablePopupCoin") val disablePopupCoin: Int=0,
    @Keep
    @SerializedName("SDKShowConfig") val SDKShowConfig: SDKShowConfigModel,
    @Keep
    @SerializedName("AdsMigame") val bannerMigame: ArrayList<BannerMiGame>? = null,

    @Keep
    @SerializedName ("AdjustConfig") var adjustConfig : MigaAdjustConfig,



)

data class MigaAdjustConfig (

    @SerializedName ("environment") val AdjustEnvironment : String = "production",

    @SerializedName("AppToken") val AdjustAppToken : String = "1p86kz440pmo",

    @SerializedName ("ClickLogin") val AdjustClickLogin: String = "zbxxug",

    @SerializedName ("ClickPayment") val AdjustClickPayment : String = "1s6c1w",
    @SerializedName ("PaymentIapClicked") val AdjustChoosePaymentIAP : String = "4776dl",
    @SerializedName ("PaymentWalletClicked") val AdjustChoosePaymentMiCoin : String = "h2kvid",


    @SerializedName ("ClickRegister") val AdjustClickRegister: String = "w32ibj",
    @SerializedName ("ClickSync") val AdjustSyncValidate: String = "y349en",
    @SerializedName ("LoginFailed") val AdjustLoginFailed : String = "ag19x0",


    @SerializedName ("ClickLoginFB") val AdjustClickLoginFB : String = "63wjuh",
    @SerializedName ("LoginFBFailed") val AdjustLoginFBFailed : String = "w48tkw",
    @SerializedName ("LoginFBSucess") val AdjustLoginFBSuccess: String = "7jokqb",

    @SerializedName ("ClickLoginGG") val AdjustClickLoginGG : String = "m81ceh",
    @SerializedName ("LoginGGFailed") val AdjustLoginGGFailed: String = "t8xfbx",
    @SerializedName ("LoginGGSucess") val AdjustLoginGGSuccess: String = "zifveg",

    @SerializedName("LoginSuccess") val AdjustLoginSuccess : String = "o6t5oc",

    @SerializedName("PaymentFailed") val AdjustPaymentFailed:String = "ix7p74",
    @SerializedName ("PaymentIapCancel") val AdjustPaymentIAPFailed : String = "f0lm5a",
    @SerializedName ("PaymentIapRevenue") val AdjustPaymentRevenueIAP : String = "5ebndy",
    @SerializedName ("PaymentIapSuccess") val AdjustPaymentIAPSuccess: String = "ldk110",


    @SerializedName("PaymentSuccess") val AdjustPaymentSuccess: String = "58iwft",
    @SerializedName ("PaymentTotalRevenue") val AdjustPaymentRevenueTotal: String = "k1b3t4",
    @SerializedName ("PaymentWalletCancel") val AdjustPaymentMicoinFailed:String = "bpycgi",

    @SerializedName ("PaymentWalletRevenue") val AdjustPaymentRevenueMicoin: String = "voc8ta",
    @SerializedName("PaymentWalletSuccess") val AdjustPaymentMicoinSuccess: String = "f3ss9l",


    @SerializedName ("ClickQP") val AdjustClickQuickPlay: String = "2w8mhy",
    @SerializedName ("LoginQPFailed") val AdjustQuickPlayFailed:String = "p4d590",
    @SerializedName ("LoginQPSuccess") val AdjustQuickPlaySuccess: String = "n0nr2m",

    @SerializedName ("RegisterFailed") val AdjustRegisterFailed: String = "qioyey",
    @SerializedName ("RegisterSuccess") val AdjustRegisterSuccess : String = "5h0rgl",

    @SerializedName ("SyncAccountFailed") val AdjustSyncFailed : String = "1rfw2d",
    @SerializedName ("SyncAccountSuccess") val AdjustSyncSuccess : String = "gltekn",

    @SerializedName("TotalPayment") val AdjustPaymentTotalPayment:String = "q3qhi3",

    @SerializedName ("LoginValidationSuccess") val AdjustLoginValidationSuccess: String = "",
    @SerializedName ("LoginValidationFailed") val AdjustLoginValidationFailed: String = "",
    @SerializedName ("VerifyToken") val AdjustVerifyToken : String = "",
    @SerializedName ("VerifyTokenSuccess") val AdjustVerifyTokenSuccess : String = "",
    @SerializedName ("VerifyTokenFailed") val AdjustVerifyTokenFailed : String = "",

)

