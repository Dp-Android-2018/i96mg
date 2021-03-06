package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
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
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.ProductsInfoModel;
import m.dp.i96mg.service.model.global.VoucherResponssData;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.ProductsOrderRequest;
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
    private ArrayList<ProductsInfoModel> productsInfoModels;
    private ArrayList<ProductsInfoModel> emptyProductsInfoModels;
    private boolean allFieldsAreOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
            productsInfoModels.clear();
            customUtilsLazy.getValue().saveProductInfoToPrefs(productsInfoModels);
        });
        productModelList = new ArrayList<>();
        productsInfoModels = new ArrayList<>();
        emptyProductsInfoModels = new ArrayList<>();
        customUtilsLazy.getValue().saveProductInfoToPrefs(emptyProductsInfoModels);
        initializeOnOperationClicked();
        checkToGetandSetProducts();
        initializeOnQuantityChangedListener();
//        initializeRecyclerView();
        checkLanguage();
    }

    private void initializeOnOperationClicked() {
        onOperationClicked = new OnOperationClicked() {
            @Override
            public void plusIconClicked(int position, ProductModel productModel) {
                if (isLoggedIn()) {
                    sendItemToDb(productModel);
                } else {
                    addThisProductToSharedPreferences(productModel);
                }
            }

            @Override
            public void minusIconClicked(int position, ProductModel productModel) {

                if (isLoggedIn()) {
                    sendItemToDb(productModel);
                } else {
                    addThisProductToSharedPreferences(productModel);
                }
            }

            @Override
            public void dataChanged(int position, ItemShopCartBinding itemShopCartBinding, ProductsInfoModel mProductsInfoModel) {
//                System.out.println("data changed here ::::::::::"+mProductsInfoModel.getEmail());
                for (int i = 0; i < productsInfoModels.size(); i++) {
                    if (productsInfoModels.get(i).getId() == mProductsInfoModel.getId()) {
                        productsInfoModels.remove(i);
                        break;
                    }
                }
                productsInfoModels.add(mProductsInfoModel);
                customUtilsLazy.getValue().saveProductInfoToPrefs(productsInfoModels);
            }
        };
    }

    private void addThisProductToSharedPreferences(ProductModel productModel) {
        for (int i = 0; i < productModelList.size(); i++) {
            if (productModelList.get(i).getId() == productModel.getId()) {
                productModelList.remove(i);
                break;
            }
        }
        productModelList.add(productModel);
        customUtilsLazy.getValue().saveProductToPrefs(productModelList);
        onQuantityChanged.onQuantityChange(true);
    }

    private void sendItemToDb(ProductModel productModel) {
        if (ValidationUtils.isConnectingToInternet(binding.getRoot().getContext())) {
            SharedUtils.getInstance().showProgressDialog(binding.getRoot().getContext());
            productDetailsViewModelLazy.getValue().addItemsToCart(getCartRequest(productModel)).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbarHere(startTripResponseResponse.body().getMessage());
                    }

                } else {
                    showErrors(startTripResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
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
            }
        };
    }

    private void getAndSetTotalPrice() {
        totalPrice = 0;
//        productModelList.clear();
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        if (productModelList != null && !productModelList.isEmpty()) {
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
            removeItemFromCartDp(item);
        } else {
            removeThisProductFromSharedPreferences(item, position);
        }
    }

    private void removeItemFromCartDp(ProductModel item) {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().removeItemFromCart(item.getId()).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbarHere(startTripResponseResponse.body().getMessage());
                        onQuantityChanged.onQuantityChange(true);
                    }
                } else {
                    showErrors(startTripResponseResponse.errorBody());
                    onQuantityChanged.onQuantityChange(true);
                }
            });
        } else {
            showSnackbarHere(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void removeThisProductFromSharedPreferences(ProductModel item, int position) {
        List<ProductModel> productModelList = new ArrayList<>();
        if (customUtilsLazy.getValue().getSavedProductsData() != null) {
            productModelList.addAll(customUtilsLazy.getValue().getSavedProductsData());
        }
        for (int i = 0; i < productModelList.size(); i++) {
            if (productModelList.get(i).getId() == item.getId()) {
                productModelList.remove(i);
                //                index = i;
                break;
            }
        }
        customUtilsLazy.getValue().saveProductToPrefs(productModelList);
        updateUiAndPrice(productModelList);
//        getAndSetTotalPrice();
//        onQuantityChanged.onQuantityChange(true);
//        makeSnakeBar(productModelList2, item, index, position);
    }

    private void updateUiAndPrice(List<ProductModel> productModelList) {
        if (productModelList != null && !productModelList.isEmpty()) {
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
            binding.textView5.setVisibility(View.GONE);
            binding.tvTotalPrice.setVisibility(View.GONE);
            binding.tvCurrency.setVisibility(View.GONE);
        }
        showSnackbarHere(getResources().getString(R.string.product_removed_successfully));
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
        if (isLoggedIn()) {
            checkFields();
            createOrder();
//            checkFields();
        } else {
            SharedUtils.getInstance().showLoginDialog(this, ConfigurationFile.Constants.SHOP_DETAILS_ACTIVITY);
//            showLoginDialog();
        }
    }

    private void checkFields() {
        if (customUtilsLazy.getValue().getSavedProductsInfo() != null) {
            productsInfoModels = customUtilsLazy.getValue().getSavedProductsInfo();
        }
        allFieldsAreOk = true;
        /*for (int i = 0; i < productsInfoModels.size(); i++) {
            if (productsInfoModels.get(i) != null) {
                if (!productsInfoModels.get(i).getEmail().isEmpty()
                        && !productsInfoModels.get(i).getPassword().isEmpty()
                        && !productsInfoModels.get(i).getType().isEmpty()
                        && !productsInfoModels.get(i).getWhatsapp().isEmpty()
                ) {
                    allFieldsAreOk = true;
                } else {
                    allFieldsAreOk = false;
//                    showSnackbarHere("please fill all data !!");
                }
            }
           *//* } else {
                showSnackbarHere("productsInfoModels is null !!");
            }*//*
        }*/
    }

    private void createOrder() {
        if (allFieldsAreOk) {
            SharedUtils.getInstance().showProgressDialog(this);
            shopDetailsActivityViewModelLazy.getValue().createOrder(getProductsOrderRequest()).observe(this, messageResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (messageResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > messageResponseResponse.code()) {
                    if (messageResponseResponse.body() != null) {
                        showSnackbarHere(messageResponseResponse.body().getMessage());
                        openNextActivity(messageResponseResponse.body().getOrderId());
                    }
                } else {
                    showErrors(messageResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbarHere("please fill all data !!");
        }
    }

    private ProductsOrderRequest getProductsOrderRequest() {
        ProductsOrderRequest productsOrderRequest = new ProductsOrderRequest();
        productsOrderRequest.setVoucher(binding.etCodeNumber.getText().toString());
        productsOrderRequest.setProductsInfoModels(productsInfoModels);
        return productsOrderRequest;
    }

    private void openNextActivity(int orderId) {
        Intent intent = new Intent(this, PayCardActivity.class);
        intent.putExtra(ConfigurationFile.Constants.ORDER_ID, orderId);
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
        SharedUtils.getInstance().showProgressDialog(this);
        shopDetailsActivityViewModelLazy.getValue().getVoucherData(binding.etCodeNumber.getText().toString());
        shopDetailsActivityViewModelLazy.getValue().getData().observe(this, voucherResponseResponse -> {
            SharedUtils.getInstance().cancelDialog();
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
            SharedUtils.getInstance().showLoginDialog(this, ConfigurationFile.Constants.SHOP_DETAILS_ACTIVITY);
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

}
