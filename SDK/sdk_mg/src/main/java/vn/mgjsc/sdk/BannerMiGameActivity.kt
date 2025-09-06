package vn.mgjsc.sdk

//import vn.mgjsc.sdk
//mport com.mgsdk.android.base.BaseActivity
//import com.mgsdk.android.base.BasePresenter
//import com.mgsdk.android.base.IOnBackPressed
//import com.mgsdk.android.constants.Constants
//import com.mgsdk.android.function.main.MiGameSDKPresenter

//import com.mgsdk.android.models.AdsMiGame
//import com.mgsdk.android.utilities.DateTimeUtils
//import com.mgsdk.android.utilities.DeviceUtils
//import com.mgsdk.android.utilities.HashUtils
//import com.mgsdk.android.utils.NetworkUtils
//import com.mgsdk.android.utils.Utilities
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
//import kotlinx.android.synthetic.main.activity_banner_sdk_migame.*


import org.json.JSONObject
import vn.mgjsc.sdk.api.ConfigApi
import vn.mgjsc.sdk.constants.Constants
import vn.mgjsc.sdk.constants.Encrypt
import vn.mgjsc.sdk.constants.SDKParams
import vn.mgjsc.sdk.databinding.ActivityBannerSdkMigameBinding
import vn.mgjsc.sdk.models.BannerMiGame
import vn.mgjsc.sdk.utils.Device
import vn.mgjsc.sdk.utils.NetworkUtils
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class BannerMiGameActivity : AppCompatActivity() {
    //BaseActivity<BasePresenter>() {



    var isBannerConfig : Boolean = true
    var zoneID : String = ""
    var isLoadComplete = false;
	
	var linkWeb : String = ""
	var dataWeb : String = ""
	var TypeLoadAds = 0

    private lateinit var binding: ActivityBannerSdkMigameBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityBannerSdkMigameBinding.inflate(layoutInflater);
        setContentView(binding.root);
        //setContentView(R.layout.activity_banner_sdk_migame)


        binding.activityBannerMigameIvClose.visibility = View.INVISIBLE;

        binding.activityBannerMigameLlBg.visibility = View.INVISIBLE;

        binding.activityBannerMigameIv.visibility = View.GONE;


        binding.activityBannerMigameWv.visibility = View.GONE;


        //activity_banner_migame_chk_skip.
        binding.activityBannerMigameChkSkip.setOnCheckedChangeListener { compoundButton, b ->
            if(bannerShow!=null) {
                bannerShow?.isSkipBanner = binding.activityBannerMigameChkSkip.isChecked
                SDKManager.saveBannerSkip(context!!,bannerShow!!.idBanner.toString(),binding.activityBannerMigameChkSkip.isChecked)
                lstAds = SDKManager.removeSkipBanner(context,lstAds);
                //AccountManager.getInstance().saveAdsSkip(adsShow!!.idAds.toString(),activity_ads_migame_chk_skip.isChecked)
                //lstAds =  AccountManager.getInstance().removeSkipAds(lstAds)
            }
        }
        binding.activityBannerMigameWv.settings.javaScriptEnabled = true;

        binding.activityBannerMigameWv.settings.allowContentAccess = true;
      //  activity_banner_migame_wv.settings.setAppCacheEnabled(false)
       binding.activityBannerMigameWv.settings.cacheMode = WebSettings.LOAD_NO_CACHE;

        binding.activityBannerMigameWv.webViewClient  = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if(url!= null && url!!.startsWith(TEXT_CLOSE)) {
                    onDialogClose()
                    return
                }
                if(isLoadComplete && url!=null)//if(url!=null && url!!.startsWith("market://")||url!!.startsWith("https://play.google.com/store") || url!!.startsWith("http://play.google.com/store"))
                {
                    openLink(url)
                    return;
                }
                binding.activityBannerLoading.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if(url!= null && isLoadComplete)//if(url!=null && url!!.startsWith("market://")||url!!.startsWith("https://play.google.com/store") || url!!.startsWith("http://play.google.com/store"))
                {
                    openLink(url)
                    return;
                }
                isLoadComplete = true;
                binding.activityBannerLoading.visibility = View.GONE
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {

                val errorMessage = "Got Error! $error"
                //showToast(errorMessage)

                super.onReceivedError(view, request, error)
            }
        }
		intent?.let {
			if(intent.hasExtra("TypeLoadAds"))
				TypeLoadAds = intent.getIntExtra("TypeLoadAds",0);
			if(TypeLoadAds == 0)
			{
				isBannerConfig = intent.getBooleanExtra("isAdsConfig",true);
				zoneID = intent.getStringExtra("ZoneID")!!;
				binding.activityBannerMigameIvClose.setOnClickListener {
					onDialogClose()
				}
				loadBanner();
			}
			else
			{
				val _dataWeb = intent.getStringExtra("DataWeb");
				if(_dataWeb != null)
					dataWeb = _dataWeb!!;
				val _linkWeb = intent.getStringExtra("LinkWeb");
				if(_linkWeb != null)
					linkWeb = _linkWeb!!;
				showBanner();
			}
            binding.activityBannerLoading.visibility = View.VISIBLE
            
        }
    }



   var loadingFinished = true
   var redirect = false
   var lstAds = ArrayList<BannerMiGame>()
   fun openLink(url:String)
   {
       val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
       startActivity(i)
       onDialogClose()
   }


   val TEXT_CLOSE = "https://migame.vn/close"


    fun fromHtml(html: String?): Spanned? {
        return if (html == null) {
            // return an empty spannable if the html is null
            SpannableString("")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }
	fun showBanner()
	{
		if(!TextUtils.isEmpty(dataWeb)) {
           binding.activityBannerMigameIv.visibility = View.GONE
            binding.activityBannerMigameWv.visibility = View.VISIBLE
           isLoadComplete = false
           val dataHtml = fromHtml(dataWeb).toString();
            binding.activityBannerMigameWv.loadData(dataHtml,"text/html", "UTF-8")
           return;
       }
		if(!TextUtils.isEmpty(linkWeb)) {
            binding.activityBannerMigameIv.visibility = View.GONE
            binding.activityBannerMigameWv.visibility = View.VISIBLE
           isLoadComplete = false;
            binding.activityBannerMigameWv.loadUrl(linkWeb)
           return;
       }
       
	}
   fun showBanner(banner:BannerMiGame)
   {
       binding.activityBannerLoading.visibility = View.GONE

       binding.activityBannerMigameLlBg.visibility = View.VISIBLE
       binding.activityBannerMigameIvClose.visibility = View.VISIBLE
       bannerShow = banner;
       if(banner.isForceShow>0) {
           binding.activityBannerMigameChkSkip.visibility = View.INVISIBLE
           binding.activityBannerMigameBottom.visibility = View.GONE
       }
       else {
           binding.activityBannerMigameChkSkip.visibility = View.VISIBLE
           binding.activityBannerMigameBottom.visibility = View.VISIBLE
       }

       binding.activityBannerMigameChkSkip.isChecked = banner.isSkipBanner


       SDKManager.getBannerCallback()?.onShow(banner)
       //AccountManager.getInstance().getAdsCallback()?.onShow(ads)

       if(banner==null)
           return;
       if(!TextUtils.isEmpty(banner.LinkHtml) && banner.Type == 1) {
           binding.activityBannerMigameIv.visibility = View.GONE
           binding.activityBannerMigameWv.visibility = View.VISIBLE
           isLoadComplete = false;
           binding.activityBannerMigameWv.loadUrl(banner.LinkHtml!!)
           return;
       }
       if(!TextUtils.isEmpty(banner.sourceHtml) && banner.Type == 2) {
           binding.activityBannerMigameIv.visibility = View.GONE
           binding.activityBannerMigameWv.visibility = View.VISIBLE
           isLoadComplete = false
           val dataHtml = fromHtml(banner.sourceHtml!!).toString();
           binding.activityBannerMigameWv.loadData(dataHtml,"text/html", "UTF-8")
           return;
       }
       if(!TextUtils.isEmpty(banner.sourceImage) &&  banner.Type == 3) {
           binding.activityBannerMigameIv.visibility = View.VISIBLE
           binding.activityBannerMigameWv.visibility = View.GONE
           binding.activityBannerMigameIv.adjustViewBounds=true
           downloadfile(banner.sourceImage,binding.activityBannerMigameIv)
           binding.activityBannerMigameIv.setOnClickListener {
               if(banner.linkOpen!=null) {
                   if (banner.linkOpen!!.startsWith(TEXT_CLOSE)) {
                       onDialogClose()

                   }else
                       openLink(banner.linkOpen!!)
               }
               else {
                   Log.d("error link", banner.linkOpen.toString())
                   onDialogClose()
               }
           }
           return;
       }

   }
   fun downloadfile(fileurl: String?, img: ImageView) {
       var bmImg: Bitmap? = null
       var myfileurl: URL? = null

       if(fileurl==null)
           return
       binding.activityBannerLoading.visibility = View.VISIBLE



//        Glide.with(this).load(fileurl).;
       Glide.with(this).load(fileurl).listener(object: RequestListener<Drawable>{
           override fun onLoadFailed(
               e: GlideException?,
               model: Any?,
               target: Target<Drawable>?,
               isFirstResource: Boolean
           ): Boolean {
               return true
           }

           override fun onResourceReady(
               resource: Drawable?,
               model: Any?,
               target: Target<Drawable>?,
               dataSource: DataSource?,
               isFirstResource: Boolean
           ): Boolean {
               binding.activityBannerLoading.visibility = View.GONE
               img.setImageDrawable(resource)
               return true
           }

       }).into(img)
       if(true)
           return
       try {
           myfileurl = URL(fileurl)
       } catch (e: MalformedURLException) {
           e.printStackTrace()
       }
       try {
           val conn: HttpURLConnection = myfileurl?.openConnection() as HttpURLConnection
           conn.setDoInput(true)
           conn.connect()
           val length: Int = conn.getContentLength()
           if (length > 0) {
               //val bitmapData = IntArray(length)
               //val bitmapData2 = ByteArray(length)
               val inputstream: InputStream = conn.getInputStream()
               bmImg = BitmapFactory.decodeStream(inputstream)
               img.setImageBitmap(bmImg)
           }
       } catch (e: Exception) {
           e.printStackTrace()
       }

       binding.activityBannerLoading.visibility = View.GONE
   }
   var bannerShow : BannerMiGame? = null
   fun loadBanner()
   {

       if(isBannerConfig)
       {
          // if(AccountManager.getInstance().configs!=null && AccountManager.getInstance().configs!!.adsMigame!=null&&AccountManager.getInstance().configs!!.adsMigame!!.size>0)
           if(SDKManager.baseConfigModel != null && SDKManager.baseConfigModel!!.bannerMigame != null && SDKManager.baseConfigModel!!.bannerMigame!!.size > 0)
           {
               SDKManager.baseConfigModel!!.bannerMigame!!.shuffle();
               //AccountManager.getInstance().configs!!.adsMigame!!.shuffle();
               //lstAds = AccountManager.getInstance().configs!!.adsMigame!!
               lstAds = SDKManager.baseConfigModel!!.bannerMigame!!
               indexAds = 0
               handler.post(runnable)
           }
           else
           {
               onFailed(getString(R.string.mg_text_error_banner_config))
           }

       }else
       {
           if(SDKManager.baseConfigModel ==null)
           {
               getConfig()
           }
           else
           {
               getBanner()
           }

       }
   }

   fun onDialogClose()
   {
       //AccountManager.getInstance().getAdsCallback()?.onClose()
       SDKManager.getBannerCallback()?.onClose()
       finish()

   }
   private fun onFailed(e:String?)
   {
       var ee = "have problem"
       if(e!=null)
           ee = e
//        showToast(ee)
      // MiGameSDK.showLog(Constants.LOG_TAG,ee);
       Constants.showDataLog(Constants.LOG_TAG,ee);
       //SDKManager.lo
      // AccountManager.getInstance().getAdsCallback()?.onFailed(ee)
       SDKManager.getBannerCallback()?.onFailed(ee)
       onDialogClose()
       finish()
   }
    var context:Context? = null
   private fun getBanner() {
       //if(AccountManager.getInstance().configs!!.SDKShowConfig.IsShowAds==0)
       if(SDKManager.baseConfigModel!!.SDKShowConfig.IsShowAds == 0)
       {
           onFailed("Hidden Banner")
           return
       }

       context = this
       context?.let {
           if(NetworkUtils.isNetworkConnected(context!!)) {
               val dateTime = SDKParams.getCurrentTime()
                   //DateTimeUtils.getCurrentTimeString()
               val deviceID = Device.getDeviceID(context)
                 //  DeviceUtils.getDeviceID(context!!)
               var userID = ""
              // val jsonSkipAds : JSONObject = AccountManager.getInstance().getAdsSkip()
               val jsonSkipAds : JSONObject = SDKManager.getBannerSkip(context!!)
//                var jsonArray : JSONArray = JSONArray()
//                for (key in jsonSkipAds.keys()) {
//                    // here key will be containing your OBJECT NAME YOU CAN SET IT IN TEXTVIEW.
//
//                }
               if (SDKManager.getUser(context!!) != null) {
                   //if (AccountManager.getInstance().getUser() != null) {
                     //  userID = AccountManager.getInstance().getUser()!!.userId
                   //}
                   userID = SDKManager.getUser(context!!)!!.userId
               }
               ConfigApi.getBanner(
               //BasePresenter().getAds(
                   SDKParams.getVersionCode(context!!),
                   //Utilities.getVersionCode(context!!),
                   zoneID,
                   jsonSkipAds.toString(),
                   userID,
                   deviceID,
                   SDKManager.baseConfigModel!!.Get_Banner,
                   dateTime,
                   Encrypt.hashCode256(dateTime,SDKManager.getAPP_KEY())
                   //HashUtils.hashCode256(dateTime, MiGameSDK.getAppSecretKey())
               ) { ads, e ->

                   if (ads != null) {
                       if(ads.size>0) {
                           ads.shuffle()
                           lstAds = ads
                           indexAds = 0
                           handler.post(runnable)
                       }
                       else
                           onFailed(getString(R.string.mg_text_error_get_banner))

                   } else {

                       onFailed(e?.message)
                   }
               }
           }else
           {
               onFailed(getString(R.string.mg_text_no_network))
           }
       }

   }
   private fun getConfig() {
       //val context = this
       context = this;
       context?.let {
           if(NetworkUtils.isNetworkConnected(context!!)) {
               val dateTime = SDKParams.getCurrentTime()
                   //DateTimeUtils.getCurrentTimeString()
               val deviceID = Device.getDeviceID(context!!)
                   //DeviceUtils.getDeviceID(context!!)

               ConfigApi.getConfig(
                   deviceID,
                   dateTime,
                   Encrypt.getHashCodeConfig(DeviceID = deviceID,RequestTime = dateTime,appSecretKey = SDKManager.getSECRET_KEY())
               ) { config, e ->

                   if (config != null) {

                       //AccountManager.getInstance().configs = config
                       SDKManager.baseConfigModel = config
                       SDKManager.savePreviousConfig(context!!,config)
                       //AccountManager.getInstance().savePreviousConfig(config)

                       getBanner()
                   } else {
                       onFailed(e?.message)
                   }
               }
           }else
           {

               onFailed(getString(R.string.mg_text_no_network))
           }
       }

   }
   override  fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
       return if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() === 0) {
           // do something on back pressed.
           onBackPressed()

           true
       } else super.onKeyDown(keyCode, event)

   }
   override fun onBackPressed() {
       onDialogClose();
//        if(supportFragmentManager.backStackEntryCount > 0)
//            supportFragmentManager.popBackStack()
//        else {
//
//            super.onBackPressed()
//        }
       //val fragment = this.supportFragmentManager.findFragmentById(R.id.activity_ads_migame_llContainer)
       //fragment as? IOnBackPressed
       //val fragment =
         //  this.supportFragmentManager.findFragmentById(R.id.activity_ads_migame_frContainer)
       //(fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
         //  if(it == true && MiGameSDK.getShowClose())
           //    super.onBackPressed()
       //}
   }

   //override fun providePresenter(): MiGameSDKPresenter {
     //  return MiGameSDKPresenter()
   //}

   val handler: Handler = Handler()
   var indexAds = 0
   val runnable: Runnable = object : Runnable {
       override fun run() {


           if(indexAds >= lstAds.size)
           {
               indexAds=0
           }
           if(lstAds.size>0)
               showBanner(lstAds.get(indexAds))
           indexAds +=1
           if(lstAds.size>1)
               handler.postDelayed(this, 7000)
       }
   }
   override fun onDestroy() {
       super.onDestroy()
       handler.removeCallbacks(runnable);


   }

}
