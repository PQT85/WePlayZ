package vn.mgjsc.sdk.models

import com.google.gson.annotations.SerializedName

/**
 * Created by songpq-toanpq on 03/02/2020.
 */
data class PaymentDataGameModel(
    @SerializedName("OrderID") val orderID: String,
    @SerializedName("Package") val packageID: String,
    @SerializedName("PackageID") val packageItem: String,
    @SerializedName("ProductID") val productID: String,
    @SerializedName("Product") val product: String,
    @SerializedName("StoreProductID") val storeProductID: String,
  //  @SerializedName("Amount") val amount: Float,
    @SerializedName("Time") val time: String,
    @SerializedName("TimeSDKServer") val timeSDKServer: String,
    @SerializedName("Other") val other: String,
    @SerializedName("RoleID") val roleID: String,
    @SerializedName("RoleName") val RoleName: String
)