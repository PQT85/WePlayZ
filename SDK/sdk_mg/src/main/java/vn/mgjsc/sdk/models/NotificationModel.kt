package vn.mgjsc.sdk.models

import com.google.gson.annotations.SerializedName

data class NotificationModel(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    @SerializedName("is_show_dialog")
    val isShowDialog: Boolean = false,
    @SerializedName("is_dialog_message")
    val isDialogMessage: Boolean = false,
    @SerializedName("title_dialog")
    val titleDialog: String = "",
    @SerializedName("message_dialog")
    val messageDialog: String = ""
)