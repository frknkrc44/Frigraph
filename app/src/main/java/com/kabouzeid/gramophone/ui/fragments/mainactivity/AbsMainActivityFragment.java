package com.kabouzeid.gramophone.ui.fragments.mainactivity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.kabouzeid.gramophone.ui.activities.MainActivity;
import com.kabouzeid.gramophone.util.PreferenceUtil;

import org.frknkrc44.frigraph.R;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public abstract class AbsMainActivityFragment extends Fragment {

    public MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setCompactMode(AppBarLayout appbar, Toolbar toolbar, View targetView) {
        if (PreferenceUtil.getInstance(requireActivity()).enableCompactMode()) {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            ActionBar bar = activity.getSupportActionBar();
            if (bar != null) {
                ViewGroup group = (ViewGroup) targetView.getParent();
                group.removeView(targetView);
                bar.setDisplayShowHomeEnabled(false);
                bar.setDisplayShowCustomEnabled(true);
                bar.setDisplayShowTitleEnabled(false);
                bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                bar.setCustomView(targetView);
                // toolbar = (Toolbar) tabs.getParent();
                toolbar.setPadding(0,0,0,0);
                toolbar.setContentInsetsAbsolute(0,0);
                appbar.setPadding(0,0,0,0);
                TypedValue tv = new TypedValue();
                int height = getResources().getDimensionPixelSize(R.dimen.tab_height);
                if (requireActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    height = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
                }
                appbar.getLayoutParams().height = height;
                toolbar.getLayoutParams().height = appbar.getLayoutParams().height;
                targetView.getLayoutParams().height = appbar.getLayoutParams().height;
            }
        }
    }
}
