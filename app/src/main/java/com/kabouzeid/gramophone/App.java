package com.kabouzeid.gramophone;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.gramophone.appshortcuts.DynamicShortcutManager;

import org.frknkrc44.frigraph.R;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class App extends Application {
    private static App app;
    private static Handler mainHandler;
    private static boolean appInit;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mainHandler = new Handler(Looper.getMainLooper());

        // default theme
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this)
                    .primaryColorRes(R.color.md_indigo_500)
                    .accentColorRes(R.color.md_pink_A400)
                    .commit();
        }

        // Set up dynamic shortcuts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).initDynamicShortcuts();
        }
    }

    public static void resetAppInit() {
        appInit = false;
    }

    public static boolean isAppInit() {
        if(!appInit) {
            appInit = true;
            return false;
        }
        return true;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }

    public static boolean isProVersion() {
        return true;
    }

    public static App getInstance() {
        return app;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
