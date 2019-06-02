package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityInformationBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.request.OrderRequest;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.viewmodel.PayCardActivityViewModel;

import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.VOUCHER_VALUE;
import static org.koin.java.standalone.KoinJavaComponent.inject;

public class InformationActivity extends BaseActivity {

    ActivityInformationBinding binding;
    private Lazy<OrderRequest> orderRequest = inject(OrderRequest.class);
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private List<ProductModel> productModelList;
    private String voucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_information);
        voucher = getIntent().getStringExtra(VOUCHER_VALUE);
        productModelList = new ArrayList<>();
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    public void continueToPayActivity(View view) {
        if (!binding.etEmail.getText().toString().isEmpty()
                && ValidationUtils.validateTexts(binding.etEmail.getText().toString(), ValidationUtils.TYPE_EMAIL)
                && !binding.etFullName.getText().toString().isEmpty()
                && !binding.etFullAddress.getText().toString().isEmpty()
                && !binding.etCountry.getText().toString().isEmpty()
                && !binding.etRegion.getText().toString().isEmpty()
                && !binding.etZipCode.getText().toString().isEmpty()
                && !binding.etPhoneNum.getText().toString().isEmpty()
                && ValidationUtils.validateTexts(binding.etPhoneNum.getText().toString(), ValidationUtils.TYPE_PHONE)) {

            setData();
            gotoNextActivity();
        } else {
            showErrors();
        }
    }

    private void setData() {
        orderRequest.getValue().setEmail(binding.etEmail.getText().toString());
        orderRequest.getValue().setName(binding.etFullName.getText().toString());
        orderRequest.getValue().setAddress(binding.etFullAddress.getText().toString());
        orderRequest.getValue().setCountry(binding.etCountry.getText().toString());
        orderRequest.getValue().setRegion(binding.etRegion.getText().toString());
        orderRequest.getValue().setZipCode(binding.etZipCode.getText().toString());
        orderRequest.getValue().setPhoneNumber(binding.etPhoneNum.getText().toString());
        if (!voucher.isEmpty()) {
            orderRequest.getValue().setVoucher(voucher);
        }
    }

    private void gotoNextActivity() {
        Intent intent = new Intent(this, PayCardActivity.class);
        intent.putExtra(ConfigurationFile.Constants.ORDER_REQUEST, orderRequest.getValue());
        startActivity(intent);
    }

    private void showErrors() {
        if (binding.etEmail.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.enter_email));
            return;
        }
        if (!ValidationUtils.validateTexts(binding.etEmail.getText().toString(), ValidationUtils.TYPE_EMAIL)) {
            showSnackBar(getResources().getString(R.string.enter_valid_email));
            return;
        }
        if (binding.etFullName.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.enter_full_name));
            return;
        }
        if (binding.etFullAddress.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.enter_full_address));
            return;
        }
        if (binding.etCountry.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.enter_country));
            return;
        }
        if (binding.etRegion.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.enter_region));
            return;
        }
        if (binding.etZipCode.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.enter_zip_code));
            return;
        }
        if (binding.etPhoneNum.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.enter_phone_number));
            return;
        }
        if (!ValidationUtils.validateTexts(binding.etPhoneNum.getText().toString(), ValidationUtils.TYPE_PHONE)) {
            showSnackBar(getResources().getString(R.string.enter_valid_phone_number));
        }

    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}
