package vn.mgjsc.sdk.utils


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import vn.mgjsc.sdk.R



class AppMessage {
    interface EventMessage {
        fun onClickConfirm() {}
        fun onClickCancel() {}
    }

    interface EventPopup {
        fun onClick(isUpdate: Boolean) {}
    }

    class ShowMessage constructor(var activity: Activity? = null,
                                  var message: String = "",
                                  var title: String = "",
                                  var textConfirm: String = "",
                                  var listener: EventMessage? = null) {
        private var dialog: Dialog? = null



        private fun init() {
            ifNotNull(activity, {

                val inflater =
                    it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.dialog_message_confirm_sdk, null)
                dialog = Dialog(it)
                ifNotNull(dialog, { mDialog ->
                    view.let { view ->
                        mDialog.setContentView(view)
                        mDialog.setCancelable(false)

                        val color = ColorDrawable(Color.TRANSPARENT)
                        val inset = InsetDrawable(color, it.dpFromPx(16F).toInt())
                        mDialog.window!!.setBackgroundDrawable(inset)
                        mDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)
                        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
                        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
                        val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
                        if (textConfirm.isNotEmpty()) {
                            btnConfirm.text = textConfirm
                        }
                        else {
                            btnConfirm.text = it.getString(R.string.mg_text_cancel)
                        }
                        if (title.isNotEmpty()) {
                            tvTitle.text = title
                            tvTitle.visibility = View.VISIBLE
                        } else {
                            tvTitle.visibility = View.GONE
                        }
                        if (message.isNotEmpty()) {
                            tvMessage.text = message
                        }
                        btnCancel.visibility = View.GONE
                        btnConfirm.setOnClickListener {
                            ifNotNull(listener, { mListener ->
                                mListener.onClickConfirm()
                            })
                            mDialog.dismiss()
                        }
                    }
                })
            })
        }

        fun show() {

            if(activity != null && !activity!!.isFinishing() && !activity!!.isDestroyed()) {
                activity?.runOnUiThread {
                    init()

                    dialog?.show()
                }
            }
        }

        fun closeDialog() {
            dialog?.dismiss()
        }

//        fun setTextConfirm(textConfirm: String) = apply { btnConfirm.text = textConfirm }

        data class Builder(private val activity: Activity,
                           private var message: String = "",
                           private var title: String = "",
                           private var textConfirm: String = "",
                           private var listener: EventMessage? = null) {
            fun setMessage(message: String) = apply { this.message = message }
            fun setTitle(title: String) = apply { this.title = title }
            fun setTextConfirm(textConfirm: String) = apply { this.textConfirm = textConfirm }
            fun setListener(listener: EventMessage?) = apply { this.listener = listener }
            fun build() = ShowMessage(activity, message, title, textConfirm, listener).show()
            fun buildNotShow() = ShowMessage(activity, message, title, textConfirm, listener)
        }
    }

    open class ShowMessageConfirm constructor(var activity: Activity? = null,
                                              var message: String = "",
                                              var title: String = "",
                                              var textConfirm: String = "",
                                              var textCancel: String = "",
                                              var listener: EventMessage? = null) {
        private var dialog: Dialog? = null



        private fun init() {
            ifNotNull(activity, {
                val inflater = it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.dialog_message_confirm_sdk, null)
                dialog = Dialog(it)
                ifNotNull(dialog, { mDialog ->
                    mDialog.setContentView(view)
                    mDialog.setCancelable(false)

                    val color = ColorDrawable(Color.TRANSPARENT)
                    val inset = InsetDrawable(color, it.dpFromPx(16F).toInt())
                    mDialog.window!!.setBackgroundDrawable(inset)
                    mDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    val btnConfirm = view.findViewById<MaterialButton>(R.id.btnConfirm)
                    val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
                    val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
                    val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
                    if (textConfirm.isNotEmpty()) {
                        btnConfirm.text = textConfirm
                    }
                    if (textCancel.isNotEmpty()) {
                        btnCancel.text = textCancel
                    }
                    if (title.isNotEmpty()) {
                        tvTitle.text = title
                        tvTitle.visibility = View.VISIBLE
                    } else {
                        tvTitle.visibility = View.GONE
                    }
                    if (message.isNotEmpty()) {
                        tvMessage.text = message
                    }

                    btnConfirm.setOnClickListener {
                        ifNotNull(listener, { mListener ->
                            mListener.onClickConfirm()
                        })
                        mDialog.dismiss()
                    }
                    btnCancel.setOnClickListener {
                        ifNotNull(listener, { mListener ->
                            mListener.onClickCancel()
                        })
                        mDialog.dismiss()
                    }
                })
            })
        }

        fun show() {
            if(activity != null && !activity!!.isDestroyed() && !activity!!.isFinishing())
            activity?.runOnUiThread {
                init()
                dialog?.show()
            }

        }

        fun closeDialog() {
            dialog?.dismiss()
        }

        data class Builder(private val activity: Activity,
                           private var message: String = "",
                           private var title: String = "",
                           private var textConfirm: String = "",
                           private var textCancel: String = "",
                           private var listener: EventMessage? = null) {
            fun setMessage(message: String) = apply { this.message = message }
            fun setTitle(title: String) = apply { this.title = title }
            fun setTextConfirm(textConfirm: String) = apply { this.textConfirm = textConfirm }
            fun setTextCancel(textCancel: String) = apply { this.textCancel = textCancel }
            fun setListener(listener: EventMessage?) = apply { this.listener = listener }
            fun build() = ShowMessageConfirm(activity, message, title, textConfirm, textCancel, listener).show()
        }
    }

