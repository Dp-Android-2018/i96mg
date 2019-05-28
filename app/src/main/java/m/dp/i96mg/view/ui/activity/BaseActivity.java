package m.dp.i96mg.view.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.Objects;

import kotlin.Lazy;
import m.dp.i96mg.application.MyApplication;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.ConnectionReceiver;
import m.dp.i96mg.utility.utils.ContextWrapper;
import m.dp.i96mg.utility.utils.CustomUtils;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class BaseActivity extends AppCompatActivity implements ConnectionReceiver.ConnectionReceiverListener {

    Lazy<CustomUtils> customUtils = inject(CustomUtils.class);
//    Lazy<MainActivityViewModel> mainActivityViewModelLazy = inject(MainActivityViewModel.class);

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

    /*public void logout() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            mainActivityViewModelLazy.getValue().logout().observe(this, voidResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (voidResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > voidResponse.code()) {
                    goToLoginPage();
                } else if (voidResponse.code() == ConfigurationFile.Constants.LOGGED_IN_BEFORE_CODE) {
                    goToLoginPage();
                } else {
                    if (voidResponse.errorBody() != null) {
                        showStartTripErrorMessage(voidResponse.errorBody());
                    }
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void goToLoginPage() {
        String languageType = customUtils.getValue().getSavedLanguageType();
        customUtils.getValue().clearSharedPref();
        customUtils.getValue().saveLanguageTypeToPrefs(languageType);
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showStartTripErrorMessage(ResponseBody errorResponseBody) {
        Gson gson = new GsonBuilder().create();
        ErrorResponse errorResponse = new ErrorResponse();

        try {
            errorResponse = gson.fromJson(errorResponseBody.string(), ErrorResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String error = "";
        for (String string : errorResponse.getErrors()) {
            error += string;
            error += "\n";
        }
        showSnackbar(error);
    }*/

    private void showSnackbar(String error) {
        Snackbar.make(Objects.requireNonNull(getCurrentFocus()), error, Snackbar.LENGTH_SHORT).show();
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
        } else {
            ConfigurationFile.Constants.ACCEPT_LANGUAGE = customUtils.getValue().getSavedLanguageType();
        }
        return customUtils.getValue().getSavedLanguageType();
    }
}
