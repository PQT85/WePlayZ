package vn.mgjsc.sdk

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.Validate
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


//import kotlinx.android.synthetic.main.activity_mg.*
//import kotlinx.android.synthetic.main.mg_fragment_forget_pass.*
//import kotlinx.android.synthetic.main.mg_fragment_login.*
//import kotlinx.android.synthetic.main.mg_fragment_register.*
//import kotlinx.android.synthetic.main.mg_fragment_sync_account.*
//import kotlinx.android.synthetic.main.mg_main_account.*



import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vn.mgjsc.sdk.api.*
import vn.mgjsc.sdk.constants.Constants
import vn.mgjsc.sdk.constants.Encrypt
import vn.mgjsc.sdk.constants.SDKParams
import vn.mgjsc.sdk.iapsdk.IAPSDKManager
import vn.mgjsc.sdk.utils.*
import vn.mgjsc.sdk.api.AuthenApi
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import com.facebook.AccessToken
import com.facebook.*
import vn.mgjsc.sdk.databinding.ActivityMgBinding
import vn.mgjsc.sdk.databinding.MgFragmentForgetPassBinding
import vn.mgjsc.sdk.databinding.MgFragmentLayoutBackBinding
import vn.mgjsc.sdk.databinding.MgFragmentLayoutTermConditionBinding
import vn.mgjsc.sdk.databinding.MgFragmentLoginBinding
import vn.mgjsc.sdk.databinding.MgFragmentRegisterBinding
import vn.mgjsc.sdk.databinding.MgFragmentSyncAccountBinding
import vn.mgjsc.sdk.databinding.MgMainAccountBinding
import vn.mgjsc.sdk.models.*

class MGActivity : AppCompatActivity() {

    private var mDialog: Dialog? = null
    private var mProgressDialog: Dialog? = null
    private var stepDebug = 0
    private lateinit var binding: ActivityMgBinding;
    private lateinit var bindingFgLogin : MgFragmentLoginBinding;
    private lateinit var bindingFgRegister : MgFragmentRegisterBinding;
   // private lateinit var bindingFgTnC : MgFragmentLayoutTermConditionBinding;
    private lateinit var bindingFgForgetPass : MgFragmentForgetPassBinding
    private lateinit var bindingFgSync : MgFragmentSyncAccountBinding
    //private lateinit var bindingFgBack : MgFragmentLayoutBackBinding
    private lateinit var bindingMainAccount : MgMainAccountBinding
    open fun showLoading() {
        binding.activityMgPgLoading.visibility = View.VISIBLE
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            return
        }

        hideLoading()
        if (!isFinishing) {
            mProgressDialog = showLoadingDialog()
        }
    }
//    fun Activity.showLoadingDialog(): Dialog {
//        val progressDialog = Dialog(this)
//        progressDialog.show()
//        if (progressDialog.window != null) {
//            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
//        progressDialog.setContentView(R.layout.progress_dialog_sdk)
//        progressDialog.setCancelable(false)
//        progressDialog.setCanceledOnTouchOutside(false)
//
//        return progressDialog
//    }

    open fun hideLoading() {
        binding.activityMgPgLoading.visibility = View.GONE;
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_mg)
        binding = ActivityMgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindingFgRegister = binding.activityMgLayoutRegister
        bindingMainAccount = binding.activityMgLayoutMainAccount
        bindingFgSync = binding.activityMgLayoutSyncAccount
        bindingFgLogin = binding.activityMgLayoutLogin
        bindingFgForgetPass = binding.activityMgLayoutForgetPass


        initSDK()
        // activate immersive mode
