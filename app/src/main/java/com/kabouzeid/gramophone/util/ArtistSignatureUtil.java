package com.kabouzeid.gramophone.util;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.signature.StringSignature;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ArtistSignatureUtil {
    private static final String ARTIST_SIGNATURE_PREFS = "artist_signatures";

    private static ArtistSignatureUtil sInstance;

    private final PreferenceUtil mPreferences;

    private ArtistSignatureUtil(@NonNull final Context context) {
        mPreferences = PreferenceUtil.getInstance(context);
    }

    public static ArtistSignatureUtil getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new ArtistSignatureUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    @SuppressLint("CommitPrefEdits")
    public void updateArtistSignature(String artistName) {
        mPreferences.edit().putLong(artistName, System.currentTimeMillis()).commit();
    }

    public long getArtistSignatureRaw(String artistName) {
        return mPreferences.getPrefs().getLong(artistName, 0);
    }

    public StringSignature getArtistSignature(String artistName) {
        return new StringSignature(String.valueOf(getArtistSignatureRaw(artistName)));
    }
}
