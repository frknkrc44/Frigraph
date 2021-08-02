package com.kabouzeid.gramophone.util;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.gramophone.App;

import java.util.Collections;
import java.util.Comparator;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class PhonographColorUtil {

    private PhonographColorUtil() {}

/*
    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private static final WallpaperManager.OnColorsChangedListener listener = (colors, which) -> {
        applyWallpaperColors(colors);
    };
*/

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public static void applyCurrentWallpaperColors() {
        applyWallpaperColors(getCurrentWallpaperColors());
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private static void applyWallpaperColors(WallpaperColors colors) {
        int primaryColor = colors.getPrimaryColor().toArgb();
        ThemeStore.editTheme(App.getInstance())
                .primaryColor(primaryColor)
                .commit();

        int accentColor = -1;
        if (colors.getTertiaryColor() != null)
            accentColor = colors.getTertiaryColor().toArgb();
        else if (colors.getSecondaryColor() != null)
            accentColor = colors.getSecondaryColor().toArgb();

        if (accentColor != -1) {
            ThemeStore.editTheme(App.getInstance())
                    .accentColor(lightenColorIfDark(accentColor))
                    .commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public static WallpaperColors getCurrentWallpaperColors(){
        WallpaperManager wallpaperManager = (WallpaperManager) App.getInstance().getSystemService(Context.WALLPAPER_SERVICE);
        return wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public static void toggleRegisterForColorChanges(boolean enabled) {
        if (enabled) {
            registerForColorChanges();
        } else {
            unregisterForColorChanges();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public static void registerForColorChanges() {
        WallpaperManager wallpaperManager = (WallpaperManager) App.getInstance().getSystemService(Context.WALLPAPER_SERVICE);
        wallpaperManager.addOnColorsChangedListener(listener, App.getMainHandler());
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public static void unregisterForColorChanges() {
        WallpaperManager wallpaperManager = (WallpaperManager) App.getInstance().getSystemService(Context.WALLPAPER_SERVICE);
        wallpaperManager.removeOnColorsChangedListener(listener);
    }
    */

    public static int lightenColorIfDark(int color) {
        int r = red(color), g = green(color), b = blue(color);
        for(int i = 1;!isColorLight(color);i++){
            int m = 0x04 * i;
            color = calculateColors(r+m,g+m,b+m);
        }
        return color;
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    private static int calculateColors(int r, int g, int b){
        r = r < 256 ? r : 255;
        g = g < 256 ? g : 255;
        b = b < 256 ? b : 255;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        return 0xFF000000 | r | g | b;
    }

    public static boolean isColorLight(int backgroundColor){
        return ColorUtils.calculateLuminance(backgroundColor) > 0.3f;
    }

    public static boolean isColorLighter(int backgroundColor){
        return ColorUtils.calculateLuminance(backgroundColor) > 0.5f;
    }

    @Nullable
    public static Palette generatePalette(Bitmap bitmap) {
        if (bitmap == null) return null;
        return Palette.from(bitmap).generate();
    }

    @ColorInt
    public static int getColor(@Nullable Palette palette, int fallback) {
        if (palette != null) {
            if (palette.getVibrantSwatch() != null) {
                return palette.getVibrantSwatch().getRgb();
            } else if (palette.getMutedSwatch() != null) {
                return palette.getMutedSwatch().getRgb();
            } else if (palette.getDarkVibrantSwatch() != null) {
                return palette.getDarkVibrantSwatch().getRgb();
            } else if (palette.getDarkMutedSwatch() != null) {
                return palette.getDarkMutedSwatch().getRgb();
            } else if (palette.getLightVibrantSwatch() != null) {
                return palette.getLightVibrantSwatch().getRgb();
            } else if (palette.getLightMutedSwatch() != null) {
                return palette.getLightMutedSwatch().getRgb();
            } else if (!palette.getSwatches().isEmpty()) {
                return Collections.max(palette.getSwatches(), SwatchComparator.getInstance()).getRgb();
            }
        }
        return fallback;
    }

    private static class SwatchComparator implements Comparator<Palette.Swatch> {
        private static SwatchComparator sInstance;

        static SwatchComparator getInstance() {
            if (sInstance == null) {
                sInstance = new SwatchComparator();
            }
            return sInstance;
        }

        @Override
        public int compare(Palette.Swatch lhs, Palette.Swatch rhs) {
            return lhs.getPopulation() - rhs.getPopulation();
        }
    }

    @ColorInt
    public static int shiftBackgroundColorForLightText(@ColorInt int backgroundColor) {
        while (ColorUtil.isColorLight(backgroundColor)) {
            backgroundColor = ColorUtil.darkenColor(backgroundColor);
        }
        return backgroundColor;
    }

    @ColorInt
    public static int shiftBackgroundColorForDarkText(@ColorInt int backgroundColor) {
        while (!ColorUtil.isColorLight(backgroundColor)) {
            backgroundColor = ColorUtil.lightenColor(backgroundColor);
        }
        return backgroundColor;
    }
}
