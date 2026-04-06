package vn.weplayz.sdk.models

import com.google.gson.annotations.SerializedName

/**
 * Created by songpq-toanpq on 03/02/2020.
 */
data class TransactionPaymentModel(
    @SerializedName("TransactionID") val transactionID: String,
    @SerializedName("PackageID") val packageID: String,
    @SerializedName("StoreProductID") val storeProductID: String="",
    @SerializedName("Amount") val amount: Float,
    @SerializedName("AskUseCoin") val askUseCoin: String="",
    @SerializedName("Yes") val textYes: String="",
    @SerializedName("No") val textNo: String=""




)
