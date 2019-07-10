package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivitySignUpBinding;
import m.dp.i96mg.service.model.global.LoginResponseModel;
import m.dp.i96mg.service.model.request.SignUpRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.viewmodel.SignUpViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class SignUpActivity extends BaseActivity {
    private Lazy<SignUpViewModel> signUpViewModelLazy = inject(SignUpViewModel.class);
    ActivitySignUpBinding binding;
    private String activityName;
    private LoginResponseModel loginResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        activityName = getIntent().getStringExtra(ConfigurationFile.Constants.ACTIVITY_NAME);
    }

    public void signUp(View view) {
        if (!binding.etFirstName.getText().toString().isEmpty()
                && !binding.etSecondName.getText().toString().isEmpty()
                && ValidationUtils.validateTexts(binding.etPhone.getText().toString(), ValidationUtils.TYPE_PHONE)
        ) {
            if (ValidationUtils.isConnectingToInternet(this)) {
                makeSignUpRequest();
            } else {
                showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
            }
        } else {
            showFieldsBugs();
        }
    }

    private void makeSignUpRequest() {
        SharedUtils.getInstance().showProgressDialog(this);
        signUpViewModelLazy.getValue().signUp(getSignUpRequest()).observe(this, new Observer<Response<MessageResponse>>() {
            @Override
            public void onChanged(Response<MessageResponse> messageResponseResponse) {
                SharedUtils.getInstance().cancelDialog();
                if (messageResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > messageResponseResponse.code()) {
                    showSnackbarHere(messageResponseResponse.body().getMessage());
                    addDataToCustomUtils();
                    gotoYourActivity();
                } else {
                    showErrors(messageResponseResponse.errorBody());
                }
            }
        });
    }

    private void addDataToCustomUtils() {
        loginResponseModel=customUtils.getValue().getSavedMemberData();
        loginResponseModel.setFirstName(binding.etFirstName.getText().toString());
        loginResponseModel.setLastName(binding.etSecondName.getText().toString());
        loginResponseModel.setPhone(binding.etPhone.getText().toString());
        customUtils.getValue().saveMemberDataToPrefs(loginResponseModel);
    }

    private void gotoYourActivity() {
        if (activityName.equals(ConfigurationFile.Constants.MAIN_ACTIVITY)) {
            gotoThisActivity(MainActivity.class);
        } else if (activityName.equals(ConfigurationFile.Constants.SHOP_DETAILS_ACTIVITY)) {
            gotoThisActivity(ShopDetailsActivity.class);
        }else if (activityName.equals(ConfigurationFile.Constants.PRODUCT_DETAILS_ACTIVITY)) {
            gotoThisActivity(ProductDetailsActivity.class);
        }
    }

    private void gotoThisActivity(Class activityClass) {
        Intent intent = new Intent(SignUpActivity.this, activityClass);
        intent.putExtra(ConfigurationFile.Constants.ACTIVITY_NAME, ConfigurationFile.Constants.MAIN_ACTIVITY);
        startActivity(intent);
        finish();
    }

    private SignUpRequest getSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setPhone(binding.etPhone.getText().toString());
        signUpRequest.setFirstName(binding.etFirstName.getText().toString());
        signUpRequest.setLastName(binding.etSecondName.getText().toString());
        return signUpRequest;
    }

    private void showFieldsBugs() {
        if (binding.etFirstName.getText().toString().isEmpty()) {
            showSnackbarHere("please enter first name !");
            return;
        }

        if (binding.etSecondName.getText().toString().isEmpty()) {
            showSnackbarHere("please enter second name !");
            return;
        }

        if (!ValidationUtils.validateTexts(binding.etPhone.getText().toString(), ValidationUtils.TYPE_PHONE)) {
            showSnackbarHere(getResources().getString(R.string.enter_valid_phone_number));
        }

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
}
