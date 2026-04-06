package vn.mgjsc.sdk.constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Constants {




    public static int REQUEST_CODE_PUSH_PERMISSION = 112233;
    public static String KEY_ACTION = "ACTION";
    public static String ACTION_LOGIN = "LOGIN";
    public static String ACTION_PAYMENT = "PAYMENT";
    public static String ACTION_LOGOUT = "LOGOUT";

    public static String ACTION_LOGOUT_ONLY = "LOGOUT_ONLY";



    public static String KEY_DATA_PACKAGE_ID = "PackageID";
    public static String KEY_DATA_ORDER_ID = "OrderID";
    public static String KEY_DATA_SERVER_ID = "ServerID";
    public static String KEY_DATA_ROLE_ID = "RoleID";
    public static String KEY_DATA_ROLE_NAME = "RoleName";
    public static String KEY_DATA_OTHER_DATA = "OtherData";
    public static String KEY_DATA_ACCESSTOKEN = "AccessToken";


    public static Boolean isDebug = false;
    public final static String ADJUST_KEY_CONFIG = "adjust_key.conf";
    public final static String LOG_TAG  = "MG_SDK_LOG:";

    public static void showDataLog(String tag,String data)
    {
        if(isDebug)
            Log.d(tag,data);
    }

    public final static String VERSION_SDK = "1.0.1";
    public final static String CLIENT_OS = "android";
    //var CLIENT_IP = DeviceUtils.getIPAddress(true)
    public final static String MAC_ADDRESS = "";
    public final static String ENVIROMENT = "production";

//        var BASE_URL = "https://msdk.migame.vn/"
    //public final static String BASE_URL = "https://msdk.migame.vn/";
    //public final static String BASE_HTTP_URL = "https://msdk.migame.vn/";

    public final static String BASE_URL = "https://sdk.weplayz.vn";
    public final static String BASE_HTTP_URL = "https://sdk.weplayz.vn";

//    public final static String BASE_URL = "https://sdk.gamestudiovn.online/";
//    public final static String BASE_HTTP_URL = "https://sdk.gamestudiovn.online/";
    public final static String []PERMISSION_FACEBOOK =  {"public_profile","email"};
    public final static long TIMER_CLOSE_DIALOG  = 2800;
    public final static long LONG_TIMER_CLOSE_DIALOG  = 4000;
    public final static int TYPE_PAYMENT_SHOW_LIST_PACKAGE = 1 ;// cho phép show danh sách vật phẩm trong sdk để mua
    public final static int TYPE_PAYMENT_USE_LIST_PACKAGE = 2;// mua thông qua list package
    public final static int TYPE_PAYMENT_DIRECT_PACKAGE = 3; // mua trực tiếp gói
//    var TYPE_PAYMENT = TYPE_PAYMENT_DIRECT_PACKAGE


    public final static String GENDER_MALE = "1";
    public final static String GENDER_FEMALE = "2";
    public final static String GENDER_OTHER = "3";
    public static void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }
    /**
     * @param key string like: SHA1, SHA256, MD5.
     */
    @SuppressLint("PackageManagerGetSignatures") // test purpose
    public static void getSHA(Context context, String key) {
        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance(key);
                md.update(signature.toByteArray());

                final byte[] digest = md.digest();
                final StringBuilder toRet = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    if (i != 0) toRet.append(":");
                    int b = digest[i] & 0xff;
                    String hex = Integer.toHexString(b);
                    if (hex.length() == 1) toRet.append("0");
                    toRet.append(hex);
                }

                Log.e(Constants.LOG_TAG, key + " " + toRet.toString());
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
    @SuppressLint("PackageManagerGetSignatures") // test purpose
    public static void getHashKey(Context context)
    {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
    }
}
