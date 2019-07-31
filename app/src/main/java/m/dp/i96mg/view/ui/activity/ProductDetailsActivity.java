package m.dp.i96mg.view.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityProductDetailsBinding;
import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.ReviewResponseData;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.ReviewRequest;
import m.dp.i96mg.service.model.request.WishListRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ProductsRecyclerViewAdapter;
import m.dp.i96mg.view.ui.adapter.ReviewsRecyclerViewAdapter;
import m.dp.i96mg.view.ui.callback.OnItemClickListener;
import m.dp.i96mg.viewmodel.MainActivityViewModel;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ProductDetailsActivity extends BaseActivity {

    ActivityProductDetailsBinding binding;
    private int productId;
    private ProductModel productModel;
    private int quantity = ConfigurationFile.Constants.DEFAULT_QUANTITY;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private int pageId = ConfigurationFile.Constants.DEFAULT_PAGE_ID;
    private String categoryId = ConfigurationFile.Constants.DEFAULT_CATEGORY_ID;
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private Lazy<MainActivityViewModel> mainActivityViewModelLazy = inject(MainActivityViewModel.class);
    private List<ProductModel> productModelList;
    private ArrayList<ProductModel> allProducts;
    private float ratingValue;
    private AlertDialog dialog;
    private OnItemClickListener onItemClickListener;
    private ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private Context context;
    private boolean isInCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initializeOnItemClickListener();
        initializeVariables();
        makeActionOnToolbat();
        makeActionOnClickOnQuantityIcons();
        makeActionOnRatingChanged();
    }

    private void initializeOnItemClickListener() {
        onItemClickListener = new OnItemClickListener() {

            @Override
            public void addItemToWishList(int id, ItemProductLayoutBinding binding) {
                if (isLoggedIn()) {
                    addItemToWishListDp(id, binding);
                } else {
                    SharedUtils.getInstance().showLoginDialog(context, ConfigurationFile.Constants.PRODUCT_DETAILS_ACTIVITY);
                }
            }

            @Override
            public void removeItemFromWishList(int id, ItemProductLayoutBinding binding) {
                if (isLoggedIn()) {
                    removeItemFromWishListDp(id, binding);
                } else {
                    SharedUtils.getInstance().showLoginDialog(context, ConfigurationFile.Constants.PRODUCT_DETAILS_ACTIVITY);
                }
            }

            @Override
            public void addItemToCart(ProductModel productModel, ItemProductLayoutBinding binding) {
                if (productModel.isInCart()) {
                    showSnackbar(getResources().getString(R.string.item_added_before));
                } else {
                    addToCart(productModel);
                }
            }
        };
    }

    private boolean isLoggedIn() {
        if (customUtilsLazy.getValue().getSavedMemberData() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void removeItemFromWishListDp(int id, ItemProductLayoutBinding binding) {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().removeItemFromWishlist(id).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        removeAndShowMessage(id, binding);
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

    private void removeAndShowMessage(int id, ItemProductLayoutBinding binding) {
        binding.ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.heart_empty));
        for (int i = 0; i < allProducts.size(); i++) {
            if (allProducts.get(i).getId() == id) {
                ProductModel productModel2 = allProducts.get(i);
                productModel2.setInWishlist(false);
                allProducts.set(i, productModel2);
                break;
            }
        }
        productsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItemToWishListDp(int id, ItemProductLayoutBinding binding) {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().addItemsToWishList(getWishListRequest(id)).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        addAndShowMessage(id, binding);
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

    private void addAndShowMessage(int id, ItemProductLayoutBinding binding) {
        binding.ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.heart_filled));
        for (int i = 0; i < allProducts.size(); i++) {
            if (allProducts.get(i).getId() == id) {
                ProductModel productModel2 = allProducts.get(i);
                productModel2.setInWishlist(true);
                allProducts.set(i, productModel2);
                break;
            }
        }
        productsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private WishListRequest getWishListRequest(int id) {
        WishListRequest wishListRequest = new WishListRequest();
        wishListRequest.setProductId(id);
        return wishListRequest;
    }

    private void makeActionOnRatingChanged() {
        RatingBar ratingBar = binding.commentRatingBar;
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> ratingValue = rating);
    }

    private void makeActionOnToolbat() {
        binding.ivBack.setOnClickListener(v -> openMainActivity());
    }

    private void openMainActivity() {
        Intent intent = new Intent(ProductDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void makeActionOnClickOnQuantityIcons() {
        binding.tvPlus.setOnClickListener(v -> {
            if (quantity < productModel.getQuantity()) {
                quantity++;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
            }
        });

        binding.tvMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
            }
        });
    }

    private void initializeVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        productModel = new ProductModel(Parcel.obtain());
        allProducts = new ArrayList<>();
        allProducts = getIntent().getParcelableArrayListExtra(ConfigurationFile.Constants.PRODUCTS_LIST);
        productId = getIntent().getIntExtra(ConfigurationFile.Constants.PRODUCT_ID, 0);
        productModelList = new ArrayList<>();
        if (customUtilsLazy.getValue().getSavedProductsData() != null) {
            productModelList.addAll(customUtilsLazy.getValue().getSavedProductsData());
        }

        if (!isLoggedIn()) {
            for (int i = 0; i < productModelList.size(); i++) {
                if (productModelList.get(i).getId() == productModel.getId()) {
                    isInCart = true;
                    break;
                }
            }
        }
        getProductDetail();
        getRandomProducts();
        getReviews();
    }

    private void getProductDetail() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().getProductById(productId).observe(this, productDetailsResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (productDetailsResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > productDetailsResponseResponse.code()) {
                    if (productDetailsResponseResponse.body() != null) {
                        ProductModel productModel2 = productDetailsResponseResponse.body().getData();
                        if (isInCart) {
                            productModel2.setInCart(isInCart);
                        }
                        initializeUiWithData(productModel2);

                    }
                } else {
                    showErrors(productDetailsResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }

    }

    private void initializeUiWithData(ProductModel productModel) {
//        checkQuantityValue();
        this.productModel = productModel;
        ImageView ivGalleryPhoto = binding.ivProductImage;
        Picasso.get().load(productModel.getImageUrl()).into(ivGalleryPhoto);

        ivGalleryPhoto = binding.ivUser;
        if (productModel.getOrderedQuantity() != 0) {
            quantity = productModel.getOrderedQuantity();
            binding.tvQuantityNum.setText(String.valueOf(quantity));
        } else {
            quantity = 1;
            binding.tvQuantityNum.setText(String.valueOf(quantity));

        }
        binding.tvName.setText(productModel.getName());
        binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
        binding.tvDescribtion.setText(String.valueOf(productModel.getDescription()));
        binding.ratingBar.setRating(productModel.getRating());
        if (isLoggedIn()) {
            Picasso.get().load(customUtilsLazy.getValue().getSavedMemberData().getProfilePictureUrl()).into(ivGalleryPhoto);
        }
        if (productModel.isInCart()) {
            binding.quantityConstraint.setVisibility(View.GONE);
            binding.tvAddToCart.setText(getResources().getString(R.string.remove_from_cart));
        } else {
            binding.quantityConstraint.setVisibility(View.VISIBLE);
            binding.tvAddToCart.setText(getResources().getString(R.string.add_to_cart));
        }
        if (productModel.isHasDiscount()) {
//            binding.tvDiscountedPrice.setText(String.valueOf(productModel.getOriginalPrice()));
//            binding.tvDiscountedPrice.setPaintFlags(binding.tvDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            binding.tvSr.setPaintFlags(binding.tvDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            binding.tvDiscountedPrice.setTextColor(Color.GRAY);
//            binding.tvSr.setTextColor(Color.GRAY);
//            binding.tvDiscountedPrice.setTextSize(15);
//            binding.tvSr.setTextSize(15);
//            binding.tvSr.setVisibility(View.VISIBLE);
            binding.tvPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
            int discountRatio = (int) ((productModel.getOriginalPrice() - productModel.getDiscountedPrice()) * 100 / productModel.getOriginalPrice());
            String discountValue = discountRatio + ConfigurationFile.Constants.PERCENT + binding.getRoot().getResources().getString(R.string.off_percent);
            binding.tvDiscountRatio.setVisibility(View.VISIBLE);
            binding.tvDiscountRatio.setText(discountValue);
        }
    }

    private void getRandomProducts() {
        if (ValidationUtils.isConnectingToInternet(Objects.requireNonNull(this))) {
//            SharedUtils.getInstance().showProgressDialog(this);
            mainActivityViewModelLazy.getValue().getProducts(categoryId, pageId);
            observeViewmodel();
        } else {
            Snackbar.make(binding.getRoot(), R.string.there_is_no_internet_connection, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void observeViewmodel() {
        mainActivityViewModelLazy.getValue().getData().observe(this, productsResponseResponse -> {
            SharedUtils.getInstance().cancelDialog();
            if (productsResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                    && ConfigurationFile.Constants.SUCCESS_CODE_TO > productsResponseResponse.code()) {
                if (productsResponseResponse.body() != null) {
                    if (!productsResponseResponse.body().getData().isEmpty()) {
                        allProducts=productsResponseResponse.body().getData();
                        initializeAllProductsRecyclerView();
                    }
                }
            } else {
                showErrors(productsResponseResponse.errorBody());
            }

        });
    }

    private void initializeAllProductsRecyclerView() {
        if (allProducts != null) {
            for (int i = 0; i < allProducts.size(); i++) {
                if (allProducts.get(i).getId() == productId) {
                    allProducts.remove(i);
                    break;
                }
            }
            productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(allProducts, ConfigurationFile.Constants.DEFAULT_TYPE, onItemClickListener);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            binding.rvProducts.setLayoutManager(linearLayoutManager);
            binding.rvProducts.setAdapter(productsRecyclerViewAdapter);
        }
    }

    private void getReviews() {
        if (ValidationUtils.isConnectingToInternet(this)) {
//            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().getProductReviews(productId).observe(this, productReviewsResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (productReviewsResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > productReviewsResponseResponse.code()) {
                    if (productReviewsResponseResponse.body() != null) {
                        initializeReviewsRecyclerView(productReviewsResponseResponse.body().getData());
                    }
                } else {
                    showErrors(productReviewsResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }

    }

    private void initializeReviewsRecyclerView(ArrayList<ReviewResponseData> reviewResponseData) {
        ReviewsRecyclerViewAdapter reviewsRecyclerViewAdapter = new ReviewsRecyclerViewAdapter(reviewResponseData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        binding.rvComments.setLayoutManager(linearLayoutManager);
        binding.rvComments.setAdapter(reviewsRecyclerViewAdapter);
    }

    private void showErrors(ResponseBody responseBody) {

        Gson gson = new GsonBuilder().create();
        ErrorResponse errorResponse = new ErrorResponse();

        try {
            errorResponse = gson.fromJson(responseBody.string(), ErrorResponse.class);
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


  /*  private void checkQuantityValue() {
        if (productModel.getQuantity() == 0) {
//            binding.text2.setVisibility(View.INVISIBLE);
            binding.quantityConstraint.setVisibility(View.INVISIBLE);
            binding.tvAddToCart.setText(getResources().getString(R.string.product_is_not_available));
            binding.btnAddToCart.setClickable(false);
        }
    }*/


    public void addProductToCart(View view) {
        if (productModel.isInCart()) {
            removeFromCart(productModel);
        } else {
            addToCart(productModel);
        }
    }

    private void removeFromCart(ProductModel productModel) {
        if (isLoggedIn()) {
            removeItemFromCartDp(productModel);
        } else {
            removeThisProductFromSharedPreferences(productModel);
            showSnackbar(getResources().getString(R.string.product_removed_successfully));
            ProductModel productModel2 = productModel;
            productModel2.setInCart(false);
            initializeUiWithData(productModel2);
        }
    }

    private void removeItemFromCartDp(ProductModel productModel) {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().removeItemFromCart(productModel.getId()).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbar(startTripResponseResponse.body().getMessage());
                        ProductModel productModel2 = productModel;
                        productModel2.setInCart(false);
                        initializeUiWithData(productModel2);
                    }
                } else {
                    showErrors(startTripResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void removeThisProductFromSharedPreferences(ProductModel item) {
        List<ProductModel> productModelList2 = customUtilsLazy.getValue().getSavedProductsData();
        for (int i = 0; i < productModelList2.size(); i++) {
            if (productModelList2.get(i).getId() == item.getId()) {
                productModelList2.remove(i);
            }
        }
        customUtilsLazy.getValue().saveProductToPrefs(productModelList2);
    }

    private void addToCart(ProductModel productModel) {
        if (isLoggedIn()) {
            sendItemToDb(productModel);
        } else {
            addItsDataToSharedPreferences(productModel);
            showSnackbar(getResources().getString(R.string.product_added_successfully));
            ProductModel productModel2 = productModel;
            productModel2.setInCart(true);
            initializeUiWithData(productModel2);
        }
    }

    private void sendItemToDb(ProductModel productModel) {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().addItemsToCart(getCartRequest()).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbar(startTripResponseResponse.body().getMessage());
                        ProductModel productModel2 = productModel;
                        productModel2.setInCart(true);
                        initializeUiWithData(productModel2);
                    }

                } else {
                    showErrors(startTripResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private CartRequest getCartRequest() {
        productModel.setOrderedQuantity(quantity);
        CartRequest cartRequest = new CartRequest();
        ArrayList<ProductData> items = new ArrayList<>();
        items.add(new ProductData(productModel.getId()
                , productModel.getOrderedQuantity()));
        cartRequest.setItems(items);
        return cartRequest;
    }

    private void addItsDataToSharedPreferences(ProductModel productModel) {
        productModel.setOrderedQuantity(quantity);
        for (int i = 0; i < productModelList.size(); i++) {
            if (productModelList.get(i).getId() == productModel.getId()) {
                productModelList.remove(i);
            }
        }
        productModelList.add(productModel);
        customUtilsLazy.getValue().saveProductToPrefs(productModelList);
    }

    public void sendReview(View view) {
        if (ValidationUtils.isConnectingToInternet(Objects.requireNonNull(this))) {
            if (customUtilsLazy.getValue().getSavedMemberData() == null) {
                SharedUtils.getInstance().showLoginDialog(this, ConfigurationFile.Constants.PRODUCT_DETAILS_ACTIVITY);
                showLoginDialog();
            } else {
                rateTrip();
            }
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
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
        notNowConstraintLayout.setOnClickListener(v -> dialog.cancel());
    }

    private void openLoginActivity() {
        Intent intent = new Intent(ProductDetailsActivity.this, LoginActivity.class);
        intent.putExtra(ConfigurationFile.Constants.ACTIVITY_NAME, ConfigurationFile.Constants.PRODUCT_DETAILS_ACTIVITY);
        startActivity(intent);
        finish();
    }


    private void rateTrip() {
        if (ratingValue != 0.0) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().postReview(getReviewRequest()).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbar(startTripResponseResponse.body().getMessage());
                        clearAllFields();
                        getReviews();
                    }

                } else {
                    showErrors(startTripResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.make_rate_please));
        }
    }

    private void clearAllFields() {
        binding.ratingBar.setRating(0);
        binding.etQuestion.setText("");
    }

    private ReviewRequest getReviewRequest() {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(productId);
        reviewRequest.setRating(ratingValue);
        reviewRequest.setReview(binding.etQuestion.getText().toString());
        return reviewRequest;
    }
}