//        activateImmersiveMode ImmersiveControl.(this);

        binding.debugMode.setOnClickListener {
           // Log.d("pqt", "test ----")
            if(ValidateSDK.isDebugModeOn()) {
                SDKManager.setIsDebug(true)
                Toast.makeText(this,"Debug enabled",Toast.LENGTH_SHORT).show()
                //Log.d("pqt", "debug enabled")
            }
        }
        Log.d("PQT Debug", "----------" + resources.getString(R.dimen.size_bg));

      //  if(!SDKManager.isAdjustValid)
        //    showToast("<!!!!> Adjust SDK isn't initialized properly. <!!!!> ")


    }
    var context: Context? = null
    var packageID : String? = null
    var serverID : String? = null
    var orderID : String? = null
    var roleID : String? = ""
    var roleName: String?  = ""
    var otherData : String? = ""
    var accessToken : String? = ""
    var packageStoreID = ""
    var transactionID = ""
    var isInit  = false
    var hideMainMenu = false
    private fun verifyInfoPayment() : Boolean
    {
        Constants.showDataLog("packageID",packageID)
        Constants.showDataLog("orderID",orderID)
        Constants.showDataLog("roleID",roleID)
        Constants.showDataLog("roleName",roleName)
        Constants.showDataLog("otherData",otherData)
        Constants.showDataLog("accessToken",accessToken)
        if(ValidateSDK.isStringNullOrEmpty(packageID) || ValidateSDK.isStringNullOrEmpty(orderID) || ValidateSDK.isStringNullOrEmpty(accessToken))
        {
            onPaymentFail(getString(R.string.mg_info_payment_faild))
            return false
        }
        return true
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        setIntent(intent)
        if(stateProcess == Constants.ACTION_PAYMENT)
            if(TextUtils.isEmpty(packageID) || TextUtils.isEmpty(orderID) || TextUtils.isEmpty(accessToken)) {
            packageID = intent?.getStringExtra(Constants.KEY_DATA_PACKAGE_ID)
            serverID = intent?.getStringExtra(Constants.KEY_DATA_SERVER_ID)
            orderID = intent?.getStringExtra(Constants.KEY_DATA_ORDER_ID)
            roleID = intent?.getStringExtra(Constants.KEY_DATA_ROLE_ID)
            roleName = intent?.getStringExtra(Constants.KEY_DATA_ROLE_NAME)
            otherData = intent?.getStringExtra(Constants.KEY_DATA_OTHER_DATA)
            accessToken = getUser()?.accessToken

            val isInit = intent?.getBooleanExtra("isInit",false)

            if(isInit==true)
                this.isInit = isInit
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        if(handle != null) {
            handle.removeCallbacks(runnable)
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus)
            ImmersiveControl.activateImmersiveMode(this);
    }
    override fun onResume() {
        super.onResume()
        if(stateProcess == Constants.ACTION_PAYMENT)
        if(TextUtils.isEmpty(packageID) || TextUtils.isEmpty(orderID) || TextUtils.isEmpty(accessToken)) {
            packageID = intent.getStringExtra(Constants.KEY_DATA_PACKAGE_ID)
            serverID = intent?.getStringExtra(Constants.KEY_DATA_SERVER_ID)
            orderID = intent.getStringExtra(Constants.KEY_DATA_ORDER_ID)
            roleID = intent.getStringExtra(Constants.KEY_DATA_ROLE_ID)
            roleName = intent.getStringExtra(Constants.KEY_DATA_ROLE_NAME)
            otherData = intent.getStringExtra(Constants.KEY_DATA_OTHER_DATA)
            accessToken = getUser()?.accessToken

//            if(token!=null)
//                paramsIAP?.accessToken = token

        }
        val isInit = intent?.getBooleanExtra("isInit",false)
        if(isInit==true)
            this.isInit = isInit
        initSDK()

        ImmersiveControl.activateImmersiveMode(this);
    }
    val runnable = Runnable { SDKManager.trackerIAP(this) }
    val handle = Handler()
    fun initSDK()
    {

        if(isInit || bindingMainAccount.mgMainAccountLlMigame==null)
            return
        isInit = true
        hideMainMenu = false
        intent.putExtra("isInit",isInit);
        sharedPreferences = getSharedPreferences(SDKManager.PREFERENCE_ACCOUNT_MANAGER, Context.MODE_PRIVATE)

        context = this
        initFacebook()
        initGoogle()
        handle.removeCallbacks(runnable)
        handle.post(runnable)
        if(intent != null)
        {
            val temp = intent.getStringExtra(Constants.KEY_ACTION)
            if(!TextUtils.isEmpty(temp)) {
                stateProcess = temp!!

                if(stateProcess == Constants.ACTION_LOGOUT_ONLY)
                    hideMainMenu = true;
            }

        }
        showMainMenu()

        when(stateProcess)
        {
            Constants.ACTION_LOGIN->{



                processLogin()
            }
            Constants.ACTION_LOGOUT->{
                processLogout()
            }
            Constants.ACTION_LOGOUT_ONLY->{
                processLogoutOnly()
            }
            Constants.ACTION_PAYMENT->{

                packageID = intent.getStringExtra(Constants.KEY_DATA_PACKAGE_ID)
                orderID = intent.getStringExtra(Constants.KEY_DATA_ORDER_ID)
                serverID = intent.getStringExtra(Constants.KEY_DATA_SERVER_ID)
                roleID = intent.getStringExtra(Constants.KEY_DATA_ROLE_ID)
                roleName = intent.getStringExtra(Constants.KEY_DATA_ROLE_NAME)
                otherData = intent.getStringExtra(Constants.KEY_DATA_OTHER_DATA)
                accessToken = getUser()?.accessToken
                processPayment()
            }

        }
        registerMainMenuScreen()
        registerLoginScreen()
        registerRegisterScreen()
        registerForgetPasswordScreen()
        registerForSyncAccount()
        getConfig()
    }
    private fun processLogin()
    {
        showMainMenu()
        val user = getUser()
        if(user!=null && !TextUtils.isEmpty(user.accessToken))
        {
            nextState = STATE_VERIFY_ACCESSTOKEN
        }
    }


    private fun processLogoutOnly()
    {
        hideMainMenu()
        nextState = STATE_LOGOUT
    }
    private fun processLogout()
    {
        showMainMenu()
        nextState = STATE_LOGOUT
    }
    private fun processPayment()
    {
        showMainMenu()

        val user = getUser()
        if(user!=null && !TextUtils.isEmpty(user.accessToken))
        {
            binding.activityMigameLlContainer.visibility = View.GONE;
            nextState = STATE_VERIFY_ACCESSTOKEN
        }
        else
        {
            binding.activityMigameLlContainer.visibility = View.VISIBLE
        }
    }
    private fun registerMainMenuScreen()
    {


        bindingMainAccount.mgMainAccountLlMigame.setOnClickListener {
            showLogin()
        }

        bindingMainAccount.mgMainAccountLlFb.setOnClickListener {
            onCLickLoginFacebook()
        }

        bindingMainAccount.mgMainAccountLlGg.setOnClickListener {
            onCLickLoginGoogle()
        }

        bindingMainAccount.mgMainAccountLlQp.setOnClickListener {
            nextState(STATE_LOGIN_QP)
        }


        bindingMainAccount.mgMainAccountTvLogin.setOnClickListener {
            stepDebug+=1
            if(stepDebug>6)
                SDKManager.setIsDebug(true)
            if(stepDebug>12)
                SDKManager.setIsDebug(false)
        }
    }



    fun validatePasswordRecovery() : Boolean {
        //var result : Boolean = true
        //val json = JSONObject()
        /*if(ValidateSDK.showCheckTextEmpty(this,mg_fragment_forget_pass_ed_email,getString(R.string.mg_text_forget_pass_email_required)))
        {
            //json.put("error",getString(R.string.mg_text_forget_pass_email_required))
            //result = false
            return false;
        }
        if(!ValidateSDK.isEmailAddress(mg_fragment_forget_pass_ed_email.text.toString()))
        {
            mg_fragment_forget_pass_ed_email.setError(getString(R.string.mg_text_invalid_email))
            showToast(getString(R.string.mg_text_invalid_email))
            return false;
        }*/
      //  if(ValidateSDK.isEmailAddress()
//        return true

        var result : Boolean = true


        if(ValidateSDK.showCheckTextEmpty(this,bindingFgForgetPass.mgFragmentForgetPassEdEmail,getString(R.string.mg_text_forget_pass_email_required)))
        {
            result = false

        }else {
            var error: String = ""
            val accountName = bindingFgForgetPass.mgFragmentForgetPassEdEmail.text.toString()
            if(accountName.length < 6 || accountName.length>32) {
                error = getString(R.string.mg_text_warning_invalid_account1)
            }else {
                accountName.forEach { char ->
                    //                    if((char !in '0'..'9'))
                    if ((char !in 'a'..'z') && (char !in '0'..'9') && char != '.' && char != '@')
                    {
                        error = getString(R.string.mg_text_warning_invalid_account2)
                        return@forEach
                    }
                }
            }
            if (!ValidateSDK.validatePhoneNumber(bindingFgForgetPass.mgFragmentForgetPassEdEmail.text.toString()) &&
                !ValidateSDK.isEmailAddress(bindingFgForgetPass.mgFragmentForgetPassEdEmail.text.toString()) &&
                !error.isNullOrEmpty()

            ) {
                result = false
                showToast(getString(R.string.mg_text_email_or_phone))
                bindingFgForgetPass.mgFragmentForgetPassEdEmail?.setError(getString(R.string.mg_text_email_or_phone))
            }
        }
        return result

    }
    fun validateLogin() : Boolean{
        var result : Boolean = true
        val json = JSONObject()
        if(ValidateSDK.showCheckTextEmpty(this,bindingFgLogin.mgFragmentLoginEdPass,getString(R.string.mg_text_error_empty_pass)))
        {
            json.put("error",getString(R.string.mg_text_error_empty_pass))
            result = false
        }else
        {
            var error: String = ""

            val pass = bindingFgLogin.mgFragmentLoginEdPass.text.toString()
            if(pass.length < 6 || pass.length>32) {
                error = getString(R.string.mg_text_error_invalid_pass)
            }
            if(!error.isNullOrEmpty()) {
                ValidateSDK.setError(bindingFgLogin.mgFragmentLoginEdPass, error)
                json.put("error",error)
                result = false
            }
        }
        if(ValidateSDK.showCheckTextEmpty(this,bindingFgLogin.mgFragmentLoginEdAccount,getString(R.string.mg_text_error_empty_account)))
        {
            json.put("error",getString(R.string.mg_text_error_empty_account))
            result = false
        }else {
            var error: String = ""
            val accountName = bindingFgLogin.mgFragmentLoginEdAccount.text.toString()
            if(accountName.length < 6 || accountName.length>32) {
                error = getString(R.string.mg_text_error_invalid_account1)
            }else {
                accountName.forEach { char ->
//                    if((char !in '0'..'9'))
                    if ((char !in 'a'..'z') && (char !in '0'..'9') && char != '.' && char != '@' && (char !in 'A'..'Z'))
                    {
                        error = getString(R.string.mg_text_error_invalid_account2)
                        return@forEach
                    }
                }
            }

            if(!error.isNullOrEmpty()) {
                ValidateSDK.setError(bindingFgLogin.mgFragmentLoginEdAccount, error)
                json.put("error",error)
                result = false
            }
        }
//        if(result==true)
//        {
//            if(!validateCaptcha())
//            {
//                Utilities.setError(fragment_login_ed_capt,getString(R.string.migamesdk_text_invalidate_captcha))
//                showCaptcha()
//                result = false
//                json.put("error",getString(R.string.migamesdk_text_invalidate_captcha))
//            }
//        }
        if(result)
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_login_validate_success),json)
        else
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_login_validate_failed),json)

        return result
    }
    var isSavePass : Boolean = true
    var isShowPass : Boolean = false

    var isShowPassRegister : Boolean = false
    var isShowConfirmPassRegister : Boolean = false

    var isShowPassSync : Boolean = false
    var isShowConfirmPassSync : Boolean = false

    var isCheckBox: Boolean = false;

    private fun isShowRegisterScreen(): Boolean
    {
        if(binding.activityMgLayoutRegister.root.visibility == View.VISIBLE)
            return true
        return false

    }
    private fun isShowLoginScreen() : Boolean
    {

        if(binding.activityMgLayoutLogin.root.visibility == View.VISIBLE)
            return true
        return false

    }

    private fun isShowForgetPass() : Boolean
    {
        if(binding.activityMgLayoutForgetPass.root.visibility == View.VISIBLE)
            return true
        return false
    }
    private fun isShowSync() : Boolean
    {

        if(binding.activityMgLayoutSyncAccount.root.visibility == View.VISIBLE)
            return true
        return false
    }
    private fun isShowMainMenuScreen(): Boolean
    {
        if(binding.activityMgLayoutMainAccount.root.visibility == View.VISIBLE)
            return true
        return false

    }

    private fun registerForgetPasswordScreen()
    {

        //bindingFgForgetPass.mgFragmentForgetPassTvLogin.paint?.isUnderlineText = true

        //bindingFgForgetPass.mgFragmentForgetPassTvRegister.paint?.isUnderlineText = true
        bindingFgForgetPass.mgFragmentForgetPassLlBack.root.setOnClickListener {
            onBackPressed()
        }
        bindingFgForgetPass.mgFragmentForgetPassTvLogin.setOnClickListener {
            showLogin()
        }
        bindingFgForgetPass.mgFragmentForgetPassTvRegister.setOnClickListener {
            showRegister()
        }
        bindingFgForgetPass.fragmentForgetPassSubmit.setOnClickListener{
            if(validatePasswordRecovery()) {
                nextState(STATE_PASS_RECOVERY)
            }
        }
    }
    private fun registerLoginScreen()
    {

        bindingFgLogin.mgFragmentLoginTvForgetPass.paint?.isUnderlineText = true
        isSavePass = bindingFgLogin.mgFragmentLoginCkSavePass.isChecked
        bindingFgLogin.mgFragmentLoginCkSavePass.setOnCheckedChangeListener{
                buttonView, isChecked -> isSavePass = isChecked

        }

        bindingFgLogin.mgFragmentLoginLlBack.root.findViewById<ImageView>(R.id.mg_fragment_layout_back_iv_back).setOnClickListener {
            //showMainMenu()
            onBackPressed()
        }
        bindingFgLogin.mgFragmentLoginLlBack.root.findViewById<TextView>(R.id.mg_fragment_layout_back_tv_debug).setOnClickListener {
            //showMainMenu()
            stepDebug+=1
            if(stepDebug>6)
                SDKManager.setIsDebug(true)
            if(stepDebug>12)
                SDKManager.setIsDebug(false)
        }
        bindingFgLogin.mgMainAccountLlQp.setOnClickListener {nextState(STATE_LOGIN_QP)
        }
        bindingFgLogin.mgMainAccountLlFbIcon.setOnClickListener { nextState(STATE_LOGIN_FB) }
        bindingFgLogin.mgMainAccountLlGgIcon.setOnClickListener { nextState(STATE_LOGIN_GG) }

        bindingFgLogin.mgFragmentLoginTvLogin.setOnClickListener {
            if(validateLogin())
                nextState(STATE_LOGIN_MG)
            // PQT added login tracker
            var json = JSONObject()
            json.put("username", bindingFgLogin.mgFragmentLoginEdAccount.text.toString())
            TrackingManager.trackEventCount(
                context?._getString(R.string.mg_event_click_login),
                json
            )
        }

        bindingFgLogin.mgFragmentLoginTvRegister.setOnClickListener {
            showRegister()
        }
        bindingFgLogin.mgFragmentLoginTvForgetPass.setOnClickListener{
            showForgetPass()
        }


        bindingFgLogin.mgFragmentLoginIvEye.setOnClickListener {

            isShowPass = !isShowPass
            if (isShowPass) {
                bindingFgLogin.mgFragmentLoginIvEye.setImageResource(R.drawable.visibility)
                bindingFgLogin.mgFragmentLoginEdPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                bindingFgLogin.mgFragmentLoginEdPass.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                bindingFgLogin.mgFragmentLoginIvEye.setImageResource(R.drawable.invisible)
                bindingFgLogin.mgFragmentLoginEdPass.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                bindingFgLogin.mgFragmentLoginEdPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }


        val userName = getLastUserName()
        if(userName!=null)
        {
            bindingFgLogin.mgFragmentLoginEdAccount.setText(userName)
            val pass = getPassword(userName!!)
            if(!ValidateSDK.isStringNullOrEmpty(pass))
                bindingFgLogin.mgFragmentLoginEdPass.setText(pass)
        }

        bindingFgLogin.mgFragmentLoginEdAccount.setOnFocusChangeListener { view, b ->
            if(b==false)
            {
                val accname= bindingFgLogin.mgFragmentLoginEdAccount.text.toString()
                val pass = getPassword(accname)
                if(!ValidateSDK.isStringNullOrEmpty(pass))
                    bindingFgLogin.mgFragmentLoginEdPass.setText(pass)
                else
                    bindingFgLogin.mgFragmentLoginEdPass.setText("")
            }
        }
        var timer :Timer? = null
        bindingFgLogin.mgFragmentLoginEdAccount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!=null && s!!.length >= 5) {
                    timer = Timer()
                    timer?.schedule(object : TimerTask() {
                        override fun run() {
                            // TODO: do what you need here (refresh list)
                            runOnUiThread {
                                val accname= bindingFgLogin.mgFragmentLoginEdAccount.text.toString()
                                val pass = getPassword(accname)
                                if(!ValidateSDK.isStringNullOrEmpty(pass))
                                    bindingFgLogin.mgFragmentLoginEdPass.setText(pass)
                                else
                                    bindingFgLogin.mgFragmentLoginEdPass.setText("")
                            }


                        }
                    }, 500)
                }
                else{
                    bindingFgLogin.mgFragmentLoginEdPass.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (timer != null) timer?.cancel()
            }
        })


    }
    private fun registerRegisterScreen()
    {

        bindingFgRegister.mgFragmentRegisterLlBack.root.findViewById<ImageView>(R.id.mg_fragment_layout_back_iv_back).setOnClickListener {
            showLogin()
        }

        bindingFgRegister.mgFragmentRegisterLlBack.root.findViewById<TextView>(R.id.mg_fragment_layout_back_tv_debug).setOnClickListener {
            //showMainMenu()
            stepDebug+=1
            if(stepDebug>6)
                SDKManager.setIsDebug(true)
            if(stepDebug>12)
                SDKManager.setIsDebug(false)
        }

        //bindingFgRegister.
        bindingFgRegister.mgFragmentRegisterTvRegister.setOnClickListener {
            if (validateRegister())
                this.nextState(STATE_REGISTER)
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_click_register),null)
        }

        var str = getString(R.string.mg_fragment_term_condition)
        SDKManager.baseConfigModel?.let {

            str = str.replace(Regex("_URL_"),SDKManager.baseConfigModel!!.urlPolicy)

        }
        bindingFgRegister.mgFragmentLayoutRegisterTnc.mgFragmentLayoutTermCondition.text= Html.fromHtml(str)
        bindingFgRegister.mgFragmentLayoutRegisterTnc.mgFragmentLayoutTermCondition.movementMethod = LinkMovementMethod.getInstance()
        //isCheckBox = false;
        //bindingFgRegister.mgFragmentLayoutRegisterTnc.mgFragmentLayoutTermConditionCheckbox.isEnabled = isCheckBox
        bindingFgRegister.mgFragmentLayoutRegisterTnc.mgFragmentLayoutTermConditionCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->  isCheckBox=isChecked}



        bindingFgRegister.mgFragmentRegisterIvEye.setOnClickListener {
            isShowPassRegister = !isShowPassRegister
            if (isShowPassRegister) {
                bindingFgRegister.mgFragmentRegisterIvEye.setImageResource(R.drawable.visibility)

                bindingFgRegister.mgFragmentRegisterEdPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                bindingFgRegister.mgFragmentRegisterEdPass.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                bindingFgRegister.mgFragmentRegisterIvEye.setImageResource(R.drawable.invisible)
                bindingFgRegister.mgFragmentRegisterEdPass.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                bindingFgRegister.mgFragmentRegisterEdPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

        bindingFgRegister.mgFragmentRegisterIvEyeConfirm.setOnClickListener {
            isShowConfirmPassRegister = !isShowConfirmPassRegister
            if (isShowConfirmPassRegister) {
                bindingFgRegister.mgFragmentRegisterIvEyeConfirm.setImageResource(R.drawable.visibility)
                bindingFgRegister.mgFragmentRegisterEdConfirmpass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                bindingFgRegister.mgFragmentRegisterEdConfirmpass.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                bindingFgRegister.mgFragmentRegisterIvEyeConfirm.setImageResource(R.drawable.invisible)
                bindingFgRegister.mgFragmentRegisterEdConfirmpass.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                bindingFgRegister.mgFragmentRegisterEdConfirmpass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
        updateViewRegister()
    }

    private fun registerForSyncAccount()
    {

        bindingFgSync.mgFragmentSyncLlBack.root.findViewById<ImageView>(R.id.mg_fragment_layout_back_iv_back).setOnClickListener {
            synSkip()
        }
        bindingFgSync.mgFragmentSyncTvSync.setOnClickListener {
            if (validateSyncAccount())
                this.nextState(STATE_SYNC)
        }
        bindingFgSync.mgFragmentSyncTvSkip.setOnClickListener {
            synSkip()
        }


        bindingFgSync.mgFragmentSyncIvEye.setOnClickListener {
            isShowPassSync = !isShowPassSync
            if (isShowPassSync) {
                bindingFgSync.mgFragmentSyncIvEye.setImageResource(R.drawable.visibility)
                bindingFgSync.mgFragmentSyncEdPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                bindingFgSync.mgFragmentSyncEdPass.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                bindingFgSync.mgFragmentSyncIvEye.setImageResource(R.drawable.invisible)
                bindingFgSync.mgFragmentSyncEdPass.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                bindingFgSync.mgFragmentSyncEdPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

        isCheckBox = false;

        var str = getString(R.string.mg_fragment_term_condition)
        SDKManager.baseConfigModel?.let {

            str = str.replace(Regex("_URL_"),SDKManager.baseConfigModel!!.urlPolicy)

        }
        bindingFgSync.mgFragmentLayoutSyncTnc.mgFragmentLayoutTermCondition.text= Html.fromHtml(str)
        bindingFgSync.mgFragmentLayoutSyncTnc.mgFragmentLayoutTermCondition.movementMethod = LinkMovementMethod.getInstance()
        //bindingFgSync.mgFragmentLayoutSyncTnc.mgFragmentLayoutTermConditionCheckbox.isEnabled = isCheckBox
        bindingFgSync.mgFragmentLayoutSyncTnc.mgFragmentLayoutTermConditionCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->  }

        bindingFgSync.mgFragmentSyncIvEyeConfirm.setOnClickListener {
            isShowConfirmPassSync = !isShowConfirmPassSync
            if (isShowConfirmPassSync) {
                bindingFgSync.mgFragmentSyncIvEyeConfirm.setImageResource(R.drawable.visibility)
                bindingFgSync.mgFragmentSyncEdConfirmpass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                bindingFgSync.mgFragmentSyncEdConfirmpass.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                bindingFgSync.mgFragmentSyncIvEyeConfirm.setImageResource(R.drawable.invisible)
                bindingFgSync.mgFragmentSyncEdConfirmpass.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                bindingFgSync.mgFragmentSyncEdConfirmpass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
        updateViewSync()
    }


    private fun updateViewSync()
    {

        bindingFgSync.mgFragmentSyncEdId?.visibility = View.GONE
        bindingFgSync.mgFragmentSyncEdDateId?.visibility = View.GONE
        bindingFgSync.mgFragmentSyncEdDob?.visibility = View.GONE
        bindingFgSync.mgFragmentSyncEdAddress?.visibility = View.GONE
        bindingFgSync.mgFragmentSyncEdFullname?.visibility = View.GONE
        bindingFgSync.mgFragmentSyncRgGender?.visibility = View.GONE
        return;
        var isShow : Int = 0
        SDKManager.baseConfigModel?.let {
            isShow = SDKManager.baseConfigModel!!.isRequireMoreInfo

        }

        if(bindingFgSync.mgFragmentSyncEdPhone == null)
            return
        if (isShow == 1) {

            bindingFgSync.mgFragmentSyncEdId?.visibility = View.VISIBLE
            bindingFgSync.mgFragmentSyncEdDateId?.visibility = View.VISIBLE
            bindingFgSync.mgFragmentSyncEdDob?.visibility = View.VISIBLE
            bindingFgSync.mgFragmentSyncEdAddress?.visibility = View.VISIBLE
            bindingFgSync.mgFragmentSyncEdFullname?.visibility = View.VISIBLE
            bindingFgSync.mgFragmentSyncRgGender?.visibility = View.VISIBLE
        } else {
            bindingFgSync.mgFragmentSyncEdId?.visibility = View.GONE
            bindingFgSync.mgFragmentSyncEdDateId?.visibility = View.GONE
            bindingFgSync.mgFragmentSyncEdDob?.visibility = View.GONE
            bindingFgSync.mgFragmentSyncEdAddress?.visibility = View.GONE
            bindingFgSync.mgFragmentSyncEdFullname?.visibility = View.GONE
            bindingFgSync.mgFragmentSyncRgGender?.visibility = View.GONE
        }
        if(isShow == 0)
        {
            //showHideInfo(false)
            bindingFgSync.mgFragmentSyncEdPhone.hint = getString(R.string.mg_text_hint_phone_optional)
            bindingFgSync.mgFragmentSyncEdEmail.hint = getString(R.string.mg_text_hint_email_optional)
        }
        else
        {
            //showHideInfo(true)
            bindingFgSync.mgFragmentSyncEdPhone.hint = getString(R.string.mg_text_hint_phone)
            bindingFgSync.mgFragmentSyncEdEmail.hint = getString(R.string.mg_text_hint_email)
        }
    }

    private fun updateViewRegister()
    {

        bindingFgRegister.mgFragmentRegisterEdId?.visibility = View.GONE
        bindingFgRegister.mgFragmentRegisterEdDateId?.visibility = View.GONE
        bindingFgRegister.mgFragmentRegisterEdDob?.visibility = View.GONE
        bindingFgRegister.mgFragmentRegisterEdAddress?.visibility = View.GONE
        bindingFgRegister.mgFragmentRegisterEdFullname?.visibility = View.GONE
        bindingFgRegister.mgFragmentRegisterRgGender?.visibility = View.GONE
        return;
        var isShow : Int = 0
        SDKManager.baseConfigModel?.let {
            isShow = SDKManager.baseConfigModel!!.isRequireMoreInfo

        }
        if(bindingFgRegister.mgFragmentRegisterEdPhone == null)
            return
        if (isShow == 1) {

            bindingFgRegister.mgFragmentRegisterEdId?.visibility = View.VISIBLE
            bindingFgRegister.mgFragmentRegisterEdDateId?.visibility = View.VISIBLE
            bindingFgRegister.mgFragmentRegisterEdDob?.visibility = View.VISIBLE
            bindingFgRegister.mgFragmentRegisterEdAddress?.visibility = View.VISIBLE
            bindingFgRegister.mgFragmentRegisterEdFullname?.visibility = View.VISIBLE
            bindingFgRegister.mgFragmentRegisterRgGender?.visibility = View.VISIBLE
        } else {
            bindingFgRegister.mgFragmentRegisterEdId?.visibility = View.GONE
            bindingFgRegister.mgFragmentRegisterEdDateId?.visibility = View.GONE
            bindingFgRegister.mgFragmentRegisterEdDob?.visibility = View.GONE
            bindingFgRegister.mgFragmentRegisterEdAddress?.visibility = View.GONE
            bindingFgRegister.mgFragmentRegisterEdFullname?.visibility = View.GONE
            bindingFgRegister.mgFragmentRegisterRgGender?.visibility = View.GONE
        }
        if(isShow == 0)
        {
            //showHideInfo(false)
            bindingFgRegister.mgFragmentRegisterEdPhone.hint = getString(R.string.mg_text_hint_phone_optional)
            bindingFgRegister.mgFragmentRegisterEdEmail.hint = getString(R.string.mg_text_hint_email_optional)
        }
        else
        {
            //showHideInfo(true)
            bindingFgRegister.mgFragmentRegisterEdPhone.hint = getString(R.string.mg_text_hint_phone)
            bindingFgRegister.mgFragmentRegisterEdEmail.hint = getString(R.string.mg_text_hint_email)
        }
    }
    private fun validateRegister() : Boolean{
        var result : Boolean = true

        var isShow : Int = 0
//        SDKManager.baseConfigModel?.let {
//            isShow = SDKManager.baseConfigModel!!.isRequireMoreInfo
//
//        }
        val json = JSONObject()
        if(bindingFgRegister.mgFragmentRegisterEdAddress.visibility == View.VISIBLE ) {
            if (ValidateSDK.showCheckTextEmpty(
                    context,
                    bindingFgRegister.mgFragmentRegisterEdAddress,
                    getString(R.string.mg_text_error_empty_address)
                )
            ) {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_address))
            }

        }
        if(bindingFgRegister.mgFragmentRegisterEdDateId.visibility == View.VISIBLE )
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdDateId,getString(R.string.mg_text_error_empty_date_id))) {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_date_id))
            }
            else
                if(ValidateSDK.invalidDate(bindingFgRegister.mgFragmentRegisterEdDateId.text.toString())==null)
                {
                    bindingFgRegister.mgFragmentRegisterEdDateId.setError(getString(R.string.mg_text_invalid_date_id))
                    showToast(getString(R.string.mg_text_invalid_date_id))
                    json.put("error",getString(R.string.mg_text_invalid_date_id))
                    result = false
                }
        }

        if(bindingFgRegister.mgFragmentRegisterEdId.visibility == View.VISIBLE )
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdId,getString(R.string.mg_text_error_empty_id))) {
                json.put("error",getString(R.string.mg_text_error_empty_id))
                result = false
            }
