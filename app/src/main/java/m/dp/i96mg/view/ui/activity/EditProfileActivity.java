package m.dp.i96mg.view.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;
    private Lazy<SignUpViewModel> signUpViewModelLazy = inject(SignUpViewModel.class);
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private LoginResponseModel loginResponseModel;
    private ProgressDialog prgDialog;
    private String imgPath, fileName, encodeString;
    private static int RESULT_LOAD_IMG = 1;
    private MultipartBody.Part imagePart;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
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
                || (imgPath != null && !imgPath.isEmpty())
        ) {
            if (ValidationUtils.isConnectingToInternet(this)) {
                makeSignUpRequest();

            } else {
                showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
            }
        } else {
            showSnackbarHere(getResources().getString(R.string.nothing_changed));
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
        loginResponseModel = customUtils.getValue().getSavedMemberData();
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
        signUpRequest.setImageBitmap(imagePart);
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

    public void editImage(View view) {
       /* // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();

//                if (imgPath != null && !imgPath.isEmpty()) {
//                    encodeImagetoString();
//                }
                // Get the Image's file name
                //String fileNameSegments[] = imgPath.split("/");
                //fileName = fileNameSegments[fileNameSegments.length - 1];

                //Create a file object using file path
                File file = new File(imgPath);
                // Create a request body with file and image media type
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
                // Create MultipartBody.Part using file request-body,file name and part name
                imagePart = MultipartBody.Part.createFormData("upload", "image.png", fileReqBody);

                //Create request body with text description and text media type
              //RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

            } else {
                showSnackbarHere(getResources().getString(R.string.you_havenot_picked_image));
            }
        } catch (Exception e) {
            showSnackbarHere(getResources().getString(R.string.something_went_wrong));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //your code
                    BitmapFactory.Options options = null;
                    options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    bitmap = BitmapFactory.decodeFile(imgPath, options);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
                    byte[] byte_arr = stream.toByteArray();
                    encodeString = Base64.encodeToString(byte_arr, 0);
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param

            }
        }.execute(null, null, null);
    }
}