//    class ShowPopupCheckUpdate private constructor(
//        activity: Activity,
//        private val message: String = "",
//        private val title: String = "",
//        private val listButton: List<ButtonCheckUpdateModel> = listOf(),
//        var listener: EventPopup? = null
//    ) {
//        private var dialog: Dialog? = null
//        private var _binding: DialogCheckUpdateSdkBinding? = null
//        private val binding get() = _binding!!
//
//        init {
//            ifNotNull(activity, {
//                _binding = DialogCheckUpdateSdkBinding.inflate(it.layoutInflater, null, false)
//                val view = binding.root
//                dialog = Dialog(it)
//                ifNotNull(dialog, { mDialog ->
//                    mDialog.setContentView(view)
//                    mDialog.setCancelable(false)
//
//                    val orientation: Int = activity.resources.configuration.orientation
//                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        val deviceWidth = activity.getDeviceWidth()
//                        val paddingStartEnd = deviceWidth * 16 / 100
//                        val color = ColorDrawable(Color.TRANSPARENT)
//                        val inset =
//                            InsetDrawable(color, paddingStartEnd, 0, paddingStartEnd, 0)
//                        mDialog.window!!.setBackgroundDrawable(inset)
//                    } else {
//                        val color = ColorDrawable(Color.TRANSPARENT)
//                        val inset = InsetDrawable(color, it.dpFromPx(12F).toInt())
//                        mDialog.window!!.setBackgroundDrawable(inset)
//                    }
//
//                    mDialog.window!!.setLayout(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    )
//
//                    if (title.isNotEmpty()) {
//                        binding.tvTitle.text = title
//                        binding.tvTitle.visibility = View.VISIBLE
//                    } else {
//                        binding.tvTitle.visibility = View.GONE
//                    }
//                    if (message.isNotEmpty()) {
//                        binding.tvMessage.text = message.replace("|", "\n")
//                    }
//
//                    binding.layoutButton.removeAllViews()
//                    ifNotNull(listButton, { listItem ->
//                        for (item in listItem) {
//                            val bindingItemButton = ItemButtonSdkBinding.inflate(it.layoutInflater, null, false)
//                            val viewButton = bindingItemButton.root
//                            bindingItemButton.button.text = item.label
//                            bindingItemButton.button.setOnClickListener {
//                                if (item.link.isNotEmpty()) {
//                                    val intent = Intent(
//                                        Intent.ACTION_VIEW,
//                                        Uri.parse(item.link))
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    activity.startActivity(intent)
//                                }
//                                listener?.onClick(isUpdate = item.link.isNotEmpty())
//                                mDialog.dismiss()
//                            }
//                            binding.layoutButton.addView(viewButton)
//                        }
//                    })
//                    mDialog.show()
//                })
//            })
//        }
//
//        data class Builder(
//            private val activity: Activity,
//            var message: String = "",
//            var title: String = "",
//            var listButton: List<ButtonCheckUpdateModel> = listOf(),
//            var listener: EventPopup? = null
//        ) {
//            fun setMessage(message: String) = apply { this.message = message }
//            fun setTitle(title: String) = apply { this.title = title }
//            fun setListButtonAction(listButton: List<ButtonCheckUpdateModel>?) =
//                apply {
//                    ifNotNull(listButton, {
//                        this.listButton = it
//                    })
//                }
//
//            fun setListener(listener: EventPopup?) = apply {
//                this.listener = listener
//            }
//
//            fun build() =
//                ShowPopupCheckUpdate(activity, message, title, listButton, listener)
//        }
//    }
}

fun Activity.showMessage(): AppMessage.ShowMessage.Builder {
    return AppMessage.ShowMessage.Builder(this)
}

fun Activity.showMessageConfirm(): AppMessage.ShowMessageConfirm.Builder {
    return AppMessage.ShowMessageConfirm.Builder(this)
}

inline fun Activity.showMessage(block: AppMessage.ShowMessage.() -> Unit) {
    AppMessage.ShowMessage(activity = this).apply(block).show()
}

inline fun Activity.showMessageConfirm(block: AppMessage.ShowMessageConfirm.() -> Unit) {
    AppMessage.ShowMessageConfirm(activity = this).apply(block).show()
}