//            else
//                if(ValidateSDK.invalidDate(bindingFgRegister.mgFragmentRegisterEdId.text.toString())==null)
//                {
//                    bindingFgRegister.mgFragmentRegisterEdDateId.setError(getString(R.string.migamesdk_text_invalid_id))
//                    showToast(getString(R.string.migamesdk_text_invalid_id))
//                    result = false
//                }
        }

        if(bindingFgRegister.mgFragmentRegisterEdFullname.visibility == View.VISIBLE)
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdFullname,getString(R.string.mg_text_error_empty_fullname)))
            {
                json.put("error",getString(R.string.mg_text_error_empty_fullname))
                result = false
            }
            else
            {
                var error = ""
                val fullName = bindingFgRegister.mgFragmentRegisterEdFullname.text.toString()
                if(fullName.length < 6 || fullName.length>60) {
                    error = getString(R.string.mg_text_error_invalid_fullname)
                    result = false
                }
                if(!error.isNullOrEmpty()) {
                    ValidateSDK.setError(bindingFgRegister.mgFragmentRegisterEdFullname, error)
                    result = false
                    json.put("error",error)
                }
            }

        }

        if(bindingFgRegister.mgFragmentRegisterEdDob.visibility == View.VISIBLE)
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdDob,getString(R.string.mg_text_error_empty_dob)))
            {
//                bindingFgRegister.mgFragmentRegisterEdDob.setPadding(bindingFgRegister.mgFragmentRegisterEdDob.paddingLeft,bindingFgRegister.mgFragmentRegisterEdDob.paddingTop,ValidateSDK.convertDpToPixel(50.0f,context!!).toInt(),bindingFgRegister.mgFragmentRegisterEdDob.paddingRight)
                json.put("error",getString(R.string.mg_text_error_empty_dob))
                result = false
            }else
                if(ValidateSDK.invalidDate(bindingFgRegister.mgFragmentRegisterEdDob.text.toString())==null)
                {
//                    bindingFgRegister.mgFragmentRegisterEdDob.setPadding(bindingFgRegister.mgFragmentRegisterEdDob.paddingLeft,bindingFgRegister.mgFragmentRegisterEdDob.paddingTop,ValidateSDK.convertDpToPixel(50.0f,context!!).toInt(),bindingFgRegister.mgFragmentRegisterEdDob.paddingRight)
                    bindingFgRegister.mgFragmentRegisterEdDob.setError(getString(R.string.mg_text_invalid_dob))
                    json.put("error",getString(R.string.mg_text_invalid_dob))
                    showToast(getString(R.string.mg_text_invalid_dob))
                    result = false
                }
        }
        if(isShow==1 || isShow == 2)
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdEmail,getString(R.string.mg_text_error_empty_email)))
            {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_email))
            }
        if(!ValidateSDK.isStringNullOrEmpty(bindingFgRegister.mgFragmentRegisterEdEmail.text.toString()))
            if(!ValidateSDK.isEmailAddress(bindingFgRegister.mgFragmentRegisterEdEmail.getText().toString()))
            {
                bindingFgRegister.mgFragmentRegisterEdEmail.setError(getString(R.string.mg_text_invalid_email))
                showToast(getString(R.string.mg_text_invalid_email))
                result = false
                json.put("error",getString(R.string.mg_text_invalid_email))
            }
        if(isShow==1 || isShow == 2)
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdPhone,getString(R.string.mg_text_error_empty_phone)))
            {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_phone))
            }
        if(!ValidateSDK.isStringNullOrEmpty(bindingFgRegister.mgFragmentRegisterEdPhone.text.toString()))
            if(ValidateSDK.showCheckPhoneNumber(context,bindingFgRegister.mgFragmentRegisterEdPhone,bindingFgRegister.mgFragmentRegisterEdPhone.text.toString(),getString(R.string.mg_invalid_phone))) {
                result = false
                json.put("error",getString(R.string.mg_invalid_phone))
            }

        if(!bindingFgRegister.mgFragmentRegisterEdPass.text.toString().equals(bindingFgRegister.mgFragmentRegisterEdConfirmpass.text.toString()))
        {
            bindingFgRegister.mgFragmentRegisterEdConfirmpass.setError(getString(R.string.mg_text_invalid_confirm_pass));
            showToast(getString(R.string.mg_text_invalid_confirm_pass))
            json.put("error",getString(R.string.mg_text_invalid_confirm_pass))
            result = false;
        }
        if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdPass,getString(R.string.mg_text_error_empty_pass)))
        {
            result = false
            json.put("error",getString(R.string.mg_text_error_empty_pass))
        }else
        {
            var error: String = ""
            val pass = bindingFgRegister.mgFragmentRegisterEdPass.text.toString()
            if(pass.length < 6 || pass.length>32) {
                error = getString(R.string.mg_text_error_invalid_pass)
            }
            if(!error.isNullOrEmpty()) {
                ValidateSDK.setError(bindingFgRegister.mgFragmentRegisterEdPass, error)
                result = false
                json.put("error",error)
            }
        }




        if(ValidateSDK.showCheckTextEmpty(context,bindingFgRegister.mgFragmentRegisterEdAccount,getString(R.string.mg_text_error_empty_account)))
        {
            result = false
            json.put("error",getString(R.string.mg_text_error_empty_account))
        }else {
            var error: String = ""
            val accountName = bindingFgRegister.mgFragmentRegisterEdAccount.text.toString()
            if(accountName.length < 6 || accountName.length>32) {
                error = getString(R.string.mg_text_error_invalid_account1)
            }else {
                accountName.forEach { char ->
                    if ((char !in 'a'..'z') && (char !in '0'..'9') && char != '.' && char != '@' && (char !in 'A'..'Z'))
                    {
                        error = getString(R.string.mg_text_error_invalid_account2)
                        return@forEach
                    }
                }
            }

            if(!error.isNullOrEmpty()) {
                ValidateSDK.setError(bindingFgRegister.mgFragmentRegisterEdAccount, error)
                json.put("error",error)
                result = false
            }
        }
        if(result)
        {
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_register_validate_success),json)
        }else{
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_register_validate_failed),json)
        }
        return result
    }

    private fun validateSyncAccount() : Boolean{
        var result : Boolean = true

        var isShow : Int = 0
//        SDKManager.baseConfigModel?.let {
//            isShow = SDKManager.baseConfigModel!!.isRequireMoreInfo
//
//        }
        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_sync_validate),null)
        val json = JSONObject()
        if(bindingFgSync.mgFragmentSyncEdAddress.visibility == View.VISIBLE ) {
            if (ValidateSDK.showCheckTextEmpty(
                    context,
                    bindingFgSync.mgFragmentSyncEdAddress,
                    getString(R.string.mg_text_error_empty_address)
                )
            ) {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_address))
            }

        }
        if(bindingFgSync.mgFragmentSyncEdDateId.visibility == View.VISIBLE )
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdDateId,getString(R.string.mg_text_error_empty_date_id))) {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_date_id))
            }
            else
                if(ValidateSDK.invalidDate(bindingFgSync.mgFragmentSyncEdDateId.text.toString())==null)
                {
                    bindingFgSync.mgFragmentSyncEdDateId.setError(getString(R.string.mg_text_invalid_date_id))
                    showToast(getString(R.string.mg_text_invalid_date_id))
                    json.put("error",getString(R.string.mg_text_invalid_date_id))
                    result = false
                }
        }

        if(bindingFgSync.mgFragmentSyncEdId.visibility == View.VISIBLE )
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdId,getString(R.string.mg_text_error_empty_id))) {
                json.put("error",getString(R.string.mg_text_error_empty_id))
                result = false
            }
