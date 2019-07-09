package m.dp.i96mg.view.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import kotlin.Lazy;
import m.dp.i96mg.application.MyApplication;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.ConnectionReceiver;
import m.dp.i96mg.utility.utils.ContextWrapper;
import m.dp.i96mg.utility.utils.CustomUtils;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class BaseActivity extends AppCompatActivity implements ConnectionReceiver.ConnectionReceiverListener {

    Lazy<CustomUtils> customUtils = inject(CustomUtils.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.Companion.getInstance().setConnectionListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.Companion.getInstance().setConnectionListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            Intent intent = new Intent(BaseActivity.this, NoInternetConnectionActivity.class);
            startActivity(intent);
            finish();
            finishAffinity();

        } else {
            Intent intent = new Intent(BaseActivity.this, SplashScreenActivity.class);
            startActivity(intent);
            finish();
            finishAffinity();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale = new Locale(getAppLang());

        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    public String getAppLang() {
        if (customUtils.getValue().getSavedLanguageType().equals(ConfigurationFile.Constants.DEFAULT_LANGUAGE_STRING)) {
            if (Locale.getDefault().getDisplayLanguage().equals(ConfigurationFile.Constants.ARABIC_LANGUAGE_STRING)) {
                ConfigurationFile.Constants.ACCEPT_LANGUAGE = ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC;
                customUtils.getValue().saveLanguageTypeToPrefs(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC);
            } else {
                ConfigurationFile.Constants.ACCEPT_LANGUAGE = ConfigurationFile.Constants.ACCEPT_LANGUAGE_ENGLISH;
                customUtils.getValue().saveLanguageTypeToPrefs(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ENGLISH);
            }
//            ConfigurationFile.Constants.ACCEPT_LANGUAGE = ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC;
//            customUtils.getValue().saveLanguageTypeToPrefs(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC);
        } else {
            ConfigurationFile.Constants.ACCEPT_LANGUAGE = customUtils.getValue().getSavedLanguageType();
        }
        return customUtils.getValue().getSavedLanguageType();
    }
}
