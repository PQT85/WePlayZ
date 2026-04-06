package vn.weplayz.sdk.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object ValidateSDK {

    @JvmStatic
    fun isStringNullOrEmpty(str:String?): Boolean {
        if(str==null || TextUtils.isEmpty(str))
            return true;
        return false
    }

    @JvmStatic
    fun forceShowKeyboard(
        context: Context,
        editText: EditText?
    ) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(editText, InputMethodManager.SHOW_FORCED)
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    @JvmStatic
    fun forceHideKeyboard(
        context: Context,
        edText: EditText?
    ) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (edText != null) imm.hideSoftInputFromWindow(edText.windowToken, 0) else {
            if (context is Activity) imm.hideSoftInputFromWindow(
                context.window.decorView.rootView
                    .windowToken, 0
            )
        }

//		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @JvmStatic
    fun showCheckPhoneNumber(
        context: Context?,
        edt: EditText?,
        phoneNumber: String,
        hint: String?
    ): Boolean {
        return if (validatePhoneNumber(phoneNumber)) {
            false
        } else {
            setError(edt, hint)
            Toast.makeText(context, hint, Toast.LENGTH_SHORT).show()
            true
        }
    }

    @JvmStatic
    fun validatePhoneNumber(phoneNumber: String): Boolean {
        var phone = phoneNumber
        if (phone.startsWith("+")) phone = phone.substring(1)
        if (!TextUtils.isDigitsOnly(phone)) return false
        return if (phone.startsWith("840")) {
            if (phone.length == 12) true else false
        } else if (phone.startsWith("84")) {
            if (phone.length == 11) true else false
        } else if (phone.startsWith("0")) {
            if (phone.length == 10) true else false
        } else {
            if (phone.length == 9) true else false
        }
    }

    @JvmStatic
    fun showStringEmpty(
        context: Context?,
        text: String?,
        hint: String?
    ): Boolean {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(context, hint, Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    @JvmStatic
    fun setError(editText: EditText?, hint: String?) {
        setError(editText, hint, true)
    }

    @JvmStatic
    fun setError(
        editText: EditText?,
        hint: String?,
        showToast: Boolean
    ) {
        if (editText != null) {
            editText.error = hint
            if (showToast) Toast.makeText(
                editText.context,
                hint,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @JvmStatic
    fun showCheckTextEmpty(
        context: Context?,
        edt: EditText,
        hint: String?
    ): Boolean {
        if (TextUtils.isEmpty(edt.text.toString())) {
            setError(edt, hint)
            Toast.makeText(context, hint, Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    @JvmStatic
    fun invalidDate(date: String?): Date? {
        val df = SimpleDateFormat("dd/MM/yyyy")
        df.isLenient = false
        return try {
            df.parse(date)
        } catch (e: ParseException) {
            null
        }
    }

    val EMAIL_REGEX =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    @JvmStatic
    fun isEmailAddress(email: String?): Boolean {
        return Pattern.matches(EMAIL_REGEX, email)
    }

    @JvmStatic
    var m_click_start = 0L;
    var m_counter = 0;

    @JvmStatic
    fun isDebugModeOn() : Boolean
    {
        //Log.d("PQT"," time click on debug mode: "+m_click_start);
        if(m_click_start == 0L)
            m_click_start = System.currentTimeMillis()!!.toLong()
        if(System.currentTimeMillis() < m_click_start + 2000)
            m_counter+= 1
        else {
            m_counter = 0
            m_click_start = System.currentTimeMillis()!!.toLong()
        }
        if(m_counter >= 6)
            return true
        return false
    }
}