//            else
//                if(ValidateSDK.invalidDate(bindingFgSync.mgFragmentSyncEdId.text.toString())==null)
//                {
//                    bindingFgSync.mgFragmentSyncEdDateId.setError(getString(R.string.migamesdk_text_invalid_id))
//                    showToast(getString(R.string.migamesdk_text_invalid_id))
//                    result = false
//                }
        }

        if(bindingFgSync.mgFragmentSyncEdFullname.visibility == View.VISIBLE)
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdFullname,getString(R.string.mg_text_error_empty_fullname)))
            {
                json.put("error",getString(R.string.mg_text_error_empty_fullname))
                result = false
            }
            else
            {
                var error = ""
                val fullName = bindingFgSync.mgFragmentSyncEdFullname.text.toString()
                if(fullName.length < 6 || fullName.length>60) {
                    error = getString(R.string.mg_text_error_invalid_fullname)
                    result = false
                }
                if(!error.isNullOrEmpty()) {
                    ValidateSDK.setError(bindingFgSync.mgFragmentSyncEdFullname, error)
                    result = false
                    json.put("error",error)
                }
            }

        }

        if(bindingFgSync.mgFragmentSyncEdDob.visibility == View.VISIBLE)
        {
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdDob,getString(R.string.mg_text_error_empty_dob)))
            {
//                bindingFgSync.mgFragmentSyncEdDob.setPadding(bindingFgSync.mgFragmentSyncEdDob.paddingLeft,bindingFgSync.mgFragmentSyncEdDob.paddingTop,ValidateSDK.convertDpToPixel(50.0f,context!!).toInt(),bindingFgSync.mgFragmentSyncEdDob.paddingRight)
                json.put("error",getString(R.string.mg_text_error_empty_dob))
                result = false
            }else
                if(ValidateSDK.invalidDate(bindingFgSync.mgFragmentSyncEdDob.text.toString())==null)
                {
//                    bindingFgSync.mgFragmentSyncEdDob.setPadding(bindingFgSync.mgFragmentSyncEdDob.paddingLeft,bindingFgSync.mgFragmentSyncEdDob.paddingTop,ValidateSDK.convertDpToPixel(50.0f,context!!).toInt(),bindingFgSync.mgFragmentSyncEdDob.paddingRight)
                    bindingFgSync.mgFragmentSyncEdDob.setError(getString(R.string.mg_text_invalid_dob))
                    json.put("error",getString(R.string.mg_text_invalid_dob))
                    showToast(getString(R.string.mg_text_invalid_dob))
                    result = false
                }
        }
        if(isShow==1 || isShow == 2)
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdEmail,getString(R.string.mg_text_error_empty_email)))
            {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_email))
            }
        if(!ValidateSDK.isStringNullOrEmpty(bindingFgSync.mgFragmentSyncEdEmail.text.toString()))
            if(!ValidateSDK.isEmailAddress(bindingFgSync.mgFragmentSyncEdEmail.getText().toString()))
            {
                bindingFgSync.mgFragmentSyncEdEmail.setError(getString(R.string.mg_text_invalid_email))
                showToast(getString(R.string.mg_text_invalid_email))
                result = false
                json.put("error",getString(R.string.mg_text_invalid_email))
            }
        if(isShow==1 || isShow == 2)
            if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdPhone,getString(R.string.mg_text_error_empty_phone)))
            {
                result = false
                json.put("error",getString(R.string.mg_text_error_empty_phone))
            }
        if(!ValidateSDK.isStringNullOrEmpty(bindingFgSync.mgFragmentSyncEdPhone.text.toString()))
            if(ValidateSDK.showCheckPhoneNumber(context,bindingFgSync.mgFragmentSyncEdPhone,bindingFgSync.mgFragmentSyncEdPhone.text.toString(),getString(R.string.mg_invalid_phone))) {
                result = false
                json.put("error",getString(R.string.mg_invalid_phone))
            }

        if(!bindingFgSync.mgFragmentSyncEdPass.text.toString().equals(bindingFgSync.mgFragmentSyncEdConfirmpass.text.toString()))
        {
            bindingFgSync.mgFragmentSyncEdConfirmpass.setError(getString(R.string.mg_text_invalid_confirm_pass));
            showToast(getString(R.string.mg_text_invalid_confirm_pass))
            json.put("error",getString(R.string.mg_text_invalid_confirm_pass))
            result = false;
        }
        if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdPass,getString(R.string.mg_text_error_empty_pass)))
        {
            result = false
            json.put("error",getString(R.string.mg_text_error_empty_pass))
        }else
        {
            var error: String = ""
            val pass = bindingFgSync.mgFragmentSyncEdPass.text.toString()
            if(pass.length < 6 || pass.length>32) {
                error = getString(R.string.mg_text_error_invalid_pass)
            }
            if(!error.isNullOrEmpty()) {
                ValidateSDK.setError(bindingFgSync.mgFragmentSyncEdPass, error)
                result = false
                json.put("error",error)
            }
        }




        if(ValidateSDK.showCheckTextEmpty(context,bindingFgSync.mgFragmentSyncEdAccount,getString(R.string.mg_text_error_empty_account)))
        {
            result = false
            json.put("error",getString(R.string.mg_text_error_empty_account))
        }else {
            var error: String = ""
            val accountName = bindingFgSync.mgFragmentSyncEdAccount.text.toString()
            if(accountName.length < 6 || accountName.length>32) {
                error = getString(R.string.mg_text_error_invalid_account1)
            }else {
                accountName.forEach { char ->
                    if ((char !in 'a'..'z') && (char !in '0'..'9') && char != '.' && char != '@' && (char !in 'A'..'Z'))
                    {
                        error = getString(R.string.mg_text_error_invalid_account2)
                        return@forEach
                    }
                }
            }

            if(!error.isNullOrEmpty()) {
                ValidateSDK.setError(bindingFgSync.mgFragmentSyncEdAccount, error)
                json.put("error",error)
                result = false
            }
        }
        if(result)
        {
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_register_validate_success),json)
        }else{
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_register_validate_failed),json)
        }
        return result
    }


    fun hideMainMenu()
    {
        binding.activityMigameLlContainer.visibility = View.GONE

        binding.activityMgLayoutMainAccount.root.visibility = View.GONE

        binding.activityMgLayoutLogin.root.visibility = View.GONE
        binding.activityMgLayoutRegister.root.visibility = View.GONE

        binding.activityMgLayoutForgetPass.root.visibility = View.GONE
        binding.activityMgLayoutSyncAccount.root.visibility = View.GONE
    }
    fun showMainMenu()
    {
        if(hideMainMenu) return;

        binding.activityMigameLlContainer.visibility = View.VISIBLE
        binding.activityMgLayoutMainAccount.root.visibility = View.GONE

        binding.activityMgLayoutLogin.root.visibility = View.VISIBLE
        binding.activityMgLayoutRegister.root.visibility = View.GONE

        binding.activityMgLayoutForgetPass.root.visibility = View.GONE
        binding.activityMgLayoutSyncAccount.root.visibility = View.GONE
    }
    fun showLogin()
    {
        binding.activityMigameLlContainer.visibility = View.VISIBLE
        binding.activityMgLayoutMainAccount.root.visibility = View.GONE
        binding.activityMgLayoutLogin.root.visibility = View.VISIBLE
        binding.activityMgLayoutRegister.root.visibility = View.GONE
        binding.activityMgLayoutForgetPass.root.visibility = View.GONE
        binding.activityMgLayoutSyncAccount.root.visibility = View.GONE

    }
    fun showRegister()
    {
        binding.activityMigameLlContainer.visibility = View.VISIBLE
        binding.activityMgLayoutMainAccount.root.visibility = View.GONE
        binding.activityMgLayoutLogin.root.visibility = View.GONE
        binding.activityMgLayoutRegister.root.visibility = View.VISIBLE
        binding.activityMgLayoutForgetPass.root.visibility = View.GONE
        binding.activityMgLayoutSyncAccount.root.visibility = View.GONE
    }

    fun showForgetPass()
    {

        binding.activityMigameLlContainer.visibility = View.VISIBLE
        binding.activityMgLayoutMainAccount.root.visibility = View.GONE
        binding.activityMgLayoutLogin.root.visibility = View.GONE
        binding.activityMgLayoutForgetPass.root.visibility = View.VISIBLE
        binding.activityMgLayoutSyncAccount.root.visibility = View.GONE
    }
    fun showSynAccount()
    {
        binding.activityMigameLlContainer.visibility = View.VISIBLE
        binding.activityMgLayoutMainAccount.root.visibility = View.GONE
        binding.activityMgLayoutLogin.root.visibility = View.GONE
        binding.activityMgLayoutForgetPass.root.visibility = View.GONE
        binding.activityMgLayoutSyncAccount.root.visibility = View.VISIBLE
    }

    override  fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() === 0) {
            // do something on back pressed.
            onBackPressed()

            true
        } else super.onKeyDown(keyCode, event)

        ImmersiveControl.onKeyDown(this,keyCode);
    }
    override fun onBackPressed() {
        if(isShowLoginScreen()) {
            showMainMenu()
            return
        }
        if(isShowRegisterScreen()) {
            showLogin()
            return
        }
        if(isShowForgetPass())
        {
            showLogin()
            return
        }
        if(isShowSync())
        {
            synSkip()
            return
        }


//        super.onBackPressed()
    }
    var stateProcess : String  = Constants.ACTION_LOGIN
    private fun onCLickLoginFacebook()
    {



            this.nextState(STATE_LOGIN_FB)
        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_click_login_fb),null)

    }
    private fun onCLickLoginGoogle() {



            this.nextState(STATE_LOGIN_GG)
        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_click_login_gg),null)

    }


    val STATE_INIT = "INIT"
    val STATE_DONE = "DONE"
    val STATE_LOGIN_FB = "LOGIN_FB"
    val STATE_LOGIN_GG = "LOGIN_GG"
    val STATE_LOGIN_QP = "LOGIN_QP"
    val STATE_LOGIN_MG = "LOGIN_ACCOUNT"
    val STATE_REGISTER = "REGISTER_USER"
    val STATE_SYNC = "SYNC_USER"
    val STATE_VERIFY_ACCESSTOKEN = "VERIFY_ACCESSTOKEN"
    val STATE_LOGOUT = "LOGOUT"
    val STATE_PAYMENT = "PAYMENT"
    val STATE_PASS_RECOVERY = "PASS_RECOVERY"
    var currentState = STATE_LOGIN_MG//STATE_INIT
    var nextState = STATE_DONE


    private fun showToast(str:String?)
    {
        var message = getString(R.string.mg_text_unknown_error)
        if(str!=null && !TextUtils.isEmpty(str))
            message = str
        runOnUiThread {
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }

    }
    private fun showMessageError(e: String?)
    {
        var _message = getString(R.string.mg_text_unknown_error)
        if(e!= null && !TextUtils.isEmpty(e))
        {
            _message = e
        }
        this.showMessage {
            title = getString(R.string.mg_text_message_title)
            message = _message
            listener = object : AppMessage.EventMessage {
                override fun onClickConfirm() {
                    super.onClickConfirm()
                    if(hideMainMenu)
                        closeActivity()
                }
            }
        }
    }
    private fun nextState(state:String)
    {
        if(!currentState.equals(STATE_DONE))
            return
        nextState = state
        if(SDKManager.baseConfigModel == null)
        {
            getConfig()
        }
        else
        {
            nextState()
        }
    }
    private fun nextState()
    {

        when(nextState)
        {
            STATE_LOGIN_FB->{
                currentState = STATE_LOGIN_FB
                nextState = STATE_DONE
                this?.let{
                   // LoginManager.getInstance().logOut();


                    LoginManager.getInstance().retrieveLoginStatus(this,
                            object: LoginStatusCallback {
                        override fun onCompleted(accessToken: AccessToken ) {

                            hideLoading()
                            currentState = STATE_DONE

                            //var accessToken = com.facebook.AccessToken.getCurrentAccessToken();
                            //Log.d("PQT Debug", "Facebook oncancel triggered");
                            //   accessToken?.token
                            //accessToken.user_id

                            if (accessToken != null) {
                               // Log.d("PQT Debug", "Facebook accesstoken still vaild");
                                loginSDKFacebook(accessToken!!)
                                // User was previously logged in, can log them in directly here. // If this callback is called, a popup notification appears that says // "Logged in as <User Name>"
                            } else {
                            //    Log.d("PQT Debug", "facebook token invalid - user cancel login");
                                showToast(getString(R.string.mg_text_login_facebook_cancel))
                          //      Log.d("PQT Debug", "show error");
                            }
                        }
                        override fun onFailure()
                         {
                             LoginManager.getInstance().logInWithReadPermissions(this@MGActivity, callbackManagerLoginFB,Arrays.asList("email", "public_profile"))
                        // No access token could be retrieved for the user
                         }
                         override fun onError(exception:Exception) {
                        // An error occurred
                             currentState = STATE_DONE
//                    showToast(getString(R.string.mg_text_login_fb_failed))
                             showMessageError(getString(R.string.mg_text_login_facebook_error))
                             hideLoading()
                             Constants.showDataLog("Login FB",": ${exception.message}")
                             }
                         });

                }

            }
            STATE_LOGIN_GG->{
                currentState = STATE_LOGIN_GG
                nextState = STATE_DONE
                mGoogleSignInClient.signOut()
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            STATE_LOGIN_QP->{
                currentState = STATE_LOGIN_QP
                nextState = STATE_DONE
                getSDKQuickPlay()
            }
            STATE_REGISTER->{
                currentState = STATE_REGISTER
                nextState = STATE_DONE
                registerMGSDKAccount()
            }
            STATE_SYNC->{
                currentState = STATE_SYNC
                nextState = STATE_DONE
                syncUserWithDevice()
            }
            STATE_LOGIN_MG->{
                currentState = STATE_LOGIN_MG
                nextState = STATE_DONE
                getMGAccountLogin()
            }
            STATE_LOGOUT -> {
                currentState = STATE_LOGOUT
                nextState = STATE_DONE
                val user = getUser()
                if(user!=null && !TextUtils.isEmpty(user.accessToken))
                {
                    logout(user!!.accessToken)
                }else
                {
                    if(hideMainMenu)
                        closeActivity()
                }

            }
            STATE_VERIFY_ACCESSTOKEN->{
                currentState = STATE_VERIFY_ACCESSTOKEN

                nextState = STATE_DONE
                val user = getUser()
                if(user!=null && !TextUtils.isEmpty(user.accessToken))
                {
                    when(stateProcess) {
                        STATE_PAYMENT -> {
                            nextState = STATE_PAYMENT
                        }
                    }

                    verifyAccessToken(user!!.accessToken)
                }

            }
            STATE_PASS_RECOVERY->{
                currentState = STATE_PASS_RECOVERY
                nextState = STATE_DONE
                processPasswordRecovery()
            }
            STATE_PAYMENT -> {
                binding.activityMigameLlContainer.visibility = View.GONE
                initBillingInApp()
            }
        }
    }
    private fun syncUserWithDevice() {
        val user = getUser()
        if (NetworkUtils.isNetworkConnected(context!!)) {
            SDKManager.baseConfigModel?.let {
                showLoading()
                val dateTime = SDKParams.getCurrentTime()


                    val userName = bindingFgSync.mgFragmentSyncEdAccount.text.toString()
                    val pass = Encrypt.md5(bindingFgSync.mgFragmentSyncEdPass.text.toString())
                    val primaryMobile = bindingFgSync.mgFragmentSyncEdPhone.text.toString()
                    val email = bindingFgSync.mgFragmentSyncEdEmail.text.toString()

                    val fullname = bindingFgSync.mgFragmentSyncEdFullname.text.toString()
                    val dob = bindingFgSync.mgFragmentSyncEdDob.text.toString()
                    val cardid = bindingFgSync.mgFragmentSyncEdId.text.toString()
                    val dateCard = bindingFgSync.mgFragmentSyncEdDateId.text.toString()
                    val address = bindingFgSync.mgFragmentSyncEdAddress.text.toString()

                    val gender = getGender()
                    val deviceId = Device.getDeviceID(context!!)



                SynQuickDeviceApi.getSynQuickDevice(
                        typeSyn = user?.accountType.getValue(),

                        deviceID = deviceId,
                        userName = userName,
                        password = pass,
                        primaryMobile = primaryMobile,
                        displayName = fullname,
                        email = email,
                        dob = dob,
                        cardID = cardid,
                        dateCard = dateCard,
                        address = address,
                        gender = gender,
                        accessToken = user?.accessToken.getValue(),
                        linkAPI = it.User_SynUser,
                        time = dateTime,
                        sign = Encrypt.getHashCodeSyncAccount(
                            userName,
                            email,
                            pass,
                            deviceId,
                            dateTime,
                            SDKManager.getSECRET_KEY()
                        )
                    ) { user, e ->
                        hideLoading()
                        currentState = STATE_DONE
                        nextState()
                        if (user != null) {
                            user.isSave = true

                            synSuccess(user)
//                            authListener?.onLoginSuccess(user)
//                            authListener?.onSyncSuccess(user)
                            val json = JSONObject()
                            json.put("username", user.userName)
                            json.put("userid", user.userId)
                           // if(context!=null)
                            TrackingManager.trackEventCount(
                                context?._getString(R.string.mg_event_sync_success),
                                    json
                                )
                        } else {
                            synFailed(e?.message)
//                            showToast(e?.message)
                            val json = JSONObject()
                            json.put("error", e?.message)
                            if(context!=null)
                                TrackingManager.trackEventCount(
                                    context?._getString(R.string.mg_event_sync_failed),
                                    json
                                )

                        }
                    }
                }

        } else {
            hideLoading()
            currentState = STATE_DONE
            showToast(getString(R.string.mg_text_no_network))
        }

    }
    private fun processPasswordRecovery()
    {


        context?.let {

            if (NetworkUtils.isNetworkConnected(context!!)) {
                showLoading()
                val dateTime = SDKParams.getCurrentTime()
                SDKManager.baseConfigModel?.let {
                    val email = bindingFgForgetPass.mgFragmentForgetPassEdEmail.text.toString()
                    ForgotPasswordApi.getForgotPassword(
                        email = email,
                        deviceID = Device.getDeviceID(context!!),
                        linkAPI = it.User_LostPassword,
                        time = dateTime,

                        sign = Encrypt.getHashCodeForgotPassword(
                            email,
                            dateTime,
                            SDKManager.getSECRET_KEY()
                        )
                    )
                    { result, e ->
                        hideLoading()
                        currentState = STATE_DONE
                        //if (result == true)
                          //  showToast(getString(R.string.mg_text_forget_pass_recovery_success))
                        //currentState = STATE_COMPLETE
                        //actionState()
                        //showToast(e?.message)
                        //showAutoDialog(getString(R.string.mgsdk_text_title_forget_pass),e?.message)
//                        showToast(e?.message)

                        showMessageError(e?.message)
                    }
                }
            }else{
                currentState = STATE_DONE
                showToast(getString(R.string.mg_text_no_network))
            }
        }

    }
    private fun logout(accessToken: String) {


        //logout fb account
        LoginManager.getInstance().logOut();
        context?.let {
            if (NetworkUtils.isNetworkConnected(context!!)) {
                showLoading()
                val dateTime = SDKParams.getCurrentTime()
                SDKManager.baseConfigModel?.let {
                    AuthenApi.getLogoutByAccessToken(
                        accessToken,
                        Device.getDeviceID(context!!),
                        it.User_Logout,
                        dateTime,
                        Encrypt.getHashCodeLogout(
                            accessToken,
                            dateTime,
                            SDKManager.getSECRET_KEY()
                        )
                    ) { result, e ->

                        hideLoading()
                        currentState = STATE_DONE
                        if (result == true) {
                            logoutSuccess()
                        } else {

                            logoutFailed(accessToken, e?.message)
                        }
                    }
                }
            } else {
                logoutFailed(accessToken, getString(R.string.mg_text_no_network))

                currentState = STATE_DONE
                showToast(getString(R.string.mg_text_no_network))
                if(hideMainMenu) {
                    closeActivity()
                }else{

                }
            }
        }
    }


    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callbackManagerLoginFB: CallbackManager

    private fun initGoogle() {
//        System.out.println("songsong::"+MiGameSDK.getAppGoogleKey())
        //songpq remove
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SDKManager.getGG_KEY())
            .requestEmail()
            .build()

        mGoogleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!



    }

    private fun initTikTok()
    {

    }
    private fun initFacebook() {
        callbackManagerLoginFB = CallbackManager.Factory.create()
        //songpq
        LoginManager.getInstance().registerCallback(callbackManagerLoginFB,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    loginResult.let {
                        showToast(getString(R.string.mg_text_login_facebook_success))
                        loginSDKFacebook(it.accessToken)
                    }
                }

                override fun onCancel() {

                    hideLoading()
                    currentState = STATE_DONE

                    var accessToken = com.facebook.AccessToken.getCurrentAccessToken();
                   // Log.d("PQT Debug", "Facebook oncancel triggered");
                 //   accessToken?.token
                    //accessToken.user_id

                    if ( accessToken != null) {
                       // Log.d("PQT Debug", "Facebook accesstoken still vaild");
                        loginSDKFacebook(accessToken!!)
                    } else {
                     //   Log.d("PQT Debug", "facebook token invalid - user cancel login");
                        showToast(getString(R.string.mg_text_login_facebook_cancel))
                        //Log.d("PQT Debug","show error");
                    }
                }

                override fun onError(exception: FacebookException) {
                    currentState = STATE_DONE
//                    showToast(getString(R.string.mg_text_login_fb_failed))
                    showMessageError(getString(R.string.mg_text_login_facebook_error))
                    hideLoading()
                    Constants.showDataLog("Login FB",": ${exception.message}")
                }
            })
    }


        private fun loginSDKFacebook(fbAccessToken:  com.facebook.AccessToken) {
        context?.let {
            deviceID = Device.getDeviceID(it)
        }

        SDKManager.baseConfigModel?.let {
            if(NetworkUtils.isNetworkConnected(context!!)) {
                showLoading()
                val dateTime = SDKParams.getCurrentTime()
                AuthenApi.getLoginFacebook(
                    fbAccessToken.userId,
                    fbAccessToken.token,
                    deviceID,
                    it.User_FaceBookLogin,
                    dateTime,
                    Encrypt.getHashCodeLoginFB(
                            fbAccessToken.token,
                            fbAccessToken.userId,
                        dateTime,
                        SDKManager.getSECRET_KEY()
                    )
                ) { user, e ->
                    hideLoading()
                    currentState = STATE_DONE
                    nextState()
                    if (user != null) {
                        loginSuccess(user)
                        TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_fb_success), null,null)
//                        loginEventSuccess(getString(R.string.migamesdk_event_login_fb_success))
                    } else {
                        loginFailed(e?.message)
//                        authListener?.onLoginFail(e?.message)
//                        loginEventFailed(getString(R.string.migamesdk_event_login_fb_failed),e?.message)
                        TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_fb_failed),null,e?.message)
                    }
                }
            }else
            {
                hideLoading()
                currentState = STATE_DONE
                showToast(getString(R.string.mg_text_no_network))
            }
        }

    }

    private fun handleSignInResult(data: Intent?) {
//        completedTask: Task<GoogleSignInAccount>
        //songpq
        try {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)

            val account = completedTask.getResult(ApiException::class.java)
            account?.let {
                showToast(getString(R.string.mg_text_login_google_success))
                getLoginSDKGoogle(it)
            }
        } catch (e: ApiException) {
            showToast(getString(R.string.mg_text_login_google_error))
            currentState = STATE_DONE
            e.printStackTrace()
            hideLoading()
            Constants.showDataLog(Constants.LOG_TAG, "signInResult:failed code=${e.statusCode}: ${e.message}")
        }
    }

    private fun getLoginSDKGoogle(account: GoogleSignInAccount) {
        context?.let {
            deviceID = Device.getDeviceID(it)
            SDKManager.baseConfigModel?.let {


                if (NetworkUtils.isNetworkConnected(context!!)) {
                    showLoading()
                    val dateTime = SDKParams.getCurrentTime()
                    AuthenApi.getLoginGoogle(
                        googleID = account.id.toString(),
                        googleTokenID = account.idToken.toString(),
                        deviceID = deviceID,
                        linkAPI = it.User_GoogleLogin,
                        time = dateTime,
                        sign = Encrypt.getHashCodeLoginGG(
                            account.idToken.toString(),
                            account.id.toString(),
                            dateTime,
                            SDKManager.getSECRET_KEY()
                        )
                    ) { user, e ->
                        hideLoading()
                        currentState = STATE_DONE
                        nextState()

                        if (user != null) {
                            loginSuccess(user)
//                            if (checkUserSynAccount(user) && AccountManager.getInstance()
//                                    .getStatusGoogle() == true
//                            )
//                                authListener?.needSyncAccount(user)
//                            else {
//                                authListener?.onLoginSuccess(user)
//                                AccountManager.getInstance().saveStatusGoogle(true)
//                            }
//                            loginEventSuccess(getString(R.string.migamesdk_event_login_gg_success))
                            TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_gg_success),null,null)
                        } else {
//                            authListener?.onLoginFail(e?.message)
//                            loginEventFailed(
//                                getString(R.string.migamesdk_event_login_fb_failed),
//                                e?.message
//                            )
                            TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_gg_failed),null,e?.message)
                            loginFailed(e?.message)
                        }
                    }
                } else {
                    hideLoading()
                    currentState = STATE_DONE
                    showToast(context?._getString(R.string.mg_text_no_network))
                }
            }
        }
    }

    var deviceID = ""

    private fun getSDKQuickPlay() {

        context?.let {
            deviceID = Device.getDeviceID(it)
        }
        SDKManager.baseConfigModel?.let {
            if(NetworkUtils.isNetworkConnected(context!!)) {
                showLoading()
                val dateTime = SDKParams.getCurrentTime()
                AuthenApi.getLoginQuickDevice(
                    deviceID = deviceID,
                    linkAPI = it.User_RegisterDevice,
                    time = dateTime,
                    sign = Encrypt.getHashCodeLoginQuickDevice(
                        deviceID,
                        dateTime,
                        SDKManager.getSECRET_KEY()
                    )
                ) { user, e ->
                    hideLoading()
//                authListener?.needSyncAccount()
                    currentState = STATE_DONE
                    nextState()
                    if (user != null) {

                        context?.let {

                            //songpq
//                            if (checkUserSynAccount(user) && AccountManager.getInstance().getStatusQuickPlay() == true)
//                                authListener?.needSyncAccount(user)
//                            else {
                            loginSuccess(user)
                            saveStatusQuickPlay(true)
//                            }


                        }
//                        loginEventSuccess(getString(R.string.migamesdk_event_login_qp_success))
                        TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_qp_success),null,null)
                    } else {
                        showMessageError(e?.message)
                        loginFailed(e?.message)
//                        authListener?.onLoginFail(e?.message)
//                        loginEventFailed(getString(R.string.migamesdk_event_login_gg_failed),e?.message)
                        TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_qp_failed),null,e?.message)
                    }
                }
            }
            else
            {
                hideLoading()
                currentState = STATE_DONE
                showToast(getString(R.string.mg_text_no_network))
            }
        }
    }

    var countLogin = 0
    private fun closeActivity()
    {
        currentState = STATE_DONE
        nextState = STATE_DONE
        finish()
    }
    private fun synSkip()
    {
        if(getUser()!=null) {

            if(checkForceUserSynAccount(getUser()))
            {
                showMessageError(getString(R.string.mg_text_force_sync_account))
            }else{
                SDKManager.authUserCallback?.onUserLoginSuccess(getUser()!!)
                SDKManager.authUserCallback?.onUserSyncCancel(getUser()!!)
                closeActivity()
            }
        }
        else
        {
            showMainMenu()
        }
    }
    private fun synSuccess(user:UserAccountModel)
    {
        saveUser(user)
        SDKManager.authUserCallback?.onUserLoginSuccess(user)
        SDKManager.authUserCallback?.onUserSyncSuccess(user)
        closeActivity()
    }
    private fun synFailed(e:String?)
    {
        showMessageError(e)
    }
    private fun checkForceUserSynAccount(user : UserAccountModel?) : Boolean
    {

        if (user == null || user.syncAccount == null)
            return false
        if( user.syncAccount!!.SyncQP == 2 || user.syncAccount!!.syncFB == 2 || user.syncAccount!!.synGG == 2)
        {
            return true
        }
        return false
    }
    private fun loginSuccess(user: UserAccountModel)
    {
        saveUser(user)
        accessToken = user.accessToken

        when(stateProcess) {
            Constants.ACTION_PAYMENT -> {

                nextState = STATE_PAYMENT
                nextState()
            }

            Constants.ACTION_LOGIN->
            {
                if(user?.syncAccount?.SyncQP==1 || user?.syncAccount?.synGG==1 || user?.syncAccount?.syncFB==1)
                {
                    showSynAccount()
					checkAndShowBanner(user)
                }
                else {
                    SDKManager.authUserCallback?.onUserLoginSuccess(user)
                    closeActivity()
					checkAndShowBanner(user)
                }
            }
        }

//        SDKManager.authUserCallback?.onUserLoginSuccess(user)
    }
    private fun logoutSuccess()
    {
        stateProcess = Constants.ACTION_LOGIN
        saveUser(null)
//        closeActivity()
        SDKManager.authUserCallback?.onUserLogoutSuccess()
        if(hideMainMenu )
            closeActivity()
    }
    private fun logoutFailed(accessToken:String,e:String?)
    {
        stateProcess = Constants.ACTION_LOGIN
//        closeActivity()
        SDKManager.authUserCallback?.onUserLogoutFail(e)
        if(hideMainMenu)
            closeActivity()
    }
    private fun loginFailed(e : String?)
    {
        showMessageError(e)
        SDKManager.authUserCallback?.onUserLoginFail(e)
    }


    private fun getGender() : String
    {
        var gender = ""
        if(bindingFgRegister.mgFragmentRegisterRdoMale.isChecked)
            gender = Constants.GENDER_MALE
        else
            if(bindingFgRegister.mgFragmentRegisterRdoMale.isChecked)
                gender = Constants.GENDER_FEMALE
            else
                if(bindingFgRegister.mgFragmentRegisterRdoMale.isChecked)
                    gender = Constants.GENDER_OTHER
        return gender
    }

    private fun registerMGSDKAccount() {

            if(NetworkUtils.isNetworkConnected(context!!)) {
                val dateTime = SDKParams.getCurrentTime()
                SDKManager.baseConfigModel?.let {
                    val userName = bindingFgRegister.mgFragmentRegisterEdAccount.text.toString()
                    val textPass = bindingFgRegister.mgFragmentRegisterEdPass.text.toString()
                    val pass = Encrypt.md5(bindingFgRegister.mgFragmentRegisterEdPass.text.toString())
                    val primaryMobile = bindingFgRegister.mgFragmentRegisterEdPhone.text.toString()
                    val email = bindingFgRegister.mgFragmentRegisterEdEmail.text.toString()
                    val dob = bindingFgRegister.mgFragmentRegisterEdDob.text.toString()
                    val cardid = bindingFgRegister.mgFragmentRegisterEdId.text.toString()
                    val dateCard = bindingFgRegister.mgFragmentRegisterEdDateId.text.toString()
                    val address = bindingFgRegister.mgFragmentRegisterEdAddress.text.toString()
                    val fullname = bindingFgRegister.mgFragmentRegisterEdFullname.text.toString()
                    val deviceID = Device.getDeviceID(context)
                    var gender = getGender()

                    AuthenApi.getRegister(
                        deviceID = deviceID,
                        userName = userName,
                        password = pass,
                        primaryMobile = primaryMobile,
                        displayName = fullname,
                        email = email,
                        dob = dob,
                        cardID = cardid,
                        dateCard = dateCard,
                        address = address,
                        gender = gender,
                        linkAPI = it.User_Register,
                        time = dateTime,
                        sign = Encrypt.getHashCodeRegister(
                            userName,
                            pass,
                            dateTime,
                            SDKManager.getSECRET_KEY()
                        )
                    ) { user, e ->
                        hideLoading()
                        currentState = STATE_DONE
                        nextState()
                        if (user != null) {
                            if(user.userName.isNullOrEmpty())
                                user.userName = userName
                            user.isSave = true
                            if(isSavePass)
                                savePassword(user.userName,textPass)
                            else
                                savePassword(user.userName,null)
                            //showToast(getString(R.string.migamesdk_text_register_success) + user.userName)
                            loginSuccess(user)
                            val json = JSONObject()
                            json.put("username",user.userName)
                            json.put("userid",user.userId)
                            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_register_success),json)
                        } else {
                            loginFailed(e?.message)
//                            showToast(e?.message)
//                            authListener?.onRegisterFail(e?.message)
                            val json = JSONObject()
                            json.put("username", bindingFgRegister.mgFragmentRegisterEdAccount.text.toString())
                            json.put("error", e?.message)
//                            if(context!=null)
                            TrackingManager.trackEventCount(

                                context?._getString(R.string.mg_event_register_failed),
                                json
                            )
                        }
                    }
                }
            }else
            {
                hideLoading()
                currentState = STATE_DONE
                showToast(getString(R.string.mg_text_no_network))
            }


    }



    private fun getMGAccountLogin() {
        val dateTime = SDKParams.getCurrentTime()
        val textPass = bindingFgLogin.mgFragmentLoginEdPass.text.toString()
        val pass = Encrypt.md5(bindingFgLogin.mgFragmentLoginEdPass.text.toString())
        val account = bindingFgLogin.mgFragmentLoginEdAccount.text.toString()
        val isSavePass = bindingFgLogin.mgFragmentLoginCkSavePass.isSaveEnabled
        SDKManager.baseConfigModel?.let {
            if (NetworkUtils.isNetworkConnected(context!!)) {
                val deviceID  = Device.getDeviceID(context!!)
                showLoading()
                AuthenApi.getLoginUser(
                    deviceID = deviceID,
                    userName = account,
                    password = (pass),
                    linkAPI = it.User_Login,
                    time = dateTime,
                    sign = Encrypt.getHashCodeLogin(
                        account,
                        pass,
                        dateTime,
                        SDKManager.getSECRET_KEY()
                    )
                )
                { user, e ->
                    hideLoading()
                    currentState = STATE_DONE
                    nextState()
                    if (user != null) {
                        countLogin = 0
                        user.isSave = true
                        if (isSavePass)
                            savePassword(user.userName,textPass)
                        else
                            savePassword(user.userName,null)
                        loginSuccess(user)
//                        authListener?.onLoginSuccess(user)
//                        loginEventSuccess(getString(R.string.migamesdk_event_login_success),
//                            hashMapOf(fragment_login_ed_account.text.toString() to "username"))
                        TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_success),
                            hashMapOf("username" to bindingFgLogin.mgFragmentLoginEdAccount.text.toString()), null)
                    } else {
                        countLogin++
                        showToast(e?.message)
                        loginFailed(e?.message)
//                        showMessage(e?.message)
                        //authListener?.onLoginFail(e?.message)
//                        loginEventFailed(getString(R.string.migamesdk_event_login_failed),e?.message,hashMapOf(fragment_login_ed_account.text.toString() to "username"))
                        TrackingManager.trackLoginEvent(context?._getString(R.string.mg_event_login_failed),hashMapOf("username" to bindingFgLogin.mgFragmentLoginEdAccount.text.toString()),e?.message)
                        if (countLogin > 2)
                            showCaptcha()
                    }
                }
            } else {
                hideLoading()
                currentState = STATE_DONE
                showToast(getString(R.string.mg_text_no_network))
            }

        }
    }
	
	public fun checkAndShowBanner(obj: Any)
    {
        if(obj == null){
            return 
        }


        if(obj is BaseConfigsDataModel){


            val config = obj as BaseConfigsDataModel
            //val dataWeb = config.dataWeb//config.dataWeb.base64Decoded()
            if(Constants.isDebug)
            {
                print("link web:"+config.linkWeb)
                print("dataWeb original: "+config.dataWeb)
                //print("dataWeb2: "+dataWeb)
            }
            if(!ValidateSDK.isStringNullOrEmpty(config.linkWeb) || !ValidateSDK.isStringNullOrEmpty(config.dataWeb)){
                val intent: Intent = Intent(this, BannerMiGameActivity::class.java)
				intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
				intent.putExtra("TypeLoadAds", 1)
				intent.putExtra("LinkWeb", config.linkWeb)
				intent.putExtra("DataWeb", config.dataWeb)
				this.startActivity(intent)
            }
        }
        if(obj is UserAccountModel){
            
            val config = obj as UserAccountModel
            
            
            
            //let dataWeb = config.dataWeb//config.dataWeb.base64Decoded()
            
           
            if(Constants.isDebug)
            {
                print("link web:"+config.linkWeb)
                print("dataWeb original: "+config.dataWeb)
                //print("dataWeb2: "+dataWeb)
            }
            if(!ValidateSDK.isStringNullOrEmpty(config.linkWeb) || !ValidateSDK.isStringNullOrEmpty(config.dataWeb)){
                val intent: Intent = Intent(this, BannerMiGameActivity::class.java)
				intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
				intent.putExtra("TypeLoadAds", 1)
				intent.putExtra("LinkWeb", config.linkWeb)
				intent.putExtra("DataWeb", config.dataWeb)
				this.startActivity(intent)
            }
        }
        
    }
	
	
    private fun loadConfigSuccess(config:BaseConfigsDataModel)
    {
        handle.removeCallbacks(runnable)
        handle.post(runnable)
        SDKManager.baseConfigModel = config
        updateButtonAction()
        updateViewRegister()
        updateViewSync()
		checkAndShowBanner(config)
    }
    private fun loadConfigFailed(e: String?)
    {
        //Log.d("PQT Debug",e.toString())
        showMessageError(e)
    }
    private fun updateButtonAction()
    {


        var isShowAccount : Int = 1
        var isShowQuickPlay : Int = 1
        var isShowFB : Int = 1
        var isShowGG : Int = 1
        SDKManager.baseConfigModel?.SDKShowConfig?.let {
            isShowAccount = SDKManager.baseConfigModel!!.SDKShowConfig!!.isShowAccount
            isShowQuickPlay = SDKManager.baseConfigModel!!.SDKShowConfig!!.isShowQuickPlay
            isShowFB = SDKManager.baseConfigModel!!.SDKShowConfig!!.isShowFB
            isShowGG = SDKManager.baseConfigModel!!.SDKShowConfig!!.isShowGG
        }

        if(isShowQuickPlay == 1){
            bindingMainAccount.mgMainAccountLlQp?.visibility = View.VISIBLE
        }
        else
        {
            bindingMainAccount.mgMainAccountLlQp?.visibility = View.GONE


        }

        if (isShowAccount == 1) {
            bindingFgLogin.mgFragmentLoginTvLogin?.visibility = View.VISIBLE
        }else
            bindingFgLogin.mgFragmentLoginTvLogin?.visibility = View.GONE
        if (isShowGG == 1) {
            bindingMainAccount.mgMainAccountLlGg?.visibility = View.VISIBLE
        }else
            bindingMainAccount.mgMainAccountLlGg?.visibility = View.GONE
        if (isShowFB == 1) {
            bindingMainAccount.mgMainAccountLlFb?.visibility = View.VISIBLE
        }else
            bindingMainAccount.mgMainAccountLlFb?.visibility = View.GONE
    }

    private var iAPManager: IAPSDKManager? = null
