package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.moip.validators.CreditCard;
import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityPayCardBinding;
import m.dp.i96mg.service.model.global.BankAccountResponseModel;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.request.BankRequest;
import m.dp.i96mg.service.model.request.OrderRequest;
import m.dp.i96mg.service.model.response.BankAccountsResponse;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.model.response.OrderResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.SpinnerAdapter;
import m.dp.i96mg.viewmodel.PayCardActivityViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.BANK_ACCOUNT_ID;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.CREDIT_CARD;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.CREDIT_ID;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PAYBAL_ID;
import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PayCardActivity extends BaseActivity {

    ActivityPayCardBinding binding;
    private int type = BANK_ACCOUNT_ID;
    private Lazy<PayCardActivityViewModel> payCardActivityViewModelLazy = inject(PayCardActivityViewModel.class);
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    OrderRequest orderRequest;
    private List<ProductModel> productModelList;
    private ArrayList<ProductData> productData;
    private int orderId;
    private SpinnerAdapter bankSpinnerAdapter;
    private BankAccountResponseModel selectedBankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_card);
        orderRequest = new OrderRequest(Parcel.obtain());
        orderRequest = getIntent().getParcelableExtra(ConfigurationFile.Constants.ORDER_REQUEST);
        orderId = getIntent().getIntExtra(ConfigurationFile.Constants.ORDER_ID, 0);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        getBankAccounts();
    }

    private void getBankAccounts() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            payCardActivityViewModelLazy.getValue().getBankAccounts().observe(this, new Observer<Response<BankAccountsResponse>>() {
                @Override
                public void onChanged(Response<BankAccountsResponse> bankAccountsResponseResponse) {
                    SharedUtils.getInstance().cancelDialog();
                    if (bankAccountsResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                            && ConfigurationFile.Constants.SUCCESS_CODE_TO > bankAccountsResponseResponse.code()) {
                        if (bankAccountsResponseResponse.body() != null) {
                            initializeSpinnerAdapter(bankAccountsResponseResponse.body().getData());
                        }
                    } else {
                        showErrors(bankAccountsResponseResponse.errorBody());
                    }
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void initializeSpinnerAdapter(ArrayList<BankAccountResponseModel> data) {
        bankSpinnerAdapter = new SpinnerAdapter(PayCardActivity.this, data);
        binding.requestTypeSpinner.setAdapter(bankSpinnerAdapter);
        binding.requestTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBankAccount = (BankAccountResponseModel) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showSnackbar("please select Bank Account !!");
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.bank_radioButton:
                if (checked)
                    makeActionOnBankCredit();
                break;
            case R.id.credit_radioButton:
                if (checked)
                    makeActionOnChooseCredit();
                break;
            case R.id.mada_radioButton:
                if (checked)
                    makeActionOnMadaCredit();
                break;
            case R.id.paybal_radioButton:
                if (checked)
                    makeActionOnChooseOnPaypal();
                break;
        }
    }

    public void onRadioButtonConstrainLayoutClicked(View view) {
        switch (view.getId()) {
            case R.id.bank_constraint:
                makeActionOnBankCredit();
                break;
            case R.id.credit_onstraint:
                makeActionOnChooseCredit();
                break;
            case R.id.mada_constraint:
                makeActionOnMadaCredit();
                break;
            case R.id.paybal_constraint:
                makeActionOnChooseOnPaypal();
                break;
        }
    }

    private void makeActionOnMadaCredit() {
        binding.creditConstraintlayout.setVisibility(View.VISIBLE);
        binding.madaRadioButton.setChecked(true);
        binding.bankConstraintlayout.setVisibility(View.GONE);
        binding.bankRadioButton.setChecked(false);
        binding.tvPaybal.setVisibility(View.INVISIBLE);
        binding.paybalRadioButton.setChecked(false);
        binding.creditRadioButton.setChecked(false);
        type = CREDIT_ID;
    }

    private void makeActionOnBankCredit() {
        binding.bankConstraintlayout.setVisibility(View.VISIBLE);
        binding.bankRadioButton.setChecked(true);
        binding.madaRadioButton.setChecked(false);
        binding.tvPaybal.setVisibility(View.GONE);
        binding.paybalRadioButton.setChecked(false);
        binding.creditConstraintlayout.setVisibility(View.GONE);
        binding.creditRadioButton.setChecked(false);
        type = BANK_ACCOUNT_ID;
    }

    private void makeActionOnChooseCredit() {
        binding.creditConstraintlayout.setVisibility(View.VISIBLE);
        binding.creditRadioButton.setChecked(true);
        binding.bankConstraintlayout.setVisibility(View.GONE);
        binding.bankRadioButton.setChecked(false);
        binding.tvPaybal.setVisibility(View.GONE);
        binding.paybalRadioButton.setChecked(false);
        binding.madaRadioButton.setChecked(false);

        type = CREDIT_ID;
    }

    private void makeActionOnChooseOnPaypal() {
        binding.tvPaybal.setVisibility(View.VISIBLE);
        binding.paybalRadioButton.setChecked(true);
        binding.bankConstraintlayout.setVisibility(View.GONE);
        binding.bankRadioButton.setChecked(false);
        binding.creditConstraintlayout.setVisibility(View.GONE);
        binding.creditRadioButton.setChecked(false);
        binding.madaRadioButton.setChecked(false);

        type = PAYBAL_ID;
//        orderRequest.setPaymentMethod(PAYBAL);
    }

    public void makeOrder(View view) {
        switch (type) {
            case BANK_ACCOUNT_ID:
                makeOrderByBankAccountRequest();
                break;
            case PAYBAL_ID:
                makeOrderByPaybalRequest();
                break;
        }
    }

    private void makeOrderByBankAccountRequest() {
        if (!binding.etFullName.getText().toString().isEmpty()){
            makeBankRequest();
        }else{
            //TODO: handle errors
        }
    }

    private void makeBankRequest() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            payCardActivityViewModelLazy.getValue().payUsingBankAccount(orderId, getBankAccountRequest()).observe(this, messageResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (messageResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > messageResponseResponse.code()) {
                    if (messageResponseResponse.body() != null) {
                        showSnackbar(messageResponseResponse.body().getMessage());
                        //TODO: complete this and see how to upload images
                    }
                } else {
                    showErrors(messageResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private BankRequest getBankAccountRequest() {
        BankRequest bankRequest=new BankRequest();
        bankRequest.setBankAccountId(selectedBankAccount.getId());
        bankRequest.setFullName(binding.etFullName.getText().toString());
        return bankRequest;
    }

    private void makeOrderOnChooseCredit() {
        if (!binding.etCardNumber.getText().toString().isEmpty()
                && isCardNumberValid()
                && !binding.etExpirationDate.getText().toString().isEmpty()
                && !binding.etSecurityCode.getText().toString().isEmpty()
                && !binding.etCardHolderName.getText().toString().isEmpty()
        ) {
            setOrderRequestData();
//            makeOrderRequest();
        } else {
            showTheirErrors();
        }
    }

    private boolean isCardNumberValid() {
        return new CreditCard(binding.etCardNumber.getText().toString()).isValid();
    }

    private void setOrderRequestData() {
        orderRequest.setPaymentMethod(CREDIT_CARD);
        orderRequest.setCardNumber(binding.etCardNumber.getText().toString());
        orderRequest.setExpirationDate(binding.etExpirationDate.getText().toString());
        orderRequest.setSecurityCode(binding.etSecurityCode.getText().toString());
        orderRequest.setCardholderName(binding.etCardHolderName.getText().toString());
    }

    private void showTheirErrors() {
        if (binding.etCardNumber.getText().toString().isEmpty()) {
            showSnackbar(getResources().getString(R.string.please_enter_card_number));
            return;
        }

        if (!isCardNumberValid()) {
            showSnackbar(getResources().getString(R.string.please_enter_valid_card_number));
            return;
        }

        if (binding.etExpirationDate.getText().toString().isEmpty()) {
            showSnackbar(getResources().getString(R.string.please_enter_expiration_date));
            return;
        }

        if (binding.etSecurityCode.getText().toString().isEmpty()) {
            showSnackbar(getResources().getString(R.string.please_enter_security_code));
            return;
        }

        if (binding.etCardHolderName.getText().toString().isEmpty()) {
            showSnackbar(getResources().getString(R.string.please_enter_card_holder));
        }

    }

    private void makeOrderByPaybalRequest() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            payCardActivityViewModelLazy.getValue().payUsingPaybal(orderId).observe(this, new Observer<Response<OrderResponse>>() {
                @Override
                public void onChanged(Response<OrderResponse> orderResponseResponse) {
                    SharedUtils.getInstance().cancelDialog();
                    if (orderResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                            && ConfigurationFile.Constants.SUCCESS_CODE_TO > orderResponseResponse.code()) {
                        if (orderResponseResponse.body() != null) {
                            redirectToPayBal(orderResponseResponse.body());
                        }
                    } else {
                        showErrors(orderResponseResponse.errorBody());
                    }
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void makeActionOnType(OrderResponse body) {
        switch (type) {
            case CREDIT_ID:
                checkResponse(body);
                break;
            case PAYBAL_ID:
                redirectToPayBal(body);
                break;
        }
    }

    private void checkResponse(OrderResponse body) {
        if (body.isSuccess()) {
            showSnackbar(body.getMessage());
            new Handler().postDelayed(() -> {
                clearproductsData();
                openMainActivity();
            }, 2000);
        } else {
            showSnackbar(body.getMessage());
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearproductsData() {
        String languageType = customUtils.getValue().getSavedLanguageType();
        customUtils.getValue().clearSharedPref();
        customUtils.getValue().saveLanguageTypeToPrefs(languageType);
    }

    private void redirectToPayBal(OrderResponse body) {
        openWebPage(body.getUrl());
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
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
        showSnackbar(error);

    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}
