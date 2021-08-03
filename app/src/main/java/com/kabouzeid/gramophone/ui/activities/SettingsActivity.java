package com.kabouzeid.gramophone.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEColorPreference;
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreferenceFragmentCompat;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.gramophone.App;
import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.appshortcuts.DynamicShortcutManager;
import com.kabouzeid.gramophone.preferences.BlacklistPreference;
import com.kabouzeid.gramophone.preferences.BlacklistPreferenceDialog;
import com.kabouzeid.gramophone.preferences.LibraryPreference;
import com.kabouzeid.gramophone.preferences.LibraryPreferenceDialog;
import com.kabouzeid.gramophone.preferences.NowPlayingScreenPreference;
import com.kabouzeid.gramophone.preferences.NowPlayingScreenPreferenceDialog;
import com.kabouzeid.gramophone.ui.activities.base.AbsBaseActivity;
import com.kabouzeid.gramophone.util.NavigationUtil;
import com.kabouzeid.gramophone.util.PhonographColorUtil;
import com.kabouzeid.gramophone.util.PreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AbsBaseActivity implements ColorChooserDialog.ColorCallback {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        } else {
            SettingsFragment frag = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (frag != null) frag.invalidateSettings();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        switch (dialog.getTitle()) {
            case R.string.primary_color:
                ThemeStore.editTheme(this)
                        .primaryColor(selectedColor)
                        .commit();
                break;
            case R.string.accent_color:
                ThemeStore.editTheme(this)
                        .accentColor(selectedColor)
                        .commit();
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).updateDynamicShortcuts();
        }
        recreate();
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends ATEPreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static void setSummary(@NonNull Preference preference) {
            setSummary(preference,
                    PreferenceUtil.getInstance(preference.getContext())
                    .getPrefs()
                    .getString(preference.getKey(), ""));
        }

        private static void setSummary(Preference preference, @NonNull Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                preference.setSummary(stringValue);
            }
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_library);
            addPreferencesFromResource(R.xml.pref_colors);
            addPreferencesFromResource(R.xml.pref_notification);
            addPreferencesFromResource(R.xml.pref_now_playing_screen);
            addPreferencesFromResource(R.xml.pref_images);
            addPreferencesFromResource(R.xml.pref_lockscreen);
            addPreferencesFromResource(R.xml.pref_audio);
            addPreferencesFromResource(R.xml.pref_playlists);
            addPreferencesFromResource(R.xml.pref_blacklist);
        }

        @Nullable
        @Override
        public DialogFragment onCreatePreferenceDialog(Preference preference) {
            if (preference instanceof NowPlayingScreenPreference) {
                return NowPlayingScreenPreferenceDialog.newInstance();
            } else if (preference instanceof BlacklistPreference) {
                return BlacklistPreferenceDialog.newInstance();
            } else if (preference instanceof LibraryPreference) {
                return LibraryPreferenceDialog.newInstance();
            }
            return super.onCreatePreferenceDialog(preference);
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getListView().setPadding(0, 0, 0, 0);
            invalidateSettings();
            PreferenceUtil.getInstance(requireActivity()).registerOnSharedPreferenceChangedListener(this);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PreferenceUtil.getInstance(requireActivity()).unregisterOnSharedPreferenceChangedListener(this);
        }

        private void invalidateSettings() {
            final Preference generalTheme = findPreference("general_theme");
            if (generalTheme != null) {
                setSummary(generalTheme);
                generalTheme.setOnPreferenceChangeListener((preference, o) -> {
                    String themeName = (String) o;

                    setSummary(generalTheme, o);

                    ThemeStore.markChanged(requireActivity());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        // Set the new theme so that updateAppShortcuts can pull it
                        requireActivity().setTheme(PreferenceUtil.getThemeResFromPrefValue(themeName));
                        new DynamicShortcutManager(getActivity()).updateDynamicShortcuts();
                    }

                    requireActivity().recreate();
                    return true;
                });
            }

            final Preference autoDownloadImagesPolicy = findPreference("auto_download_images_policy");
            if (autoDownloadImagesPolicy != null) {
                setSummary(autoDownloadImagesPolicy);
                autoDownloadImagesPolicy.setOnPreferenceChangeListener((preference, o) -> {
                    setSummary(autoDownloadImagesPolicy, o);
                    return true;
                });
            }

            final ATEColorPreference primaryColorPref = (ATEColorPreference) findPreference("primary_color");
            if(primaryColorPref != null) {
                final int primaryColor = ThemeStore.primaryColor(requireActivity());
                primaryColorPref.setColor(primaryColor, ColorUtil.darkenColor(primaryColor));
                primaryColorPref.setOnPreferenceClickListener(preference -> {
                    new ColorChooserDialog.Builder(requireActivity(), R.string.primary_color)
                            .accentMode(false)
                            .allowUserColorInput(true)
                            .allowUserColorInputAlpha(false)
                            .preselect(primaryColor)
                            .show(getActivity());
                    return true;
                });
            }

            final ATEColorPreference accentColorPref = (ATEColorPreference) findPreference("accent_color");
            if(accentColorPref != null) {
                final int accentColor = ThemeStore.accentColor(requireActivity());
                accentColorPref.setColor(accentColor, ColorUtil.darkenColor(accentColor));
                accentColorPref.setOnPreferenceClickListener(preference -> {
                    new ColorChooserDialog.Builder(requireActivity(), R.string.accent_color)
                            .accentMode(true)
                            .allowUserColorInput(true)
                            .allowUserColorInputAlpha(false)
                            .preselect(accentColor)
                            .show(getActivity());
                    return true;
                });
            }

            TwoStatePreference colorNavBar = (TwoStatePreference) findPreference("should_color_navigation_bar");
            if(colorNavBar != null) {
                colorNavBar.setChecked(ThemeStore.coloredNavigationBar(requireActivity()));
                colorNavBar.setOnPreferenceChangeListener((preference, newValue) -> {
                    ThemeStore.editTheme(requireActivity())
                            .coloredNavigationBar((Boolean) newValue)
                            .commit();
                    requireActivity().recreate();
                    return true;
                });
            }

            final TwoStatePreference classicNotification = (TwoStatePreference) findPreference("classic_notification");
            if(classicNotification != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    classicNotification.setVisible(false);
                } else {
                    classicNotification.setChecked(PreferenceUtil.getInstance(requireActivity()).classicNotification());
                    classicNotification.setOnPreferenceChangeListener((preference, newValue) -> {
                        // Save preference
                        PreferenceUtil.getInstance(requireActivity()).setClassicNotification((Boolean) newValue);
                        return true;
                    });
                }
            }

            final TwoStatePreference coloredNotification = (TwoStatePreference) findPreference("colored_notification");
            if(coloredNotification != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    coloredNotification.setEnabled(PreferenceUtil.getInstance(requireActivity()).classicNotification());
                } else {
                    coloredNotification.setChecked(PreferenceUtil.getInstance(requireActivity()).coloredNotification());
                    coloredNotification.setOnPreferenceChangeListener((preference, newValue) -> {
                        // Save preference
                        PreferenceUtil.getInstance(requireActivity()).setColoredNotification((Boolean) newValue);
                        return true;
                    });
                }
            }

            final TwoStatePreference colorAppShortcuts = (TwoStatePreference) findPreference("should_color_app_shortcuts");
            if(colorAppShortcuts != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                    colorAppShortcuts.setVisible(false);
                } else {
                    colorAppShortcuts.setChecked(PreferenceUtil.getInstance(requireActivity()).coloredAppShortcuts());
                    colorAppShortcuts.setOnPreferenceChangeListener((preference, newValue) -> {
                        // Save preference
                        PreferenceUtil.getInstance(requireActivity()).setColoredAppShortcuts((Boolean) newValue);

                        // Update app shortcuts
                        new DynamicShortcutManager(requireActivity()).updateDynamicShortcuts();

                        return true;
                    });
                }
            }

            final TwoStatePreference useWallpaperColors = (TwoStatePreference) findPreference("use_wallpaper_colors");
            if(useWallpaperColors != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
                    useWallpaperColors.setVisible(false);
                } else {
                    if (primaryColorPref != null) {
                        primaryColorPref.setVisible(!useWallpaperColors.isChecked());
                    }

                    if (accentColorPref != null) {
                        accentColorPref.setVisible(!useWallpaperColors.isChecked());
                    }

                    useWallpaperColors.setOnPreferenceChangeListener((preference, newValue) -> {
                        boolean value = (boolean) newValue;

                        if (primaryColorPref != null) {
                            primaryColorPref.setVisible(!value);
                        }

                        if (accentColorPref != null) {
                            accentColorPref.setVisible(!value);
                        }

                        if (value) {
                            PhonographColorUtil.applyCurrentWallpaperColors();
                            float animSpeed = Settings.Global.getFloat(requireActivity().getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
                            App.getMainHandler().postDelayed(() -> requireActivity().recreate(), (int)(animSpeed * 1000));
                        }
                        return true;
                    });
                }
            }

            final TwoStatePreference enableCompactMode = (TwoStatePreference) findPreference(PreferenceUtil.ENABLE_COMPACT_MODE);
            if (enableCompactMode != null) {
                enableCompactMode.setOnPreferenceChangeListener((preference, newValue) -> {
                    ThemeStore.markChanged(requireActivity());
                    return true;
                });
            }

            final Preference equalizer = findPreference("equalizer");
            if(equalizer != null) {
                if (!hasEqualizer()) {
                    equalizer.setEnabled(false);
                    equalizer.setSummary(getResources().getString(R.string.no_equalizer));
                }
                equalizer.setOnPreferenceClickListener(preference -> {
                    NavigationUtil.openEqualizer(requireActivity());
                    return true;
                });
            }

            updateNowPlayingScreenSummary();
        }

        private boolean hasEqualizer() {
            final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            PackageManager pm = requireActivity().getPackageManager();
            ResolveInfo ri = pm.resolveActivity(effects, 0);
            return ri != null;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferenceUtil.NOW_PLAYING_SCREEN_ID:
                    updateNowPlayingScreenSummary();
                    break;
                case PreferenceUtil.CLASSIC_NOTIFICATION:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        final TwoStatePreference coloredNotification = (TwoStatePreference) findPreference("colored_notification");
                        if(coloredNotification != null) {
                            coloredNotification.setEnabled(sharedPreferences.getBoolean(key, false));
                        }
                    }
                    break;
            }
        }

        private void updateNowPlayingScreenSummary() {
            Preference preference = findPreference("now_playing_screen_id");
            if(preference != null) {
                preference.setSummary(PreferenceUtil.getInstance(requireActivity()).getNowPlayingScreen().titleRes);
            }
        }
    }
}