//    private var billingProcess: BillingProcessor? = null
    private fun initBillingInApp() {
//        val listener = object : AppBillingResult{
//            override fun onBillingFail() {
//                MiGameSDK.showLog("payment","thanh toán faile productid="+productId + "-transactionid=" + transactionId + "packageid=" + request?.packageId)
//                paymentListener?.onPaymentFail(getString(R.string.migamesdk_text_payment_error))
//            }
//
//            override fun onBillingNotFoundProduct() {
//                MiGameSDK.showLog("payment","thanh toán not found productid="+productId + "-transactionid=" + transactionId + "packageid=" + request?.packageId)
//                paymentListener?.onPaymentFail(getString(R.string.migamesdk_text_payment_error_not_found_product))
//            }
//
//            override fun onBillingSuccess(purchase: Purchase) {
//                MiGameSDK.showLog("payment","thanh toán success productid="+productId + "-transactionid=" + transactionId + "packageid=" + request?.packageId)
//
//                if(purchase.purchaseState==0)
//                    getChargeToGame(purchase)
//                else
//                    paymentListener?.onPaymentFail(getString(R.string.migamesdk_text_payment_error))
//            }
//        }
//        appBillingManager = context?.let {
//            AppBillingManager(it, listener)
//        }
//        activity?.let {
//            appBillingManager?.launchBillingFlow(
//                activity!!,
//                productId,
//                BillingClient.SkuType.INAPP
//            )
//        }



//        billingProcess = BillingProcessor(context,AccountManager.getInstance().configs?.extraValue?.IAP_Android_PublicKey, object : BillingProcessor.IBillingHandler {
//            override fun onBillingInitialized() {
//                readyPurchase = true
//                if(!Utilities.isStringNullOrEmpty(paramsIAP?.transactionID))
//                    getBillingInAppStore()
//            }
//
//            override fun onPurchaseHistoryRestored() {
////                Log.d(Constants.LOG_TAG, "onPurchaseHistoryRestored")
//                MiGameSDK.showLog(Constants.LOG_TAG,"onPurchaseHistoryRestored")
//            }
//
//            override fun onProductPurchased(productId: String, details: TransactionDetails?) {
//                billingProcess?.consumePurchase(productId)
//                val purchaseState: Int? = details?.purchaseInfo?.purchaseData?.purchaseState?.ordinal
//                MiGameSDK.showLog(Constants.LOG_TAG,"purchaseState = " + purchaseState)
//                val orderIDIAP = details?.purchaseInfo?.purchaseData?.orderId
//                val signatureIAP = details?.purchaseInfo?.signature
//                val dataReceipt = details?.purchaseInfo?.responseData
//
//                val js = JSONObject(Gson().toJson(paramsIAP))
//                js.put("orderIDIAP",orderIDIAP)
//                js.put("signatureIAP",signatureIAP)
//                js.put("dataReceipt",dataReceipt)
//                Utilities.trackEventCount(context,getString(R.string.migamesdk_event_payment_success),js)
//                val valueVND = paramsIAP!!.valueVND
//                Utilities.trackEventRevenue(context,getString(R.string.migamesdk_event_payment_total_success), valueVND, js.toString())
//
//                var _paramsIAP = paramsIAP
//                if(_paramsIAP?.transactionID.isNullOrEmpty())
//                    _paramsIAP = AccountManager.getInstance().getLastIAP()
//                if(_paramsIAP == null)
//                    return
//                if (purchaseState == 0) {
//                    if (orderIDIAP!= null && signatureIAP!= null && dataReceipt!= null)
//                        saveInfoIAP(signatureIAP,_paramsIAP!!.transactionID,paramsIAP?.packageId!!,orderIDIAP!!,_paramsIAP?.packageStoreID!!,dataReceipt!!,_paramsIAP!!.orderId,_paramsIAP!!.serverId,_paramsIAP!!.roleID,_paramsIAP!!.roleName,_paramsIAP!!.otherData)
//                    getChargeToGame(
//                        details.purchaseInfo,
//                        _paramsIAP
//                    )
//                } else {
//                    paymentListener?.onPaymentFail(getString(R.string.migamesdk_text_payment_error))
//                }
//            }
//
//            override fun onBillingError(errorCode: Int, error: Throwable?) {
//                MiGameSDK.showLog(Constants.LOG_TAG,"${error?.message}")
//                onPaymentFail("Thanh toán không thành công!")
//            }
//        })


//        iAPManager.callIAP(dataPaymentModel.productID.checkNull())
//        if(iAPManager!=null && iAPManager!!.getBillingClient() != null && iAPManager!!.getBillingClient()!!.isReady)
//            return
        //System.out.println("debug7")
        SDKManager.baseConfigModel?.let { config ->
            if (NetworkUtils.isNetworkConnected(context)) {
//                System.out.println("activity"+activity);
//                System.out.println("activity"+AccountManager.getInstance().configs?.SDKShowConfig);
//                System.out.println("AccountManager.getInstance().configs!!.extraValue"+AccountManager.getInstance().configs?.extraValue)
//                System.out.println("AccountManager.getInstance().configs!!.extraValue.IAP_Android_PublicKey"+AccountManager.getInstance().configs?.extraValue?.IAP_Android_PublicKey)
                //System.out.println("debug9" + config.extraValue.IAP_Android_PublicKey)
                iAPManager = IAPSDKManager(this@MGActivity, config.extraValue.IAP_Android_PublicKey, object : IAPSDKManager.EventIAPManager {
                    override fun onPaymentReady() {

                        //System.out.println("debug10")
                        if(isCreateTrans==false && transactionIAP==null)
                            getCreateTransStore()
                        Constants.showDataLog("iap game","ready")
                    }

                    override fun onPaymentSuccess(purchase: Purchase) {
                        //System.out.println("debug11")

                        var js: JSONObject;
                        js = JSONObject();
                        var valueVND = 0.0f;
                        if(transactionIAP != null) {
                            js.put("transactionID", transactionIAP!!.transactionID);
                            js.put("Package",transactionIAP!!.packageID)
                            valueVND = transactionIAP!!.amount;
                        }
                           TrackingManager.trackEventRevenue(
                             context?._getString(R.string.mg_event_revenue_iap),
                           valueVND,
                         js.toString()
                        )
                        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_success_iap),js)
                        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_total_payment),js)
                        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_total_complete_success),js)
                        TrackingManager.trackEventCount(context?.getString(R.string.mg_event_payment_success),js)

                        getChargeToGame(purchase)
                      //  purchase.
                    }

                    override fun onPaymentFailed(message: String) {
                        messageErrorIAP = message
                        if(isCreateTrans==false && transactionIAP==null)
                            getCreateTransStore()
                        else if(readyPurchase)
                            onPaymentFail(message)
                    }
                })
                iAPManager!!.startConnectionIAP()
            } else {
                onPaymentFail(getString(R.string.mg_text_no_network))
            }
        } ?: run {
//            callAPIGetConfigData(typeGetConfig = TYPE_GET_CONFIG_INIT_IAP)
        }
        //System.out.println("debug8")

    }

    var retryChargeGame : Int = 0
    var isUseCoin : Boolean = false
    //songpq
    private fun getChargeToGame(
        purchase: Purchase?,

        useCoin : String = ""
    ) {
        Constants.showDataLog(Constants.LOG_TAG,"getChargeToGame")

    //    transactionIAP


        var orderIDIAP = "orderIDIAP"
        var signatureIAP = "signatureIAP"
        var dataReceipt = "dataReceipt"
        if (purchase != null){

            orderIDIAP = purchase.orderId.toString()
            signatureIAP = purchase.signature
            dataReceipt = purchase.originalJson
        }

//        val purchaseToken = purchase.responseData
        SDKManager.baseConfigModel?.let {

            val deviceId = Device.getDeviceID(this@MGActivity)
            val dateTime = SDKParams.getCurrentTime()
             PaymentApi.getChargeToGame(
                accessToken.getValue(),
                transactionID,
                packageID.getValue(),
                orderIDIAP,
                packageStoreID.getValue(),
                signatureIAP,
                dataReceipt,
                orderID.getValue(),
                serverID.getValue(),
                roleID.getValue(),
                roleName.getValue(),
                Constants.CLIENT_OS,
                deviceId,
                otherData.getValue(),
                useCoin,
                it.urlIAPChargeToGame,
                dateTime,
                Encrypt.getHashCodeGetChargeToGame(
                    accessToken.getValue(),
                    transactionID,
                    orderID.getValue(),
                    orderIDIAP,
                    packageStoreID.getValue(),
                    dateTime,SDKManager.getSECRET_KEY()
                )
            ) { result, e ->
                if (result != null) {
                    removeInfoIAP(transactionID)
                    onPaymentSuccess(result)
                } else {
                    if (retryChargeGame >= 3) {


                        onPaymentFail(e?.message)
                    }
                    else {
                        retryChargeGame = retryChargeGame + 1
                        getChargeToGame(purchase,useCoin)
                    }
                }
            }
        }
    }

    private fun onPaymentSuccess(chargeToGameResult: PaymentDataGameModel?) {
        closeActivity()
        SDKManager?.paymentUserCallback?.onUserPaymentSuccess(chargeToGameResult)
        val js = JSONObject(Gson().toJson(chargeToGameResult))
        var valueVND = 0.0f
     //   if(chargeToGameResult!=null && chargeToGameResult.amount>0)
       //     valueVND = chargeToGameResult.amount as Float

      //  Log.d("PQT Debug","------------ amount---------" +  chargeToGameResult?.amount);
     //   Log.d("PQT Debug","transaction " + transactionIAP!!.transactionID + " AMOUNT " + transactionIAP!!.amount)
        if(transactionIAP != null)
        {
            js.put("transactionID",transactionIAP!!.transactionID);
            valueVND = transactionIAP!!.amount;
        }

        //js.put("transactionID",transactionID)

        if(isUseCoin) {
            TrackingManager.trackEventRevenue(
                context?._getString(R.string.mg_event_revenue_micoin),
                valueVND,
                js.toString()
            )
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_success_micoin),js)
            TrackingManager.trackEventRevenue(context?._getString(R.string.mg_event_revenue_total), valueVND , js.toString())

            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_total_payment),js)
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_total_complete_success),js)
            TrackingManager.trackEventCount(context?.getString(R.string.mg_event_payment_success),js)
        }
       // else {
         //   TrackingManager.trackEventRevenue(
           //     context?._getString(R.string.mg_event_revenue_iap),
             //   valueVND,
               // js.toString()
            //)
            //TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_success_iap),js)
        //}


      //


    }
    private fun onPaymentFail(e: String?){



        var _message = getString(R.string.mg_text_unknown_error)
        if(e!= null && !TextUtils.isEmpty(e))
        {
            _message = e
        }

        this.showMessage {
            title = getString(R.string.mg_text_message_title)
            message = _message
            listener = object : AppMessage.EventMessage {
                override fun onClickConfirm() {
                    super.onClickConfirm()
                    SDKManager?.paymentUserCallback?.onUserPaymentFail(e)
                    closeActivity()

                }
            }
        }
        val js = JSONObject()
        js.put(Constants.KEY_DATA_PACKAGE_ID,packageID)
        js.put(Constants.KEY_DATA_ORDER_ID,orderID)
        js.put(Constants.KEY_DATA_SERVER_ID,serverID)
        js.put(Constants.KEY_DATA_ROLE_ID,roleID)
        js.put(Constants.KEY_DATA_ROLE_NAME,roleName)
        js.put(Constants.KEY_DATA_OTHER_DATA,otherData)
        js.put("MessageError",e)
        TrackingManager.trackEventCount(

            context?._getString(R.string.mg_event_payment_failed),
            js
        )
        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_total_payment),js)

        TrackingManager.trackEventCount(context?.getString(R.string.mg_event_payment_total_complete_failed),js)
        if(isUseCoin)
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_failed_micoin),js);
        else
            TrackingManager.trackEventCount(context?._getString(R.string.mg_event_payment_failed_iap),js);



    }
    var messageErrorIAP = ""
    var isCreateTrans = false
    var transactionIAP :TransactionPaymentModel? = null
    var readyPurchase = false
    private fun processUseCoin(trans: TransactionPaymentModel) : Boolean
    {
        val js = JSONObject()
        js.put(Constants.KEY_DATA_PACKAGE_ID,packageID)
        js.put(Constants.KEY_DATA_ORDER_ID,orderID)
        js.put(Constants.KEY_DATA_SERVER_ID,serverID)
        js.put(Constants.KEY_DATA_ROLE_ID,roleID)
        js.put(Constants.KEY_DATA_ROLE_NAME,roleName)
        js.put(Constants.KEY_DATA_OTHER_DATA,otherData)
        if (!trans.askUseCoin.isNullOrEmpty()) {
            var txtYes = trans.textYes
            if (txtYes.isNullOrEmpty()) {
                txtYes = getString(R.string.mg_text_confirm)
            }
            var txtNo = trans.textNo
            if (txtNo.isNullOrEmpty()) {
                txtNo = getString(R.string.mg_text_cancel)
            }
//            val listennerYes = object : DialogInterface.OnClickListener {
//                override fun onClick(p0: DialogInterface?, p1: Int) {
//                    //songpq
//                    getChargeToGame(null,paramsIAP,"1")
//                }
//            }
//            val listennerNo = object : DialogInterface.OnClickListener {
//                override fun onClick(p0: DialogInterface?, p1: Int) {
//                    if(iAPManager!!.isReady && ValidateSDK.isStringNullOrEmpty(messageErrorIAP)) {
//                        readyPurchase = true
//                        iAPManager!!.callIAP(packageStoreID)
//                    }
//                    else
//                    {
//                        onPaymentFail(messageErrorIAP)
//                    }
//                }
//            }
            //SDKManager.baseConfigModel!!.disablePopupCoin == 1
            //showAskDialog(getString(R.string.migamesdk_text_title_alert),trans.askUseCoin,txtNo , txtYes , listennerNo,listennerYes)
            if(SDKManager.baseConfigModel!!.disablePopupCoin == 0) {
                showMessageConfirm {
                    title = getString(R.string.mg_text_message_title)
                    message = trans.askUseCoin
                    textConfirm = txtYes
                    textCancel = txtNo
                    listener = object : AppMessage.EventMessage {
                        override fun onClickCancel() {
                            super.onClickCancel()
//                        if(iAPManager!!.isReady && ValidateSDK.isStringNullOrEmpty(messageErrorIAP)) {
//                            readyPurchase = true
//                            iAPManager!!.callIAP(packageStoreID)
//                        }
//                        else
//                        {
//                            onPaymentFail(messageErrorIAP)
//                        }

                            onPaymentFail(getString(R.string.mg_you_have_canceled_your_payment))
                        }

                        override fun onClickConfirm() {
                            super.onClickConfirm()
                            getChargeToGame(null, "1")
                            isUseCoin = true;
                        }
                    }
                }
            }else
                getChargeToGame(null, "1")
            isUseCoin = true;
            TrackingManager.trackEventCount(

                context?._getString(R.string.mg_event_choose_payment_micoin),
                js
            )
            return true
        }
        isUseCoin = false;
        TrackingManager.trackEventCount(

            context?._getString(R.string.mg_event_choose_payment_iap),
            js
        )
        return false
    }
    private fun getCreateTransStore() {
//        AccessToken + Package + DeviceID + ClientOS + StoreProductID + Time , SecretKey)
        //System.out.println("debug11")

        if(isCreateTrans)
            return





        isCreateTrans = true
        if(verifyInfoPayment()==false)
            return
        val runnable : Runnable = Runnable {
            SDKManager.baseConfigModel?.let {

                val dateTime = SDKParams.getCurrentTime()
                val deviceId = Device.getDeviceID(this@MGActivity)
                val data = "${accessToken.getValue()}${packageID.getValue()}${deviceId.getValue()}${Constants.CLIENT_OS.getValue()}${dateTime.getValue()}"
                PaymentApi.getCreateTrans(
                    accessToken.getValue(),
                    packageID.getValue(),
                    serverID.getValue(),
                    deviceId,
                    packageStoreID,
                    it.urlIAPCreateTransStore,
                    dateTime,
                    Encrypt.getHashString(
                        data,SDKManager.getSECRET_KEY())
                ) { result, e ->
                    if (result != null) {

                        transactionIAP = result
                        transactionID = result.transactionID
                        //result.amount
                        if(packageID != result.packageID && ValidateSDK.isStringNullOrEmpty(result.askUseCoin))
                        {
//                            showToast(getString(R.string.migamesdk_text_error_payment))
                            onPaymentFail(getString(R.string.mg_text_error_payment))

                        } else {
                            packageStoreID = result.storeProductID
                            if (processUseCoin(result)==false) {
//                            getBillingInAppStore()
                                if(packageStoreID.isNullOrEmpty())
                                {
                                    //showToast(getString(R.string.migamesdk_text_payment_error) + " product empty")
                                    //paymentListener?.onPaymentFail(getString(R.string.migamesdk_text_payment_error)+" product empty")
                                    onPaymentFail(getString(R.string.mg_payment_failed)+ " product empty")
                                }
                                else if(iAPManager!!.isReady && ValidateSDK.isStringNullOrEmpty(messageErrorIAP)) {
                                    readyPurchase = true
                                    iAPManager!!.callIAP(packageStoreID)
                                }
                                else
                                {
                                    onPaymentFail(messageErrorIAP)
                                }
                            }
                        }
                    } else {
//                        showToast(e?.message)
                        onPaymentFail(e?.message)
                    }
                }
            }
        }
        val thread : Thread = Thread(runnable)
        thread.start()

    }
    private fun getConfig() {
        context?.let {
            if(NetworkUtils.isNetworkConnected(context!!)) {
                showLoading()
                val dateTime = SDKParams.getCurrentTime()
                val deviceID = Device.getDeviceID(context!!)
                ConfigApi.getConfig(
                    deviceID,
                    dateTime,
                    Encrypt.getHashCodeConfig(DeviceID = deviceID,RequestTime = dateTime,appSecretKey = SDKManager.getSECRET_KEY())
                ) { config, e ->
                    hideLoading()

                    currentState = STATE_LOGIN_MG//STATE_INIT
                    if (config != null) {
                        currentState = STATE_DONE
                        SDKManager.baseConfigModel = config
                      //  if(SDKManager.baseConfigModel.adjustConfig == null)
                        //    SDKManager.baseConfigModel.adjustConfig = MigaAdjustConfig();
                       // Log.d("PQT Debug", "kkkkkkkkkk" + SDKManager.baseConfigModel!!.urlIAPChargeToGame);
                       // Log.d("PQT Debug","hhhhhhhhhh" + SDKManager.baseConfigModel!!.urlIAPCreateTransStore);
                       // Log.d("PQT Debug","hhhhhhhhhh" + SDKManager.baseConfigModel!!.urlIAPCreateTrans);

                        SDKManager.baseConfigModel?.let {
                         SDKManager.adjustConfig = SDKManager.baseConfigModel!!.adjustConfig;
                        }
                      //  if(SDKManager.adjustConfig == null)
                        //    SDKManager.adjustConfig = MigaAdjustConfig();
                        //SDKManager.initAdjustSDK(applicationContext,SDKManager.adjustConfig!!.AdjustAppToken)
                        //TrackingManager.mappingAdjustKey();
//                        Log.d("PQT Debug-----","config::" + config.toString())
  //                      Log.d("PQT Debug-----","Adjust config::" + SDKManager.adjustConfig.toString())
                        SDKManager.initAdjustSDK(applicationContext)
                        savePreviousConfig(config)
                        loadConfigSuccess(config)

                        nextState()

//                    this.config = config
//                    context?.let {
//                        UserManager(it).storeConfig(config)
//                    }
                    } else {
                        loadConfigFailed(e?.message)
                    }
                }
            }else
            {
                hideLoading()
                currentState = STATE_DONE
                showToast(getString(R.string.mg_text_no_network))
                if(hideMainMenu)
                    closeActivity()
            }
        }
    }
    private fun verifyAccessTokenFailed(e:String?)
    {
        showMainMenu()
    }
    private fun verifyAccessToken(accessToken: String) {

        TrackingManager.trackEventCount(context?._getString(R.string.mg_event_verify_token),null)
        context?.let {

            var payment = "0"

            val dateTime = SDKParams.getCurrentTime()

            SDKManager.baseConfigModel?.let {
                if (NetworkUtils.isNetworkConnected(context!!)) {
                showLoading()
                    val deviceId = Device.getDeviceID(context!!)
                    UserApi.getUserByAccessToken(accessToken, deviceId, payment, SDKManager.baseConfigModel!!.User_GetByToken,dateTime,
                        Encrypt.getHashCodeGetUser(accessToken, dateTime, SDKManager.getSECRET_KEY()),
                        { user, e ->
                        currentState = STATE_DONE
                        hideLoading()
                        if (user != null) {
                            user.accessToken = accessToken
                            nextState();
                            loginSuccess(user)
                                TrackingManager.trackEventCount( context?._getString(R.string.mg_event_verify_token_success), null)
                        } else {
                            val js = JSONObject()
                            js.put("error", e?.message)
                            verifyAccessTokenFailed(e?.message)
                                TrackingManager.trackEventCount(context?._getString(R.string.mg_event_verify_token_failed), js)
                        }
//                        if (checkUserSynAccount(user)) {
//                            checkVerifyAccesstoken(user,e?.message)
//                        }else

                    })
                } else {
                    hideLoading()
                     verifyAccessTokenFailed(getString(R.string.mg_text_no_network))
                    currentState = STATE_DONE
                    showToast(getString(R.string.mg_text_no_network))
                }
            }
        }
    }



