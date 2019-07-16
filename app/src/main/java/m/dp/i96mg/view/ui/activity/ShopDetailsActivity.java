package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityShopDetailsBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.VoucherResponssData;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.CheckRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ShopRecyclerViewAdapter;
import m.dp.i96mg.view.ui.callback.OnOperationClicked;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;
import m.dp.i96mg.viewmodel.PayCardActivityViewModel;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;
import m.dp.i96mg.viewmodel.ShopDetailsActivityViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ShopDetailsActivity extends BaseActivity {

    ActivityShopDetailsBinding binding;
    private List<ProductModel> productModelList;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private Lazy<ShopDetailsActivityViewModel> shopDetailsActivityViewModelLazy = inject(ShopDetailsActivityViewModel.class);
    private Lazy<PayCardActivityViewModel> payCardActivityViewModelLazy = inject(PayCardActivityViewModel.class);
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private float totalPrice;
    private OnQuantityChanged onQuantityChanged;
    private ShopRecyclerViewAdapter shopRecyclerViewAdapter;
    private int index;
    private AlertDialog dialog;
    private ArrayList<ProductData> productData;
    private float totalPriceDb;
    private OnOperationClicked onOperationClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        checkToGetandSetProducts();
        initializeOnQuantityChangedListener();
        initializeOnOperationClicked();
//        initializeRecyclerView();
        checkLanguage();
    }

    private void initializeOnOperationClicked() {
        onOperationClicked=new OnOperationClicked() {
            @Override
            public void plusIconClicked(int position, ProductModel productModel) {
                sendItemToDb(productModel);
            }

            @Override
            public void minusIconClicked(int position, ProductModel productModel) {
                sendItemToDb(productModel);
            }
        };
    }

    private void sendItemToDb(ProductModel productModel) {
        if (ValidationUtils.isConnectingToInternet(binding.getRoot().getContext())) {
            SharedUtils.getInstance().showProgressDialog(binding.getRoot().getContext());
            productDetailsViewModelLazy.getValue().addItemsToCart(getCartRequest(productModel)).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbar(startTripResponseResponse.body().getMessage());
                    }

                } else {
                    showErrors(startTripResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private CartRequest getCartRequest(ProductModel productModel) {
        CartRequest cartRequest = new CartRequest();
        ArrayList<ProductData> items = new ArrayList<>();
        items.add(new ProductData(productModel.getId()
                , productModel.getOrderedQuantity()));
        cartRequest.setItems(items);
        return cartRequest;
    }

    private void checkToGetandSetProducts() {
        if (isLoggedIn()) {
            getProductsFromDb();
        } else {
            getAndSetTotalPrice();
            initializeRecyclerView();
        }
    }

    private void getProductsFromDb() {
        SharedUtils.getInstance().showProgressDialog(this);
        shopDetailsActivityViewModelLazy.getValue().getCartItems().observe(this, cartResponseResponse -> {
            SharedUtils.getInstance().cancelDialog();
            if (cartResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                    && ConfigurationFile.Constants.SUCCESS_CODE_TO > cartResponseResponse.code()) {
                if (cartResponseResponse.body() != null) {
                    if (!cartResponseResponse.body().getData().isEmpty()) {
                        productModelList = cartResponseResponse.body().getData();
                        initializeRecyclerView();
                        binding.tvTotalPrice.setText(String.valueOf(cartResponseResponse.body().getTotal()));
                        binding.tvNoData.setVisibility(View.INVISIBLE);
                        binding.tvSwipe.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvNoData.setVisibility(View.VISIBLE);
                        binding.tvSwipe.setVisibility(View.GONE);
                        binding.tvTotalPrice.setText(ConfigurationFile.Constants.SPACE);
                    }
                }
            } else {
                showErrors(cartResponseResponse.errorBody());
            }
        });
    }

    private void checkLanguage() {
        if (customUtils.getValue().getSavedLanguageType().equals(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC)) {
            binding.ivVoucherIcon.setRotation(180);
        }
    }

    private void initializeOnQuantityChangedListener() {
        onQuantityChanged = b -> {
            if (b) {
                if (isLoggedIn()) {
                    getProductsFromDb();
                } else {
                    getAndSetTotalPrice();
                    initializeRecyclerView();
                }
//                getAndSetTotalPrice();
            }
        };
    }

    private void getAndSetTotalPrice() {
        totalPrice = 0;
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        if (productModelList != null && productModelList.size() != 0) {
            binding.tvNoData.setVisibility(View.INVISIBLE);
            binding.tvSwipe.setVisibility(View.VISIBLE);
            for (int i = 0; i < productModelList.size(); i++) {
                if (productModelList.get(i).isHasDiscount()) {
                    totalPrice += productModelList.get(i).getDiscountedPrice() * productModelList.get(i).getOrderedQuantity();
                } else {
                    totalPrice += productModelList.get(i).getOriginalPrice() * productModelList.get(i).getOrderedQuantity();
                }
            }
            if (totalPrice != 0.0) {
                binding.textView5.setVisibility(View.VISIBLE);
                binding.tvTotalPrice.setText(String.valueOf(totalPrice));
                binding.tvCurrency.setVisibility(View.VISIBLE);
            }
        } else {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.tvSwipe.setVisibility(View.GONE);
        }
    }

    private void initializeRecyclerView() {
        shopRecyclerViewAdapter = new ShopRecyclerViewAdapter(this, productModelList, onQuantityChanged, onOperationClicked);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvProducts.setHasFixedSize(true);
        binding.rvProducts.setLayoutManager(linearLayoutManager);
        binding.rvProducts.setAdapter(shopRecyclerViewAdapter);
        binding.rvProducts.setItemAnimator(new DefaultItemAnimator());
        binding.rvProducts.enableItemSwipe();
        binding.rvProducts.setItemSwipeCompleteListener(position -> getAndDeleteItem(position));
    }

    private void getAndDeleteItem(int position) {
        ProductModel item = shopRecyclerViewAdapter.getItem(position);
        if (isLoggedIn()) {
            removeItemFromCartDp(item, position);
        } else {
            removeThisProductFromSharedPreferences(item, position);
        }
    }

    private void removeItemFromCartDp(ProductModel item, int position) {
        if (ValidationUtils.isConnectingToInternet(this)) {
//            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().removeItemFromCart(item.getId()).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
//                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbar(startTripResponseResponse.body().getMessage());
                        onQuantityChanged.onQuantityChange(true);
                    }
                } else {
                    showErrors(startTripResponseResponse.errorBody());
                    onQuantityChanged.onQuantityChange(true);
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void removeThisProductFromSharedPreferences(ProductModel item, int position) {
        List<ProductModel> productModelList2 = customUtilsLazy.getValue().getSavedProductsData();
        for (int i = 0; i < productModelList2.size(); i++) {
            if (productModelList2.get(i).getId() == item.getId()) {
                productModelList2.remove(i);
                index = i;
            }
        }
        customUtilsLazy.getValue().saveProductToPrefs(productModelList2);
        onQuantityChanged.onQuantityChange(true);
        makeSnakeBar(productModelList2, item, index, position);
    }

    private void makeSnakeBar(List<ProductModel> productModelList2, ProductModel item, int index, int position) {
        Snackbar mySnackbar = Snackbar.make(binding.getRoot(), item.getName() + getResources().getString(R.string.deleted_successfully), Snackbar.LENGTH_LONG);
        mySnackbar.setAction(getResources().getString(R.string.undo_), view -> {
            shopRecyclerViewAdapter.addItem(item, position);
            productModelList2.add(index, item);
            customUtilsLazy.getValue().saveProductToPrefs(productModelList2);
            onQuantityChanged.onQuantityChange(true);
        }).setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mySnackbar.show();
    }

    public void goToPayActivity(View view) {
        if (productModelList != null && !productModelList.isEmpty()) {
            checkIfLoggedInOrNot();
        } else {
            showSnackbarHere(getResources().getString(R.string.please_add_products));
        }
    }

  /*  private void checkProducts() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            shopDetailsActivityViewModelLazy.getValue().checkProducts(getCheckRequest()).observe(this, voucherResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (voucherResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > voucherResponseResponse.code()) {

                } else {
                    showErrors(voucherResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private CheckRequest getCheckRequest() {
        productData = new ArrayList<>();
        if (productModelList != null) {
            for (int i = 0; i < productModelList.size(); i++) {
                productData.add(new ProductData(productModelList.get(i).getId(), productModelList.get(i).getOrderedQuantity()));
            }
        }
        CheckRequest checkRequest = new CheckRequest();
        checkRequest.setProductsData(productData);
        return checkRequest;
    }*/

    private void checkIfLoggedInOrNot() {
        if (customUtilsLazy.getValue().getSavedMemberData() != null) {
            createOrder();
        } else {
            showLoginDialog();
        }
    }

    private void createOrder() {
        //TODO: create order here (payCardActivityViewModelLazy)
    }

    private void showLoginDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(binding.getRoot().getContext());
        LayoutInflater factory = LayoutInflater.from(binding.getRoot().getContext());
        final View view = factory.inflate(R.layout.choose_order_type, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        makeActionOnChoose(view);
    }

    private void makeActionOnChoose(View view) {
        ConstraintLayout loginConstraintLayout = view.findViewById(R.id.loginConstraintLayout);
        loginConstraintLayout.setOnClickListener(v -> {
            dialog.cancel();
            openLoginActivity();
        });
        ConstraintLayout notNowConstraintLayout = view.findViewById(R.id.notNowConstraintLayout);
        notNowConstraintLayout.setOnClickListener(v -> {
            dialog.cancel();
        });
    }

    private void openLoginActivity() {
        Intent intent = new Intent(ShopDetailsActivity.this, LoginActivity.class);
        intent.putExtra(ConfigurationFile.Constants.ACTIVITY_NAME, ConfigurationFile.Constants.SHOP_DETAILS_ACTIVITY);
        startActivity(intent);
        finish();
    }

    private void openNextActivity() {
        Intent intent = new Intent(this, InformationActivity.class);
        intent.putExtra(ConfigurationFile.Constants.VOUCHER_VALUE, binding.etCodeNumber.getText().toString());
        startActivity(intent);
    }

    public void makeVoucherRequest(View view) {
        if (productModelList != null && !productModelList.isEmpty()) {
            if (!binding.etCodeNumber.getText().toString().isEmpty()) {
                if (ValidationUtils.isConnectingToInternet(this)) {
                    makeRequest();
                } else {
                    showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
                }
            } else {
                showSnackbarHere(getResources().getString(R.string.please_enter_code_number));
            }
        } else {
            showSnackbarHere(getResources().getString(R.string.please_add_products));
        }
    }

    private void makeRequest() {
//        SharedUtils.getInstance().showProgressDialog(this);
        shopDetailsActivityViewModelLazy.getValue().getVoucherData(binding.etCodeNumber.getText().toString());
        shopDetailsActivityViewModelLazy.getValue().getData().observe(this, voucherResponseResponse -> {
//            SharedUtils.getInstance().cancelDialog();
            if (voucherResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                    && ConfigurationFile.Constants.SUCCESS_CODE_TO > voucherResponseResponse.code()) {
                if (voucherResponseResponse.body() != null) {
                    setTotalwithDiscount(voucherResponseResponse.body().getData());
                    binding.ivDone.setVisibility(View.VISIBLE);
                }
            } else {
                showErrors(voucherResponseResponse.errorBody());
            }
        });
    }

    private void setTotalwithDiscount(VoucherResponssData data) {
        binding.textView6.setVisibility(View.VISIBLE);
        binding.etCodeNumber.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.checked), null);
        if (data.isFlat()) {
            if (data.getDiscountAmount() <= data.getMaxDiscount()) {
                binding.tvVoucher.setText(String.valueOf(data.getDiscountAmount()));
                binding.tvSr.setVisibility(View.VISIBLE);
                totalPrice = totalPrice - data.getDiscountAmount();
            } else {
                binding.tvVoucher.setText(String.valueOf(data.getMaxDiscount()));
                binding.tvSr.setVisibility(View.VISIBLE);
                totalPrice = totalPrice - data.getMaxDiscount();
            }
            binding.tvTotalPrice.setText(String.valueOf(totalPrice));
            binding.tvCurrency.setVisibility(View.VISIBLE);
        } else {
            float discountAmount = (data.getDiscountAmount() / 100) * totalPrice;
            if (discountAmount <= data.getMaxDiscount()) {
                binding.tvVoucher.setText(String.valueOf(discountAmount));
                binding.tvSr.setVisibility(View.VISIBLE);
                totalPrice = totalPrice - discountAmount;
            } else {
                binding.tvVoucher.setText(String.valueOf(data.getMaxDiscount()));
                binding.tvSr.setVisibility(View.VISIBLE);
                totalPrice = totalPrice - data.getMaxDiscount();
            }
            binding.tvTotalPrice.setText(String.valueOf(totalPrice));
            binding.tvCurrency.setVisibility(View.VISIBLE);
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

    public void showCodeConstraintLayout(View view) {
        if (customUtilsLazy.getValue().getSavedMemberData() == null) {
            showLoginDialog();
        } else {
            binding.btnVoucher.setVisibility(View.GONE);
            binding.constraintLayout2.setVisibility(View.VISIBLE);
        }
    }

    private boolean isLoggedIn() {
        if (customUtilsLazy.getValue().getSavedMemberData() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}
