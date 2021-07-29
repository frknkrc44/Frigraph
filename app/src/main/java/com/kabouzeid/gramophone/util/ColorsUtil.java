package com.kabouzeid.gramophone.util;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.gramophone.App;

@RequiresApi(api = Build.VERSION_CODES.O_MR1)
public class ColorsUtil {
    private ColorsUtil() {}

/*
    private static final WallpaperManager.OnColorsChangedListener listener = (colors, which) -> {
        applyWallpaperColors(colors);
    };
*/

    public static void applyCurrentWallpaperColors() {
        applyWallpaperColors(getCurrentWallpaperColors());
    }

    private static void applyWallpaperColors(WallpaperColors colors) {
        int primaryColor = colors.getPrimaryColor().toArgb();
        ThemeStore.editTheme(App.getInstance())
                .primaryColor(primaryColor)
                .commit();

        if (colors.getSecondaryColor() != null) {
            int accentColor = colors.getSecondaryColor().toArgb();
            ThemeStore.editTheme(App.getInstance())
                    .accentColor(accentColor)
                    .commit();
        }
    }

    public static WallpaperColors getCurrentWallpaperColors(){
        WallpaperManager wallpaperManager = (WallpaperManager) App.getInstance().getSystemService(Context.WALLPAPER_SERVICE);
        return wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
    }

    /*
    public static void toggleRegisterForColorChanges(boolean enabled) {
        if (enabled) {
            registerForColorChanges();
        } else {
            unregisterForColorChanges();
        }
    }

    public static void registerForColorChanges() {
        WallpaperManager wallpaperManager = (WallpaperManager) App.getInstance().getSystemService(Context.WALLPAPER_SERVICE);
        wallpaperManager.addOnColorsChangedListener(listener, App.getMainHandler());
    }

    public static void unregisterForColorChanges() {
        WallpaperManager wallpaperManager = (WallpaperManager) App.getInstance().getSystemService(Context.WALLPAPER_SERVICE);
        wallpaperManager.removeOnColorsChangedListener(listener);
    }
    */
}