//    private fun checkVerifyAccesstoken(user: User?,e: String?)
//    {
//        if(user!=null)
//        {
//            if(checkUserSynAccount(user) && AccountManager.getInstance().getStatusQuickPlay() == true)
//            {
//                needSyncAccount(user)
//
//            }else
//                if(request?.isForceShowLogin==false) {
//                    MiGameSDK.isShowPopup = false
//                    onLoginSuccess(user, false)
//                }
//        }
//        else
//        {
//            migamesdk_fragment_auth?.visibility = View.VISIBLE
//            onLoginFail(e)
//        }
//    }
    private fun showCaptcha()
    {

    }








    private var sharedPreferences: SharedPreferences? = null


    private val KEY_SHARED_PREFERENCES_USER = "vn.mgjsc.sdk.user"
    private val KEY_SHARED_PREFERENCES_LAST_USER = "vn.mgjsc.sdk.lastuser"


    private val KEY_SHARED_PREFERENCES_QUICK_PLAY = "vn.mgjsc.sdk.quickplay"
//    private val KEY_SHARED_PREFERENCES_FACEBOOK = "vn.mgjsc.sdk.facebook"
//    private val KEY_SHARED_PREFERENCES_GOOGLE = "vn.mgjsc.sdk.google"
    private val KEY_SHARED_PREFERENCES_USERNAME = "vn.mgjsc.sdk.username"
    private val KEY_SHARED_PREFERENCES_PACKAGES = "vn.mgjsc.sdk.packages"
    private val KEY_SHARED_PREFERENCES_SKIP_ADS = "vn.mgjsc.sdk.skipAdses"
    private val KEY_SHARED_PREFERENCES_LAST_IAP = "vn.mgjsc.sdk.lastIAP"


    private var typePayment : Int = Constants.TYPE_PAYMENT_DIRECT_PACKAGE
    fun getTypePayment() : Int {
        return typePayment
    }
    private val gson: Gson = Gson()
    fun isLogged(): Boolean {
        val user: UserAccountModel? = try {
            gson.fromJson(
                sharedPreferences?.getString(KEY_SHARED_PREFERENCES_USER, ""),
                UserAccountModel::class.java
            )
        } catch (e: Exception) {
            return false
        }
        return user != null
    }
    fun saveListPackage(packages : List<Package>)
    {
        if (packages != null && sharedPreferences != null) {
            val dataStr = gson.toJson(packages)
            sharedPreferences!!.edit()!!.putString(KEY_SHARED_PREFERENCES_PACKAGES, dataStr).apply()
        }
    }
    fun getListPackage(): List<Package>? {
        if(sharedPreferences!=null) {
            val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_PACKAGES, "")
            val groupListType = object : TypeToken<ArrayList<Package>>() {

            }.type
            return gson.fromJson(data, groupListType)
        }
        return null
    }
    fun clearUser()
    {
        sharedPreferences?.edit()?.putString(KEY_SHARED_PREFERENCES_USER, null)?.apply()
    }
    fun saveAdsSkip(idAds : String,isSkip : Boolean = true):Boolean
    {
        if(sharedPreferences!=null) {
            val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_SKIP_ADS, "")
            var json : JSONObject = JSONObject()
            if(!TextUtils.isEmpty(data))
            {
                json = JSONObject(data);
            }
            if(isSkip) {
                json.put((idAds), isSkip)
                sharedPreferences!!.edit()!!
                    .putString(KEY_SHARED_PREFERENCES_SKIP_ADS, json.toString()).apply()
                return true;
            }
            else
            {
                if(json.has(idAds))
                {
                    json.remove(idAds);
                    sharedPreferences!!.edit()!!
                        .putString(KEY_SHARED_PREFERENCES_SKIP_ADS, json.toString()).apply()
                }
                return false;
            }

        }
        return false;
    }
    fun getAdsSkip(): JSONObject {
        if(sharedPreferences!=null) {
            val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_SKIP_ADS, "")
            if(!TextUtils.isEmpty(data))
            {
                return JSONObject(data)
            }
        }
        return JSONObject()
    }
