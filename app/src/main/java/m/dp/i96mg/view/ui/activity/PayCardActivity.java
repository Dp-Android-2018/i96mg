package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityPayCardBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.request.OrderRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.OrderResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.viewmodel.PayCardActivityViewModel;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PayCardActivity extends BaseActivity {

    ActivityPayCardBinding binding;
    private int type = ConfigurationFile.Constants.CREDIT_ID;
    private Lazy<PayCardActivityViewModel> payCardActivityViewModelLazy = inject(PayCardActivityViewModel.class);
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    OrderRequest orderRequest;
    private List<ProductModel> productModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_card);
        orderRequest = new OrderRequest(Parcel.obtain());
        orderRequest = getIntent().getParcelableExtra(ConfigurationFile.Constants.ORDER_REQUEST);
        productModelList = new ArrayList<>();
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        ArrayList<ProductData> productData = new ArrayList<>();
        if (productModelList != null) {
            for (int i = 0; i < productModelList.size(); i++) {
                productData.add(new ProductData(productModelList.get(i).getId(), productModelList.get(i).getOrderedQuantity()));
                System.out.println("products : " + productModelList.get(i).getName());
            }
        }
        if (productData!=null){
            orderRequest.setProductsData(productData);
        }
//        getBrowserResponse();
    }

   /* private void getBrowserResponse() {
        Uri data = getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = getIntent().getDataString();
            System.out.println("Deep link clicked " + uri);
            System.out.println("Deep link data " + getIntent().getDataString());
        }
    }*/

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        String str = "";
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.credit_radioButton:
                if (checked)
                    makeActionOnChooseCredit();
                break;
            case R.id.paybal_radioButton:
                if (checked)
                    makeActionOnChooseOnPaypal();
                break;
        }
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    public void onRadioButtonConstrainLayoutClicked(View view) {
        switch (view.getId()) {
            case R.id.credit_onstraint:
                makeActionOnChooseCredit();
                break;
            case R.id.paybal_constraint:
                makeActionOnChooseOnPaypal();
                break;
        }
    }

    private void makeActionOnChooseCredit() {
        binding.creditConstraintlayout.setVisibility(View.VISIBLE);
        binding.tvPaybal.setVisibility(View.INVISIBLE);
        binding.paybalRadioButton.setChecked(false);
        binding.creditRadioButton.setChecked(true);
        type = ConfigurationFile.Constants.CREDIT_ID;
        orderRequest.setPaymentMethod("CREDIT_CARD");
    }

    private void makeActionOnChooseOnPaypal() {
        binding.creditConstraintlayout.setVisibility(View.GONE);
        binding.tvPaybal.setVisibility(View.VISIBLE);
        binding.creditRadioButton.setChecked(false);
        binding.paybalRadioButton.setChecked(true);
        type = ConfigurationFile.Constants.PAYBAL_ID;
        orderRequest.setPaymentMethod("PAYPAL");
    }

    public void makeOrder(View view) {
        switch (type) {
            case ConfigurationFile.Constants.CREDIT_ID:
                makeOrderOnChooseCredit();
                break;
            case ConfigurationFile.Constants.PAYBAL_ID:
                makeOrderOnChooseOnPaypal();
                break;
        }
    }

    private void makeOrderOnChooseCredit() {

    }

    private void makeOrderOnChooseOnPaypal() {
        SharedUtils.getInstance().showProgressDialog(this);
        payCardActivityViewModelLazy.getValue().createOrder(orderRequest);
        payCardActivityViewModelLazy.getValue().getData().observe(this, new Observer<Response<OrderResponse>>() {
            @Override
            public void onChanged(Response<OrderResponse> orderResponseResponse) {
                if (orderResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > orderResponseResponse.code()) {
                    if (orderResponseResponse.body() != null) {
                        redirectToPayBal(orderResponseResponse.body());
                    }
                } else {
                    showErrors(orderResponseResponse);
                }
            }
        });
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

    private void showErrors(Response<OrderResponse> productsResponseResponse) {

        Gson gson = new GsonBuilder().create();
        ErrorResponse errorResponse = new ErrorResponse();

        try {
            errorResponse = gson.fromJson(productsResponseResponse.errorBody().string(), ErrorResponse.class);
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
