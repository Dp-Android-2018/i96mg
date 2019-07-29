package m.dp.i96mg.view.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityWishListBinding;
import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.GridSpacingItemDecoration;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ProductsRecyclerViewAdapter;
import m.dp.i96mg.view.ui.callback.OnItemClickListener;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;
import m.dp.i96mg.viewmodel.WishListViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class WishListActivity extends BaseActivity {
    private Lazy<WishListViewModel> wishListViewModelLazy = inject(WishListViewModel.class);
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    ActivityWishListBinding binding;
    private int pageId = ConfigurationFile.Constants.DEFAULT_PAGE_ID;
    private String next_page = ConfigurationFile.Constants.DEFAULT_STRING_VALUE;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private int position = 0;
    ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private ArrayList<ProductModel> loadedData;
    private GridLayoutManager gridLayoutManager;
    private OnItemClickListener onItemClickListener;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wish_list);
        context = this;
        loadedData = new ArrayList<>();
        setUpToolbar();
        initializeOnItemClickListener();
        initializeViewModel();
        initializeRecyclerViewWithData();
        initializeSwipeRefreshLayout();
    }

    private void initializeOnItemClickListener() {
        onItemClickListener = new OnItemClickListener() {

            @Override
            public void addItemToWishList(int id, ItemProductLayoutBinding binding) {

            }

            @Override
            public void removeItemFromWishList(int id, ItemProductLayoutBinding binding) {
                if (isLoggedIn()) {
                    removeItemFromWishListDp(id, binding);
                } else {
                    SharedUtils.getInstance().showLoginDialog(context, ConfigurationFile.Constants.WISHLIST_TYPE);
                }
            }

            @Override
            public void addItemToCart(ProductModel productModel, ItemProductLayoutBinding binding) {
                if (productModel.isInCart()) {
                    showSnackbar(getResources().getString(R.string.item_added_before));
                } else {
                    addToCart(productModel,binding);
                }
            }
        };
    }

    private void addToCart(ProductModel productModel, ItemProductLayoutBinding binding) {
        if (isLoggedIn()) {
            sendItemToDb(productModel,binding);
        } else {
            addItsDataToSharedPreferences(productModel);
            showSnackbar(getResources().getString(R.string.product_added_successfully));
            binding.ivAddToCart.setImageDrawable(getResources().getDrawable(R.drawable.in_cart_icon));
            for (int i = 0; i < loadedData.size(); i++) {
                if (loadedData.get(i).getId() == productModel.getId()) {
                    productModel.setInCart(true);
                    loadedData.set(i, productModel);
                    break;
                }
            }
            productsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void sendItemToDb(ProductModel productModel, ItemProductLayoutBinding binding) {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            productDetailsViewModelLazy.getValue().addItemsToCart(getCartRequest(productModel)).observe(this, (Response<MessageResponse> startTripResponseResponse) -> {
                SharedUtils.getInstance().cancelDialog();
                if (startTripResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > startTripResponseResponse.code()) {
                    if (startTripResponseResponse.body() != null) {
                        showSnackbar(startTripResponseResponse.body().getMessage());
                        binding.ivAddToCart.setImageDrawable(getResources().getDrawable(R.drawable.in_cart_icon));
                        for (int i = 0; i < loadedData.size(); i++) {
                            if (loadedData.get(i).getId() == productModel.getId()) {
                                productModel.setInCart(true);
                                loadedData.set(i, productModel);
                                break;
                            }
                        }
                        productsRecyclerViewAdapter.notifyDataSetChanged();
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
        productModel.setOrderedQuantity(1);
        CartRequest cartRequest = new CartRequest();
        ArrayList<ProductData> items = new ArrayList<>();
        items.add(new ProductData(productModel.getId()
                , productModel.getOrderedQuantity()));
        cartRequest.setItems(items);
        return cartRequest;
    }

    private void addItsDataToSharedPreferences(ProductModel productModel) {
        productModel.setOrderedQuantity(1);
        List<ProductModel> productModelList = new ArrayList<>();
        if (customUtilsLazy.getValue().getSavedProductsData() != null) {
            productModelList.addAll(customUtilsLazy.getValue().getSavedProductsData());
        }
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        for (int i = 0; i < productModelList.size(); i++) {
            if (productModelList.get(i).getId() == productModel.getId()) {
                productModelList.remove(i);
                break;
            }
        }
        productModelList.add(productModel);
        customUtilsLazy.getValue().saveProductToPrefs(productModelList);
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
                    binding.ivFavorite.setImageDrawable(binding.getRoot().getResources().getDrawable(R.drawable.heart_empty));
                    if (startTripResponseResponse.body() != null) {
                        showSnackbar(startTripResponseResponse.body().getMessage());
                        for (int i = 0; i < loadedData.size(); i++) {
                            if (loadedData.get(i).getId() == id) {
                                loadedData.remove(i);
                                break;
                            }
                        }
                        productsRecyclerViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    showErrors(startTripResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void initializeViewModel() {
        if (ValidationUtils.isConnectingToInternet(Objects.requireNonNull(this))) {
            SharedUtils.getInstance().showProgressDialog(this);
            makeRequest();
            binding.swipeRefreshLayout.setRefreshing(false);
            observeViewmodel();
        } else {
            binding.swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(binding.getRoot(), R.string.there_is_no_internet_connection, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void makeRequest() {
        wishListViewModelLazy.getValue().getWishListItems();
    }

    private void initializeSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            loadedData.clear();
            initializeViewModel();
        });
        // Configure the refreshing colors
        binding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.darker_gray,
                android.R.color.holo_green_light,
                android.R.color.background_dark,
                android.R.color.holo_purple);

    }

    private void observeViewmodel() {
        wishListViewModelLazy.getValue().getData().observe(this, productsResponseResponse -> {
            SharedUtils.getInstance().cancelDialog();
            if (productsResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                    && ConfigurationFile.Constants.SUCCESS_CODE_TO > productsResponseResponse.code()) {
                if (productsResponseResponse.body() != null) {
                    if (!productsResponseResponse.body().getData().isEmpty()) {
                        addDataToLoadedData(productsResponseResponse.body());
                    }
                }
            } else {
                showErrors(productsResponseResponse.errorBody());
            }

        });
    }

    private void addDataToLoadedData(ProductsResponse body) {
        for (int i = 0; i < body.getData().size(); i++) {
            loadedData.add(body.getData().get(i));
        }
        loading = true;
        /*if (body.getPageLinks().getNextPageLink() != null) {
            next_page = body.getPageLinks().getNextPageLink();
        } else {
            next_page = null;
        }
        if (loadedData.isEmpty() && (next_page != null)) {
            makeRequest();
            observeViewmodel();
        }*/
        productsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initializeRecyclerViewWithData() {
        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(loadedData, ConfigurationFile.Constants.WISHLIST_TYPE, onItemClickListener);
        int spanCount = 2;
        int spacing = 32;
        binding.rvProducts.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, false));
        gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rvProducts.setLayoutManager(gridLayoutManager);
        productsRecyclerViewAdapter.setPageImages(loadedData);
        binding.rvProducts.setAdapter(productsRecyclerViewAdapter);
//        makeOnScrollOnRecyclerView();
    }

    private void makeOnScrollOnRecyclerView() {
        binding.rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > ConfigurationFile.Constants.DEFAULT_INTEGER_VALUE) //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
                    if (loading && (next_page != null)) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loadMoreData();
                        }
                    }
                }
            }
        });
    }

    private void loadMoreData() {
        loading = false;
        position = totalItemCount;
        pageId++;
//        SharedUtils.getInstance().showProgressDialog(this);
        makeRequest();
        observeViewmodel();
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

    private void setUpToolbar() {
        binding.ivBack.setOnClickListener(view -> {
            WishListActivity.this.onBackPressed();
            Intent intent = new Intent(WishListActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
