package com.kabouzeid.gramophone.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.ui.activities.base.AbsBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.psdev.licensesdialog.LicensesDialog;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
@SuppressWarnings("FieldCanBeLocal")
@SuppressLint("NonConstantResourceId")
public class AboutActivity extends AbsBaseActivity implements View.OnClickListener {

    private static String KARIM_ABOU_ZEID_GITHUB = "https://github.com/kabouzeid";
    private static String KARIM_ABOU_ZEID_TWITTER = "https://twitter.com/swiftkarim";
    private static String KARIM_ABOU_ZEID_WEBSITE = "https://kabouzeid.com/";

    private static String AIDAN_FOLLESTAD_GITHUB = "https://github.com/afollestad";

    private static String MICHAEL_COOK_WEBSITE = "https://cookicons.co/";

    private static String MAARTEN_CORPEL_WEBSITE = "https://maartencorpel.com/";
    private static String MAARTEN_CORPEL_TWITTER = "https://twitter.com/maartencorpel";

    private static String ALEKSANDAR_TESIC_TWITTER = "https://twitter.com/djsalezmaj";

    private static String EUGENE_CHEUNG_GITHUB = "https://github.com/arkon";
    private static String EUGENE_CHEUNG_WEBSITE = "https://echeung.me/";

    private static String ADRIAN_TWITTER = "https://twitter.com/froschgames";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.licenses)
    LinearLayout licenses;
    @BindView(R.id.karim_abou_zeid_email)
    AppCompatButton karimAbouZeidEmail;
    @BindView(R.id.karim_abou_zeid_git_hub)
    AppCompatButton karimAbouZeidGitHub;
    @BindView(R.id.karim_abou_zeid_website)
    AppCompatButton karimAbouZeidWebsite;
    @BindView(R.id.karim_abou_zeid_twitter)
    AppCompatButton karimAbouZeidTwitter;
    @BindView(R.id.aidan_follestad_git_hub)
    AppCompatButton aidanFollestadGitHub;
    @BindView(R.id.michael_cook_website)
    AppCompatButton michaelCookWebsite;
    @BindView(R.id.maarten_corpel_website)
    AppCompatButton maartenCorpelWebsite;
    @BindView(R.id.maarten_corpel_twitter)
    AppCompatButton maartenCorpelTwitter;
    @BindView(R.id.aleksandar_tesic_twitter)
    AppCompatButton aleksandarTesicTwitter;
    @BindView(R.id.eugene_cheung_git_hub)
    AppCompatButton eugeneCheungGitHub;
    @BindView(R.id.eugene_cheung_website)
    AppCompatButton eugeneCheungWebsite;
    @BindView(R.id.adrian_twitter)
    AppCompatButton adrianTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        setUpViews();
    }

    private void setUpViews() {
        setUpToolbar();
        setUpAppVersion();
        setUpOnClickListeners();
    }

    private void setUpToolbar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAppVersion() {
        appVersion.setText(getCurrentVersionName(this));
    }

    private void setUpOnClickListeners() {
        licenses.setOnClickListener(this);
        karimAbouZeidGitHub.setOnClickListener(this);
        karimAbouZeidTwitter.setOnClickListener(this);
        karimAbouZeidWebsite.setOnClickListener(this);
        karimAbouZeidEmail.setOnClickListener(this);
        aidanFollestadGitHub.setOnClickListener(this);
        michaelCookWebsite.setOnClickListener(this);
        maartenCorpelWebsite.setOnClickListener(this);
        maartenCorpelTwitter.setOnClickListener(this);
        aleksandarTesicTwitter.setOnClickListener(this);
        eugeneCheungGitHub.setOnClickListener(this);
        eugeneCheungWebsite.setOnClickListener(this);
        adrianTwitter.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getCurrentVersionName(@NonNull final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + " MOD";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Unkown";
    }

    @Override
    public void onClick(View v) {
        if (v == licenses) {
            showLicenseDialog();
        } else if (v == karimAbouZeidTwitter) {
            openUrl(KARIM_ABOU_ZEID_TWITTER);
        } else if (v == karimAbouZeidGitHub) {
            openUrl(KARIM_ABOU_ZEID_GITHUB);
        } else if (v == karimAbouZeidWebsite) {
            openUrl(KARIM_ABOU_ZEID_WEBSITE);
        } else if (v == karimAbouZeidEmail) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:contact@kabouzeid.com"));
            intent.putExtra(Intent.EXTRA_EMAIL, "contact@kabouzeid.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Phonograph");
            startActivity(Intent.createChooser(intent, "E-Mail"));
        } else if (v == aidanFollestadGitHub) {
            openUrl(AIDAN_FOLLESTAD_GITHUB);
        } else if (v == michaelCookWebsite) {
            openUrl(MICHAEL_COOK_WEBSITE);
        } else if (v == maartenCorpelWebsite) {
            openUrl(MAARTEN_CORPEL_WEBSITE);
        } else if (v == maartenCorpelTwitter) {
            openUrl(MAARTEN_CORPEL_TWITTER);
        } else if (v == aleksandarTesicTwitter) {
            openUrl(ALEKSANDAR_TESIC_TWITTER);
        } else if (v == eugeneCheungGitHub) {
            openUrl(EUGENE_CHEUNG_GITHUB);
        } else if (v == eugeneCheungWebsite) {
            openUrl(EUGENE_CHEUNG_WEBSITE);
        } else if (v == adrianTwitter) {
            openUrl(ADRIAN_TWITTER);
        }
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void showLicenseDialog() {
        new LicensesDialog.Builder(this)
                .setNotices(R.raw.notices)
                .setTitle(R.string.licenses)
                .setNoticesCssStyle(getString(R.string.license_dialog_style)
                        .replace("{bg-color}", ThemeSingleton.get().darkTheme ? "424242" : "ffffff")
                        .replace("{text-color}", ThemeSingleton.get().darkTheme ? "ffffff" : "000000")
                        .replace("{license-bg-color}", ThemeSingleton.get().darkTheme ? "535353" : "eeeeee")
                )
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }
}
