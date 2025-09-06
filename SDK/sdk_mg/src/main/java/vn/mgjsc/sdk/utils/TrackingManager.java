package vn.mgjsc.sdk.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.*;
//import com.appsflyer.AppsFlyerTrackingRequestListener;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import vn.mgjsc.sdk.R;
import vn.mgjsc.sdk.SDKManager;
import vn.mgjsc.sdk.constants.Constants;
import vn.mgjsc.sdk.constants.SDKParams;

import vn.mgjsc.sdk.models.MigaAdjustConfig;

import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.Adjust;
public class TrackingManager {

    private static FirebaseAnalytics mfirebaseAnalytics = null;
    private static Context context;
//    private static AppEventsLogger logger ;
    static Map<String,String> AdjustSDKMap = new HashMap<String,String>();

    public static void mappingAdjustKey()
    {
        MigaAdjustConfig adjustConfig = SDKManager.getAdjustConfig();

        if(adjustConfig != null) {

            AdjustSDKMap.put(context.getString(R.string.mg_event_click_login), adjustConfig.getAdjustClickLogin());
            AdjustSDKMap.put(context.getString(R.string.mg_event_login_success), adjustConfig.getAdjustLoginSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_login_failed), adjustConfig.getAdjustLoginFailed());

            AdjustSDKMap.put(context.getString(R.string.mg_event_click_login_qp), adjustConfig.getAdjustClickQuickPlay());
            AdjustSDKMap.put(context.getString(R.string.mg_event_login_qp_success), adjustConfig.getAdjustQuickPlaySuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_login_qp_failed), adjustConfig.getAdjustQuickPlayFailed());

            AdjustSDKMap.put(context.getString(R.string.mg_event_login_fb_success), adjustConfig.getAdjustLoginFBSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_login_fb_failed), adjustConfig.getAdjustLoginFBFailed());
            AdjustSDKMap.put(context.getString(R.string.mg_event_click_login_fb), adjustConfig.getAdjustClickLoginFB());

            AdjustSDKMap.put(context.getString(R.string.mg_event_login_gg_success), adjustConfig.getAdjustLoginGGSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_login_gg_failed), adjustConfig.getAdjustLoginGGFailed());
            AdjustSDKMap.put(context.getString(R.string.mg_event_click_login_gg), adjustConfig.getAdjustClickLoginGG());

            AdjustSDKMap.put(context.getString(R.string.mg_event_click_payment), adjustConfig.getAdjustClickPayment());

            AdjustSDKMap.put(context.getString(R.string.mg_event_choose_payment_iap), adjustConfig.getAdjustChoosePaymentIAP());
            AdjustSDKMap.put(context.getString(R.string.mg_event_payment_success_iap),adjustConfig.getAdjustPaymentIAPSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_revenue_iap), adjustConfig.getAdjustPaymentRevenueIAP());
            AdjustSDKMap.put(context.getString(R.string.mg_event_payment_failed_iap),adjustConfig.getAdjustPaymentIAPFailed());

            AdjustSDKMap.put(context.getString(R.string.mg_event_choose_payment_micoin), adjustConfig.getAdjustChoosePaymentMiCoin());
            AdjustSDKMap.put(context.getString(R.string.mg_event_payment_success_micoin),adjustConfig.getAdjustPaymentMicoinSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_revenue_micoin), adjustConfig.getAdjustPaymentRevenueMicoin());
            AdjustSDKMap.put(context.getString(R.string.mg_event_payment_failed_micoin),adjustConfig.getAdjustPaymentMicoinFailed());

            AdjustSDKMap.put(context.getString(R.string.mg_event_payment_total_payment),adjustConfig.getAdjustPaymentTotalPayment());
            AdjustSDKMap.put(context.getString(R.string.mg_event_payment_failed),adjustConfig.getAdjustPaymentFailed());
            AdjustSDKMap.put(context.getString(R.string.mg_event_payment_success),adjustConfig.getAdjustPaymentSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_revenue_total),adjustConfig.getAdjustPaymentRevenueTotal());



            AdjustSDKMap.put(context.getString(R.string.mg_event_click_register), adjustConfig.getAdjustClickRegister());
            AdjustSDKMap.put(context.getString(R.string.mg_event_register_success), adjustConfig.getAdjustRegisterSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_register_failed), adjustConfig.getAdjustRegisterFailed());

            AdjustSDKMap.put(context.getString(R.string.mg_event_sync_validate), adjustConfig.getAdjustSyncValidate());
            AdjustSDKMap.put(context.getString(R.string.mg_event_sync_success), adjustConfig.getAdjustSyncSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_sync_failed), adjustConfig.getAdjustSyncFailed());

            // AdjustSDKMap.put(context.getString(R.string.mg_event_login_failed), adjustConfig.getAdjustLoginFailed());
            //AdjustSDKMap.put(context.getString(R.string.mg_event_login_success), adjustConfig.getAdjustLoginSuccess());

            //AdjustSDKMap.put(context.getString(R.string.mg_event_login_validate_success), adjustConfig.getAdjustLoginValidationSuccess());

            //AdjustSDKMap.put(context.getString(R.string.mg_event_payment_total_complete_success), adjustConfig.getAdjustPaymentRevenue());








            AdjustSDKMap.put(context.getString(R.string.mg_event_verify_token), adjustConfig.getAdjustVerifyToken());
            AdjustSDKMap.put(context.getString(R.string.mg_event_verify_token_success), adjustConfig.getAdjustVerifyTokenSuccess());
            AdjustSDKMap.put(context.getString(R.string.mg_event_verify_token_failed), adjustConfig.getAdjustVerifyTokenFailed());



            //AdjustSDKMap.put(context.getString(R.string.mg_event_revenue_total), adjustConfig.getAdjustPaymentRevenueTotal());





        }
    }
    public static void init(Context _context)
    {
        context = _context;
        mfirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mappingAdjustKey();


    //    AppsFlyerLib.getInstance().init(SDKManager.Companion.getAFF_KEY(),null,context);
      //  AppsFlyerLib.getInstance().start(context);
//        logger = AppEventsLogger.newLogger(_context);
    }


    public static void trackLoginEvent(String eventName, Map<String,String> map,String msgError)
    {
        try {



            JSONObject json = new JSONObject();
            if (map != null) {

                for (Map.Entry<String,String> entry : map.entrySet())
                {
                    json.put(entry.getKey(),entry.getValue());
                }
            }
            String subError = "";
            if(msgError != null && msgError.length() > 0) {
                int indexStatusCode = msgError.indexOf("statusCode");
                //String subError = "";
                if(indexStatusCode >= 0 )
                {
                    subError = msgError.substring(indexStatusCode);
                    json.put("error",subError);
                }
            }


            trackEventCount(eventName,json);



        }catch(Exception io){}

    }
    public static void trackEventRevenue(final String eventName,final float value,final String jsonContent)
    {
        String transID = "";
        String packID="";
        try{
            JSONObject object= new JSONObject(jsonContent);
            transID = object.optString("transactionID");
            packID = object.optString("Package");
        }catch (Exception io)
        {

        }
        //songpq remove applsflyer
        if(SDKManager.getAdjustConfig() != null) {
            String keyEvent = AdjustSDKMap.get(eventName);

            if (keyEvent != null) {
                AdjustEvent event = new AdjustEvent(keyEvent);
                if (event != null) {
                    event.setRevenue(value, "VND");
                    if(!TextUtils.isEmpty(transID))
                        event.setOrderId(transID);
                    Adjust.trackEvent(event);
                }
            }
        }
        try{
            Bundle bundle = new Bundle();
            bundle.putFloat(FirebaseAnalytics.Param.VALUE,value);
            bundle.putString(FirebaseAnalytics.Param.CURRENCY,"VND");
            if(!TextUtils.isEmpty(packID))
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,packID);
            if(!TextUtils.isEmpty(transID))
                bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID,transID);

            if(mfirebaseAnalytics == null)
                mfirebaseAnalytics = FirebaseAnalytics.getInstance(context);

            mfirebaseAnalytics.logEvent(eventName,bundle);
            mfirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE,bundle);

            Constants.showDataLog(Constants.LOG_TAG,"event name :" + eventName + " track revenue" + bundle.toString());
            Map<String, Object> eventValue = new HashMap<String, Object>();

            eventValue.put(AFInAppEventParameterName.REVENUE,value);

