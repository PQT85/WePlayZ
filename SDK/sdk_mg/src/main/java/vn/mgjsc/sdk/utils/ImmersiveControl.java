package vn.mgjsc.sdk.utils;

import android.app.Activity;
import android.icu.text.LocaleDisplayNames;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.core.view.ViewConfigurationCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
//import androidx.core.view.WindowInsetsControllerCompat;

public class ImmersiveControl {

    private static View.OnSystemUiVisibilityChangeListener UIListener = null;

    private static void makeImmersive(final Activity thiz)
    {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        {
            thiz.getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_VISIBLE);
            thiz.getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }else {
            thiz.getWindow().getDecorView().setSystemUiVisibility(
                    //android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                      //      android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            //android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //|
                            //android.view.View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

        thiz.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
       // thiz.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //thiz.getWindow().setSoftInputMode();
    }

    public static void activateImmersiveMode (final Activity thiz)
    {

        if(ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(thiz.getApplicationContext())))
            return; // do nothing if device has no soft key bar

        makeImmersive(thiz);

        //registerSystemUIListener(thiz);
    }

    private static void registerSystemUIListener(final Activity thiz)
    {
        if(UIListener == null)
        {
            UIListener = new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            makeImmersive(thiz);
                        }
                    },2000);
                }
            };
        }

        thiz.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(UIListener);
    }

    public static void onKeyDown (final Activity thiz, int keyCode) {
        if (ViewConfigurationCompat.hasPermanentMenuKey(android.view.ViewConfiguration.get(thiz.getApplicationContext())))
            return;//do nothing if device has no soft key bar

        if(keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP
        )
        {
            activateImmersiveMode(thiz);
        }
    }
}
