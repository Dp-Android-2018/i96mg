package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.graphics.Color;
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
import androidx.lifecycle.Observer;
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
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.ReviewResponseData;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.ReviewRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.ProductDetailsResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ProductsRecyclerViewAdapter;
import m.dp.i96mg.view.ui.adapter.ReviewsRecyclerViewAdapter;
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
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private float price;
    private List<ProductModel> productModelList;
    private ArrayList<ProductModel> allProducts;
    private float ratingValue;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeVariables();
        makeActionOnToolbat();
        makeActionOnClickOnQuantityIcons();
        makeActionOnRatingChanged();
    }

    private void makeActionOnRatingChanged() {
        RatingBar ratingBar = binding.commentRatingBar;
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> ratingValue = rating);
    }

    private void makeActionOnToolbat() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
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
        getProductDetail();
        initializeAllProductsRecyclerView();
        getReviews();
    }

    private void getProductDetail() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().getProductById(productId).observe(this, new Observer<Response<ProductDetailsResponse>>() {
                @Override
                public void onChanged(Response<ProductDetailsResponse> productDetailsResponseResponse) {
                    SharedUtils.getInstance().cancelDialog();
                    if (productDetailsResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                            && ConfigurationFile.Constants.SUCCESS_CODE_TO > productDetailsResponseResponse.code()) {
                        if (productDetailsResponseResponse.body() != null) {
                            initializeUiWithData(productDetailsResponseResponse.body().getData());
                        }
                    } else {
                        showErrors(productDetailsResponseResponse.errorBody());
                    }
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }

    }

    private void initializeUiWithData(ProductModel productModel) {
//        checkQuantityValue();
        this.productModel = productModel;
        if (productModelList != null) {
            for (int i = 0; i < productModelList.size(); i++) {
                if (productModelList.get(i).getId() == productModel.getId()) {
                    binding.tvQuantityNum.setText(String.valueOf(productModelList.get(i).getOrderedQuantity()));
                    quantity = productModelList.get(i).getOrderedQuantity();
                }
            }
            ImageView ivGalleryPhoto = binding.ivProductImage;
            Picasso.get().load(productModel.getImageUrl()).into(ivGalleryPhoto);
            binding.tvName.setText(productModel.getName());
            binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
            binding.tvDescribtion.setText(String.valueOf(productModel.getDescription()));
            binding.ratingBar.setRating(productModel.getRating());
            /*  if (productModel.isHasDiscount()) {
             *//*  binding.tvDiscountedPrice.setText(String.valueOf(productModel.getOriginalPrice()));
                binding.tvDiscountedPrice.setPaintFlags(binding.tvDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvSr.setPaintFlags(binding.tvDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvDiscountedPrice.setTextColor(Color.GRAY);
                binding.tvSr.setTextColor(Color.GRAY);
                binding.tvDiscountedPrice.setTextSize(15);
                binding.tvSr.setTextSize(15);
                binding.tvSr.setVisibility(View.VISIBLE);*//*
                binding.tvPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
                int discountRatio = (int) ((productModel.getOriginalPrice() - productModel.getDiscountedPrice()) * 100 / productModel.getOriginalPrice());
                String discountValue = discountRatio + ConfigurationFile.Constants.PERCENT + binding.getRoot().getResources().getString(R.string.off_percent);
                binding.tvDiscountRatio.setVisibility(View.VISIBLE);
                binding.tvDiscountRatio.setText(discountValue);
            }*/
        }
    }

    private void initializeAllProductsRecyclerView() {
        if (allProducts != null) {
            for (int i = 0; i < allProducts.size(); i++) {
                if (allProducts.get(i) == productModel) {
                    allProducts.remove(productModel);
                    break;
                }
            }

            ProductsRecyclerViewAdapter productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(allProducts, ConfigurationFile.Constants.DEFAULT_TYPE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            binding.rvProducts.setLayoutManager(linearLayoutManager);
            binding.rvProducts.setAdapter(productsRecyclerViewAdapter);
        }
    }

    private void getReviews() {
        if (ValidationUtils.isConnectingToInternet(this)) {
//            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().getProductReviews(productId).observe(this, productReviewsResponseResponse -> {
//                SharedUtils.getInstance().cancelDialog();
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


    private void checkQuantityValue() {
        if (productModel.getQuantity() == 0) {
//            binding.text2.setVisibility(View.INVISIBLE);
            binding.quantityConstraint.setVisibility(View.INVISIBLE);
            binding.tvAddToCart.setText(getResources().getString(R.string.product_is_not_available));
            binding.btnAddToCart.setClickable(false);
        }
    }


    public void addProductToCart(View view) {
        if (customUtilsLazy.getValue().getSavedMemberData() == null) {
            addItsDataToSharedPreferences();
            showSnackbar("product added to cart successfully");
//            onBackPressed();
        } else {
            addItsDataToSharedPreferences();
            sendItemToDb();
//            onBackPressed();
        }

    }

    private void sendItemToDb() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().addItemsToCart(getCartRequest()).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
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

    private CartRequest getCartRequest() {
        CartRequest cartRequest = new CartRequest();
        ArrayList<ProductData> items = new ArrayList<>();
        for (int i = 0; i < customUtilsLazy.getValue().getSavedProductsData().size(); i++) {
            items.add(new ProductData(customUtilsLazy.getValue().getSavedProductsData().get(i).getId()
                    , customUtilsLazy.getValue().getSavedProductsData().get(i).getQuantity()));

        }
        cartRequest.setItems(items);
        return cartRequest;
    }

    private void addItsDataToSharedPreferences() {
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
        notNowConstraintLayout.setOnClickListener(v -> {
            dialog.cancel();
        });
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
                    }

                } else {
                    showErrors(startTripResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar("Please make rate before send review !!");
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
