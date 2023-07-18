package com.kabouzeid.appthemehelper.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.SwitchCompat;

import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.R;
import com.kabouzeid.appthemehelper.ThemeStore;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATESwitch extends SwitchCompat {

    public ATESwitch(Context context) {
        super(context);
        init(context, null);
    }

    public ATESwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATESwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setThumbResource(R.drawable.switch_thumb);
        setTrackResource(R.drawable.switch_track);
        ATH.setTint(this, ThemeStore.accentColor(context));
    }

    @Override
    public boolean isShown() {
        return getParent() != null && getVisibility() == View.VISIBLE;
    }
}
