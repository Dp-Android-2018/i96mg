package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityShopDetailsBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.VoucherResponssData;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.service.model.response.VoucherResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.view.ui.adapter.ShopRecyclerViewAdapter;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;
import m.dp.i96mg.viewmodel.ShopDetailsActivityViewModel;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ShopDetailsActivity extends BaseActivity {

    ActivityShopDetailsBinding binding;
    private List<ProductModel> productModelList;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private Lazy<ShopDetailsActivityViewModel> shopDetailsActivityViewModelLazy = inject(ShopDetailsActivityViewModel.class);
    private float totalPrice;
    private OnQuantityChanged onQuantityChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        getAndSetTotalPrice();
        initializeOnQuantityChangedListener();
        initializeRecyclerView();
    }

    private void initializeOnQuantityChangedListener() {
        onQuantityChanged = b -> {
            if (b) {
                getAndSetTotalPrice();
            }
        };
    }

    private void getAndSetTotalPrice() {
        totalPrice = 0;
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        if (productModelList != null) {
            for (int i = 0; i < productModelList.size(); i++) {
                if (productModelList.get(i).isHasDiscount()) {
                    totalPrice += productModelList.get(i).getDiscountedPrice() * productModelList.get(i).getOrderedQuantity();
                } else {
                    totalPrice += productModelList.get(i).getOriginalPrice() * productModelList.get(i).getOrderedQuantity();
                }
            }
            binding.tvTotalPrice.setText(String.valueOf(totalPrice));
        }
    }

    private void initializeRecyclerView() {
        ShopRecyclerViewAdapter shopRecyclerViewAdapter = new ShopRecyclerViewAdapter(productModelList, onQuantityChanged);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvProducts.setLayoutManager(linearLayoutManager);
        binding.rvProducts.setAdapter(shopRecyclerViewAdapter);
        binding.rvProducts.setItemAnimator(new DefaultItemAnimator());
    }

    public void goToPayActivity(View view) {
        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
        finish();
    }

    public void makeVoucherRequest(View view) {
        if (!binding.etCodeNumber.getText().toString().isEmpty()) {
            SharedUtils.getInstance().showProgressDialog(this);
            shopDetailsActivityViewModelLazy.getValue().getVoucherData(binding.etCodeNumber.getText().toString());
            shopDetailsActivityViewModelLazy.getValue().getData().observe(this, new Observer<Response<VoucherResponse>>() {
                @Override
                public void onChanged(Response<VoucherResponse> voucherResponseResponse) {
                    SharedUtils.getInstance().cancelDialog();
                    if (voucherResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                            && ConfigurationFile.Constants.SUCCESS_CODE_TO > voucherResponseResponse.code()) {
                        if (voucherResponseResponse.body() != null) {
                            setTotalwithDiscount(voucherResponseResponse.body().getData());
                        }
                    } else {
                        showErrors(voucherResponseResponse);
                    }
                }
            });
        } else {
            showSnackbarHere("please enter your code number !!");
        }
    }

    private void setTotalwithDiscount(VoucherResponssData data) {
        binding.textView6.setVisibility(View.VISIBLE);
        if (data.isFlat()) {
            if (data.getDiscountAmount() <= data.getMaxDiscount()) {
                binding.tvVoucher.setText(String.valueOf(data.getDiscountAmount()));
                totalPrice = totalPrice - data.getDiscountAmount();
            } else {
                binding.tvVoucher.setText(String.valueOf(data.getMaxDiscount()));
                totalPrice = totalPrice - data.getMaxDiscount();
            }
//            binding.tvTotalPrice.setText(String.valueOf(totalPrice));
        } else {
            float discountAmount = (data.getDiscountAmount() / 100) * totalPrice;
            if (discountAmount <= data.getMaxDiscount()) {
                binding.tvVoucher.setText(String.valueOf(discountAmount));
                totalPrice = totalPrice - discountAmount;
            } else {
                binding.tvVoucher.setText(String.valueOf(data.getMaxDiscount()));
                totalPrice = totalPrice - data.getMaxDiscount();
            }
//            binding.tvTotalPrice.setText(String.valueOf(totalPrice));
        }

    }

    private void showErrors(Response<VoucherResponse> productsResponseResponse) {

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
        showSnackbarHere(error);

    }

    private void showSnackbarHere(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}
