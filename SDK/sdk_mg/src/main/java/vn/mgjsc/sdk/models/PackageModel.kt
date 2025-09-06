package vn.mgjsc.sdk.models


import com.google.gson.annotations.SerializedName
import vn.mgjsc.sdk.constants.Constants

/**
 * Created by songpq-toanpq on 31/01/2020.
 */
data class PackageModel(
    @SerializedName("Title") val title: String = "",
    @SerializedName("Desc") val desc: String = "",
    @SerializedName("Image") val image: String = "",
    @SerializedName("Package") val packageID: String = "",
    @SerializedName("ProductID") val productIDStore: String = "",
    @SerializedName("ValueVND") val valueVND: Float = 0.0f,
    @SerializedName("ValueUSD") val valueUSD: Float = 0.0f,
    @SerializedName("ClientOS") val clientOS: String = Constants.CLIENT_OS



)