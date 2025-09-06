package com.example.myapplication

import android.os.Bundle
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vuonggiathienha.tamquoc.R
import kotlinx.android.synthetic.main.activity_main.*
import vn.mgjsc.sdk.SDKManager
import vn.mgjsc.sdk.models.PaymentDataGameModel
import vn.mgjsc.sdk.models.UserAccountModel


class MainActivity : AppCompatActivity() {


    fun showToast(msg : String)
    {

        val monitor: Runnable = object : Runnable{
            override fun run() {
                Toast.makeText(this@MainActivity,msg,2000).show()
            }
            //runnable

        }
        runOnUiThread(monitor);
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main)
     //   SDKManager.initSDK(application,"cvtq09754b9218cc9f9d6e9192be85f514cdK","cvtq5f4feb3e6070a615c7ef49f801823d85S","682249380643-bch2l5lo88jj1st1evb2tnf5mfs5bha2.apps.googleusercontent.com",null);
        SDKManager.initSDK(application,"wow3q992f53ccda65076cbc7083880f6f4e8cK","wow3qf3caf8b1faff19e8741d4f14c93c3cbaS","577860542106-t3bgflo30dkmhks3hmialpasm7j2e38m.apps.googleusercontent.com",null);
        SDKManager.setIsDebug(true)

        Init.setOnClickListener {
            SDKManager.initSDK(application,edAppKey.text.toString(),edSecretKey.text.toString(),edGGKey.text.toString(),null);
        }

        val authen = object : SDKManager.AuthUserInterface
        {
            override
            fun onUserLoginSuccess(userAccountModel: UserAccountModel)
            {
                //System.out.println(userAccountModel.accessToken)
                //Log.d("PQT Debug","login success:" + userAccountModel.accessToken);
                showToast("login success:\n" + userAccountModel.accessToken)

            }
            override
            fun onUserLoginFail(e: String?){
                showToast("login fail:\n")
            }
            override
            fun onUserLogoutSuccess()
            {
                showToast("logout success")
            }
            override
            fun onUserLogoutFail(e : String?)
            {
                showToast("logout fail")
            }
            override
            fun onUserCancel()
            {
                showToast("user cancel request")
            }
            override
            fun onUserSyncSuccess(userAccountModel: UserAccountModel){
                System.out.println(userAccountModel.accessToken)
                showToast("sync success")
            }
            override
            fun onUserSyncCancel(userAccountModel: UserAccountModel){
                showToast("syn cancel")
            }
        }
        login.setOnClickListener {
            SDKManager.login(this,authen)
        }
        logout.setOnClickListener{
            SDKManager.logout(this,authen)
        }

        val payment = object:SDKManager.PaymentUserCallback{
            override fun onUserPaymentSuccess(chargeToGameResult: PaymentDataGameModel?) {
                showToast("success package:"+chargeToGameResult?.packageID)
            }

            override fun onUserPaymentFail(e: String?) {
//                TODO("Not yet implemented")
                showToast(e+"loiiii")
            }

            override fun onUserCancel() {
//                TODO("Not yet implemented")
                showToast("Cancel payment")
            }
        }
        btnPayment.setOnClickListener {
            SDKManager.payment(this,edPackage.text.toString(),System.currentTimeMillis().toString()
                ,edServer.text.toString(),"","","",payment)
        }
    }
}