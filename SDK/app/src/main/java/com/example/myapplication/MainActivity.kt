package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//import com.vuonggiathienha.tamquoc.R
//import vn.mg.tanvuong3q.R;
//import vn.mg.ditienhiep.R;
//import vn.mg.tamquocchienthan.R;
import com.daihiep.thinhkinh.databinding.ActivityMainBinding
//import kotlinx.android.synthetic.main.activity_main.*
import vn.weplayz.sdk.SDKWeplayZManager
import vn.weplayz.sdk.models.PaymentDataGameModel
import vn.weplayz.sdk.models.UserAccountModel


class MainActivity : AppCompatActivity() {


    private lateinit var binding : ActivityMainBinding;

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


      //  window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
       //         SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION or
        //        SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(layoutInflater);
        SDKWeplayZManager.requestPermissionForPN(this);
      //  setContentView(R.layout.activity_main)
        setContentView(binding.root)
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE)
     //   SDKManager.initSDK(application,"cvtq09754b9218cc9f9d6e9192be85f514cdK","cvtq5f4feb3e6070a615c7ef49f801823d85S","682249380643-bch2l5lo88jj1st1evb2tnf5mfs5bha2.apps.googleusercontent.com",null);
        SDKWeplayZManager.initSDK(application,"demo222222AK","demo222222SK","62115860010-rs5487dshpt850k60mvet9nojmkocv0d.apps.googleusercontent.com",null,"123");
        //SDKManager.setIsDebug(true)
        vn.weplayz.sdk.utils.ImmersiveControl.activateImmersiveMode(this);
       // SDKManager.showBanner(this,"test",false);
        binding.Init.setOnClickListener {
            //if(!TextUtils.isEmpty(edAppKey.text.toString())  && !TextUtils.isEmpty(edSecretKey.text.toString()))
            //if(edAppKey!!.text != null && edSecretKey!!.text != null)
              //  SDKManager.initSDK(application,edAppKey.text.toString(),edSecretKey.text.toString(),edGGKey.text.toString(),null);
            //val s : String
            //s.isNullorBlank();
        }



      //  binding.testHyperlink.text = Html.fromHtml(getString(R.string.mg_fragment_term_condition));//"Click <a href='google.com'>Google</a>");
       // binding.testHyperlink.movementMethod = LinkMovementMethod.getInstance();

        SDKWeplayZManager.requestPermissionForPN(this);
        val authen = object : SDKWeplayZManager.AuthUserInterface
        {
            override
            fun onUserLoginSuccess(userAccountModel: UserAccountModel)
            {
                //System.out.println(userAccountModel.accessToken)
                //Log.d("PQT Debug","login success:" + userAccountModel.accessToken);
             //   showToast("login success:\n" + userAccountModel.accessToken)

            }
            override
            fun onUserLoginFail(e: String?){
                showToast("login fail:\n")

            }
            override
            fun onUserLogoutSuccess()
            {
                showToast("logout success")
                Log.d("PQT Debug", "logout success")
            }
            override
            fun onUserLogoutFail(e : String?)
            {
                showToast("logout fail")
                Log.d("PQT Debug", "logout failed")
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
        binding.login.setOnClickListener {
            SDKWeplayZManager.login(this,authen)
        }
        binding.logout.setOnClickListener{
            SDKWeplayZManager.logout(this,authen)
        }

        val payment = object:SDKWeplayZManager.PaymentUserCallback{
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
        binding.btnPayment.setOnClickListener {

            SDKWeplayZManager.payment(this,binding.edPackage.text.toString(),System.currentTimeMillis().toString()
                ,binding.edServer.text.toString(),"","","",payment)
        }


    }
}