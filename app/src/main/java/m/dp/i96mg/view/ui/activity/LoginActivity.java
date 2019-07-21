package m.dp.i96mg.view.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.poovam.pinedittextfield.LinePinField;
import com.poovam.pinedittextfield.PinField;
import com.poovam.pinedittextfield.SquarePinField;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityLoginBinding;
import m.dp.i96mg.service.model.global.LoginResponseModel;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.LoginRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.LoginResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.viewmodel.LoginViewModel;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class LoginActivity extends BaseActivity {

    private Lazy<LoginViewModel> loginViewModelLazy = inject(LoginViewModel.class);
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    ActivityLoginBinding binding;
    private String email;
    private String pinCode;
    private String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setupToolbar();
        activityName = getIntent().getStringExtra(ConfigurationFile.Constants.ACTIVITY_NAME);
        makeActionOnPinText();
    }

    private void setupToolbar() {
        binding.ivBack.setOnClickListener(view -> onBackPressed());
    }

    public void sendCode(View view) {
        if (ValidationUtils.validateTexts(binding.etEmail.getText().toString(), ValidationUtils.TYPE_EMAIL)) {
            if (ValidationUtils.isConnectingToInternet(this)) {
                SharedUtils.getInstance().showProgressDialog(this);
                loginViewModelLazy.getValue().sendLoginCode(getEmailRequest()).observe(this, new Observer<Response<MessageResponse>>() {
                    @Override
                    public void onChanged(Response<MessageResponse> messageResponseResponse) {
                        SharedUtils.getInstance().cancelDialog();
                        if (messageResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                                && ConfigurationFile.Constants.SUCCESS_CODE_TO > messageResponseResponse.code()) {
                            showSnackbarHere(messageResponseResponse.body().getMessage());
                            showCodePinEntry();
                        } else {
                            showErrors(messageResponseResponse.errorBody());
                        }
                    }
                });
            } else {
                showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
            }
        } else {
            showSnackbarHere(getResources().getString(R.string.enter_valid_email));
        }
    }

    private void showCodePinEntry() {
        binding.etEmail.setVisibility(View.INVISIBLE);
        binding.btnCountinue.setVisibility(View.INVISIBLE);
        binding.textView12.setText(getResources().getString(R.string.registration_code));
        binding.btnResend.setVisibility(View.VISIBLE);
        binding.sfPin.setVisibility(View.VISIBLE);
    }

    private LoginRequest getEmailRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(binding.etEmail.getText().toString());
        email = binding.etEmail.getText().toString();
        return loginRequest;
    }

    private void makeActionOnPinText() {
        final SquarePinField sfPin = binding.sfPin;
        sfPin.setOnTextCompleteListener(enteredText -> {
            pinCode = enteredText;
            makeLoginRequest();
            return false; // Return true to keep the keyboard open else return false to close the keyboard
        });
    }

    private void makeLoginRequest() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            loginViewModelLazy.getValue().login(getLoginRequest()).observe(this, loginResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (loginResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > loginResponseResponse.code()) {
                    saveDataToSharedPreferences(loginResponseResponse.body().getData());
                    openNextActivity();
                } else {
                    showErrors(loginResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private LoginRequest getLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setCode(pinCode);
        return loginRequest;
    }

    private void openNextActivity() {
        ConfigurationFile.Constants.AUTHORIZATION = customUtilsLazy.getValue().getSavedMemberData().getToken();
        if (customUtilsLazy.getValue().getSavedProductsData() != null) {
            if (!customUtilsLazy.getValue().getSavedProductsData().isEmpty()) {
                sendCartToDb();
            }
        }else {
            openActivity();
        }
    }

    private void sendCartToDb() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().addItemsToCart(getCartRequest()).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    List<ProductModel> productModels = customUtilsLazy.getValue().getSavedProductsData();
                    productModels.clear();
                    customUtilsLazy.getValue().saveProductToPrefs(productModels);
                        showSnackbar(startTripResponseResponse.body().getMessage());
                    openActivity();
                } else {
                    showErrors(startTripResponseResponse.errorBody());
                    new Handler().postDelayed(() -> openActivity(), 1000);

                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void openActivity() {
        if (customUtilsLazy.getValue().getSavedMemberData().isCompletedRegistration()) {
            gotoYourActivity();
        } else {
            goToSignUpActivity();
        }
    }

    private CartRequest getCartRequest() {
        CartRequest cartRequest = new CartRequest();
        ArrayList<ProductData> items = new ArrayList<>();
        for (int i = 0; i < customUtilsLazy.getValue().getSavedProductsData().size(); i++) {
            items.add(new ProductData(customUtilsLazy.getValue().getSavedProductsData().get(i).getId()
                    , customUtilsLazy.getValue().getSavedProductsData().get(i).getQuantity()));

        }
        cartRequest.setItems(items);
        return cartRequest;
    }

    private void gotoYourActivity() {
        if (activityName.equals(ConfigurationFile.Constants.MAIN_ACTIVITY)) {
            gotoThisActivity(MainActivity.class);
        } else if (activityName.equals(ConfigurationFile.Constants.SHOP_DETAILS_ACTIVITY)) {
            gotoThisActivity(ShopDetailsActivity.class);
        } else if (activityName.equals(ConfigurationFile.Constants.PRODUCT_DETAILS_ACTIVITY)) {
            gotoThisActivity(ProductDetailsActivity.class);
        }
    }

    private void gotoThisActivity(Class activityClass) {
        Intent intent = new Intent(LoginActivity.this, activityClass);
        startActivity(intent);
        finish();
    }

    private void goToSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        intent.putExtra(ConfigurationFile.Constants.ACTIVITY_NAME, activityName);
        startActivity(intent);
        finish();
    }

    private void saveDataToSharedPreferences(LoginResponseModel loginResponseModel) {
        String languageType = customUtils.getValue().getSavedLanguageType();
        customUtils.getValue().clearSharedPref();
        customUtils.getValue().saveLanguageTypeToPrefs(languageType);
        customUtilsLazy.getValue().saveMemberDataToPrefs(loginResponseModel);
        ConfigurationFile.Constants.AUTHORIZATION = customUtilsLazy.getValue().getSavedMemberData().getToken();
    }

    private void showErrors(ResponseBody productsResponseResponse) {

        Gson gson = new GsonBuilder().create();
        ErrorResponse errorResponse = new ErrorResponse();

        try {
            errorResponse = gson.fromJson(productsResponseResponse.string(), ErrorResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String error = "";
        for (String string : errorResponse.getErrors()) {
            error += string;
            error += "\n";
        }
        showSnackbarHere(error);

    }

    private void showSnackbarHere(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    public void resendCode(View view) {

    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}