//        eventValue.put(AFInAppEventParameterName.CURRENCY,);
            if(jsonContent!= null) {
                eventValue.put(AFInAppEventParameterName.CONTENT, jsonContent);
                bundle.putString(FirebaseAnalytics.Param.CONTENT, jsonContent);
            }


            trackEventAppsflyer(eventName,eventValue);


            //trackRevenueEventAdjust(value,"VND");

        //    Bundle params = new Bundle();
         //   params.putString("content", jsonContent);

//            MiGameSDK.getFbLogger().logEvent(eventName, 1, params);

//            MiGameSDK.getFbLogger().logPurchase(new BigDecimal(value),  Currency.getInstance("VND"));
        }catch(Exception e)
        {

        }

    }
    public static void trackRevenueEventAdjust(float amount, String currency)
    {
        AdjustEvent event = new AdjustEvent("EVENT TOKEN");
        event.setRevenue(amount,currency);
        Adjust.trackEvent(event);
    }
    public static void trackEventAppsflyer(final String eventName,final Map eventValues)
    {

        if(context == null)
            return;
        //songpq remove appsflyer

                try{
                    AppsFlyerLib.getInstance().trackEvent(context, eventName, eventValues, new AppsFlyerTrackingRequestListener() {
                        @Override
                        public void onTrackingRequestSuccess() {

                        }

                        @Override
                        public void onTrackingRequestFailure(String s) {
                            //AppsFlyerLib.getInstance().trackEvent(context, eventName, eventValues);
                        }
                    });
                }catch (Exception e)
                {

                }



    }
    public static void trackEventCount(final String eventName,final JSONObject _jsonContent)
    {

        try {
            JSONObject jsonContent = _jsonContent;
            Bundle bundle = new Bundle();
            bundle.putInt(FirebaseAnalytics.Param.QUANTITY, 1);
            if(jsonContent == null)
                jsonContent = new JSONObject();

            jsonContent.put("deviceid",Device.INSTANCE.getDeviceID());
            jsonContent.put("time", SDKParams.INSTANCE.getCurrentTime());
            bundle.putString(FirebaseAnalytics.Param.CONTENT,jsonContent.toString());
            if(mfirebaseAnalytics!=null)
                mfirebaseAnalytics.logEvent(eventName,bundle);
            Constants.showDataLog(Constants.LOG_TAG,"track count event name:" + eventName + " :: " + bundle.toString());
            //SDKManager.getAdjustConfig();
            MigaAdjustConfig adjustConfig = SDKManager.getAdjustConfig();
            AdjustEvent event = null;
            if(adjustConfig != null) {


                String str = AdjustSDKMap.get(eventName);
                if(str != null)
                    event = new AdjustEvent(str);


                if(event != null ) {
                    Iterator<String> keys = jsonContent.keys();
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        Object value = jsonContent.get(key);
                        event.addCallbackParameter(key, value.toString());
                    }
                }
                Adjust.trackEvent(event);
            }
        }catch(Exception e)
        {

        }
    }

}
