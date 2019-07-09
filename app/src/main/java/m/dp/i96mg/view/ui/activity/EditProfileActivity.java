package m.dp.i96mg.view.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityEditProfileBinding;
import m.dp.i96mg.service.model.global.LoginResponseModel;
import m.dp.i96mg.service.model.request.SignUpRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.viewmodel.SignUpViewModel;
import okhttp3.ResponseBody;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;
    private Lazy<SignUpViewModel> signUpViewModelLazy = inject(SignUpViewModel.class);
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private LoginResponseModel loginResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        setupToolbar();
        initializeUiData();
    }

    private void setupToolbar() {
        binding.ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void initializeUiData() {
        binding.etPhone.setText(customUtilsLazy.getValue().getSavedMemberData().getPhone());
        binding.etFirstName.setText(customUtilsLazy.getValue().getSavedMemberData().getFirstName());
        binding.etSecondName.setText(customUtilsLazy.getValue().getSavedMemberData().getLastName());
        ImageView ivGalleryPhoto = binding.ivUser;
        Picasso.get().load(customUtilsLazy.getValue().getSavedMemberData().getProfilePictureUrl()).into(ivGalleryPhoto);
    }

    public void updateProfileDetails(View view) {
        if (!binding.etFirstName.getText().toString().equals(customUtilsLazy.getValue().getSavedMemberData().getFirstName())
                || !binding.etSecondName.getText().toString().equals(customUtilsLazy.getValue().getSavedMemberData().getLastName())
                || !binding.etPhone.getText().toString().equals(customUtilsLazy.getValue().getSavedMemberData().getPhone())
        ) {
            if (ValidationUtils.isConnectingToInternet(this)) {
                makeSignUpRequest();
            } else {
                showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
            }
        } else {
            showSnackbarHere("nothing changed !");
        }
    }

    private void makeSignUpRequest() {
        SharedUtils.getInstance().showProgressDialog(this);
        signUpViewModelLazy.getValue().signUp(getSignUpRequest()).observe(this, messageResponseResponse -> {
            SharedUtils.getInstance().cancelDialog();
            if (messageResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                    && ConfigurationFile.Constants.SUCCESS_CODE_TO > messageResponseResponse.code()) {
                showSnackbarHere(messageResponseResponse.body().getMessage());
                addDataToCustomUtils();
            } else {
                showErrors(messageResponseResponse.errorBody());
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

    private SignUpRequest getSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setPhone(binding.etPhone.getText().toString());
        signUpRequest.setFirstName(binding.etFirstName.getText().toString());
        signUpRequest.setLastName(binding.etSecondName.getText().toString());
        return signUpRequest;
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