//    fun saveLastIAP(paramsIAP: ParamsIAP?)
//    {
//        if(paramsIAP != null && sharedPreferences != null)
//        {
//            val dataStr = gson.toJson(paramsIAP!!)
//            sharedPreferences!!.edit()!!.putString(KEY_SHARED_PREFERENCES_LAST_IAP,dataStr).apply()
//        }
//    }

//    fun getLastIAP(): ParamsIAP? {
//        if(sharedPreferences!=null) {
//            val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_LAST_IAP, "")
//            return gson.fromJson(
//                data,
//                ParamsIAP::class.java
//            )
//        }
//        return null
//    }
    fun getUser(): UserAccountModel? {
        if(sharedPreferences!=null) {
            val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_USER, "")
            return gson.fromJson(
                data,
                UserAccountModel::class.java
            )
        }
        return null
    }
    fun saveUser(user: UserAccountModel?) {
        if(user!=null && sharedPreferences!= null && !user.accessToken.isNullOrEmpty()) {
            val dataStr = gson.toJson(user!!)
            sharedPreferences!!.edit()!!.putString(KEY_SHARED_PREFERENCES_USER,dataStr).apply()
            if(user.isSave) {
                user.userName?.let {
                    saveLastUserName(user.userName)
                }
            }
        }
    }

    fun getLastUser(): UserAccountModel? {
        if(sharedPreferences!=null) {
            val data = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_LAST_USER, "")
            return gson.fromJson(
                data,
                UserAccountModel::class.java
            )
        }
        return null
    }
    fun saveLastUser(user: UserAccountModel?) {
        if(user!=null && sharedPreferences!= null && !user.accessToken.isNullOrEmpty()) {
            val dataStr = gson.toJson(user!!)
            sharedPreferences!!.edit()!!.putString(KEY_SHARED_PREFERENCES_LAST_USER,dataStr).apply()

        }
    }



    fun savePreviousConfig(config : BaseConfigsDataModel?)
    {
//        MiGameSDK.getApplicationContext()?.let {
//            val runnable = Runnable { MiGameSDK.trackerIAP(MiGameSDK.getApplicationContext()!!) }
//            val thread:Thread = Thread(runnable)
//            thread.start()
//
//        }


        if(config!=null && sharedPreferences!= null ) {
            val dataStr = gson.toJson(config!!)
            sharedPreferences!!.edit()!!.putString(SDKManager.KEY_SHARED_PREFERENCES_CONFIGS,dataStr).apply()
        }
        else
        {
            sharedPreferences!!.edit()!!.putString(SDKManager.KEY_SHARED_PREFERENCES_CONFIGS,"").apply()
        }


    }

    fun saveInfoIAP(data: JSONArray?) {
        if(data!=null && sharedPreferences!= null ) {
            val dataStr = data.toString()
            sharedPreferences!!.edit()!!.putString(SDKManager.KEY_SHARED_PREFERENCES_INFO_IAP,dataStr).apply()
        }
        else
        {

            sharedPreferences!!.edit()!!.putString(SDKManager.KEY_SHARED_PREFERENCES_INFO_IAP, JSONArray().toString()).apply()
        }
    }
    private fun getInfoIAP() : JSONArray
    {

        if(sharedPreferences!=null) {
            val data = sharedPreferences!!.getString(SDKManager.KEY_SHARED_PREFERENCES_INFO_IAP, "")
            try {
                var info : JSONArray = JSONArray(data)
                return info
            }catch (e: JSONException)
            {
                return JSONArray()
            }

        }
        return JSONArray()
    }

    private fun saveLastUserName(userName:String) {
        if (sharedPreferences != null && !userName.isNullOrEmpty()) {
            sharedPreferences!!.edit()!!.putString(KEY_SHARED_PREFERENCES_USERNAME, userName)!!.apply()

        }
    }

    private fun getLastUserName() : String?
    {
        var result : String? = ""

        sharedPreferences?.let {
            result = sharedPreferences!!.getString(KEY_SHARED_PREFERENCES_USERNAME,result)
        }
        return result
    }
    private fun getPassword(userName : String) : String?
    {
        var result : String? = ""

        sharedPreferences?.let {
            result = sharedPreferences!!.getString(userName,result)
        }
        return result
    }

    private fun savePassword(userName : String,password:String?) {
        if(password != null) {
            if (sharedPreferences != null) {
                sharedPreferences!!.edit()!!.putString(userName, password)!!.apply()

            }
        }else
        {
            if (sharedPreferences != null) {
                sharedPreferences!!.edit()!!.putString(userName, "")!!.apply()

            }
        }
    }

    fun saveStatusQuickPlay(status:Boolean) {
        return
        if (sharedPreferences != null) {
            sharedPreferences!!.edit()!!.putBoolean(KEY_SHARED_PREFERENCES_QUICK_PLAY, status)!!.apply()

        }
    }

    fun getStatusQuickPlay():Boolean
    {
        return true
        var result = false
        sharedPreferences?.let {
            result = sharedPreferences!!.getBoolean(KEY_SHARED_PREFERENCES_QUICK_PLAY, false)
        }
        return result
    }
    fun Activity.showLoadingDialog(): Dialog {
        val progressDialog = Dialog(this)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.progress_dialog_sdk)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        return progressDialog
    }

    private val RC_SIGN_IN = 111
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //songpq
   //     if (callbackManagerLoginFB!= null && callbackManagerLoginFB.onActivityResult(requestCode, resultCode, data)) {
       //     return
    //    }
        //ParseFacebookUtils.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(data)
        }

    }





    private fun removeInfoIAP(transactionID : String) {
        try {
            var dics = getInfoIAP()
            var exist = false
            var newDics = JSONArray()
            val n = dics.length() - 1
            for (index in 0..n) {
                val item = dics.getJSONObject(index)
                val trans = item.getString(SDKParams.PARAM_TRANSACTION_ID)
                if (trans != null && !trans.equals(transactionID)) {
                    newDics.put(item)
                } else {
                    exist = true
                }
            }
            if (exist == true) {
                saveInfoIAP(newDics)
            }
        } catch (e: Exception) {
            Constants.showDataLog("save iAP", e.toString())
        }
    }



    fun saveInfoIAP(
        signature: String,
        transactionID: String,
        packageID: String,
        orderIDIAP: String,
        productID: String,
        dataReceipt: String,
        orderID: String,
        serverID: String,
        roleID: String,
        roleName: String,
        other: String
    ) {
        try {
            var dics = getInfoIAP()
            var dic: JSONObject = JSONObject()

            dic.put(SDKParams.PARAM_SIGNATURE_IAP, signature)
            dic.put(SDKParams.PARAM_TRANSACTION_ID, transactionID)
            dic.put(SDKParams.PARAM_PACKAGE_ID, packageID)
            dic.put(SDKParams.PARAM_ORDER_ID_IAP, orderIDIAP)
            dic.put(SDKParams.PARAM_PRODUCT_ID, productID)
            dic.put(SDKParams.PARAM_DATA_RECEIPT, dataReceipt)
            dic.put(SDKParams.PARAM_ORDER_ID, orderID)
            dic.put(SDKParams.PARAM_SERVERID, serverID)
            dic.put(SDKParams.PARAM_ROLE_ID, roleID)
            dic.put(SDKParams.PARAM_ROLE_NAME, roleName)
            dic.put(SDKParams.PARAM_OTHER_DATA, other)
            var exist = false
            val n = dics.length() - 1
            for (index in 0..n) {
                val item = dics.getJSONObject(index)
                val trans = item.getString(SDKParams.PARAM_TRANSACTION_ID)
                if (trans != null && trans.equals(transactionID)) {
                    exist = true
                    break
                }
            }
            if (exist == false) {
                dics.put(dic)
                saveInfoIAP(dics)
            }
        } catch (e: Exception) {
            Constants.showDataLog("save iAP", e.toString())
        }
//            do {
//                let jsonData = try JSONSerialization.data(withJSONObject: dic, options: .prettyPrinted)
//                // here "jsonData" is the dictionary encoded in JSON data
//
//                let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
//                // here "decoded" is of type `Any`, decoded from JSON data
//
//                // you can now cast it with the right type
//                if let dictFromJSON = decoded as? [String:String] {
//                    // use dictFromJSON
//                }
//            } catch {
//                print(error.localizedDescription)
//            }
    }



}