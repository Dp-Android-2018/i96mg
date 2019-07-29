package m.dp.i96mg.view.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
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
import m.dp.i96mg.databinding.ActivityMainBinding;
import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.CategoriesResponeModel;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.SocialNetworksModel;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.WishListRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.GridSpacingItemDecoration;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.CategoriesRecyclerViewAdapter;
import m.dp.i96mg.view.ui.adapter.ProductsRecyclerViewAdapter;
import m.dp.i96mg.view.ui.callback.OnItemClickListener;
import m.dp.i96mg.viewmodel.MainActivityViewModel;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class MainActivity extends BaseActivity {

    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private Lazy<MainActivityViewModel> mainActivityViewModelLazy = inject(MainActivityViewModel.class);
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    ActivityMainBinding binding;
    ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private ArrayList<ProductModel> loadedData;
    private int pageId = ConfigurationFile.Constants.DEFAULT_PAGE_ID;
    private String categoryId = ConfigurationFile.Constants.DEFAULT_CATEGORY_ID;
    private int saleId;
    private int rvCategoryId;
    private String next_page = ConfigurationFile.Constants.DEFAULT_STRING_VALUE;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private GridLayoutManager gridLayoutManager;
    public static DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    OnItemClickListener onItemClickListener;
    private Context context;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        context = this;
        loadedData = new ArrayList<>();
        rvCategoryId = getIntent().getIntExtra(ConfigurationFile.Constants.Category_ID, 0);
        saleId = getIntent().getIntExtra(ConfigurationFile.Constants.Category_ID, 0);
        if (rvCategoryId != 0) {
            categoryId = String.valueOf(rvCategoryId);
        }
        setupToolbar();
        initializeWishListOnItemClicked();
        initializeDrawerandNavigationView();
        checkIfLoginOrNot();
        makeActionOnMenuItems();
        getAndSetSettings();
        initializeViewModel();
        initializeRecyclerViewWithData();
        initializeSwipeRefreshLayout();
        getBrowserResponse();
    }

    private void initializeWishListOnItemClicked() {
        onItemClickListener = new OnItemClickListener() {

            @Override
            public void addItemToWishList(int id, ItemProductLayoutBinding binding) {
                if (isLoggedIn()) {
                    addItemToWishListDp(id, binding);
                } else {
                    SharedUtils.getInstance().showLoginDialog(context, ConfigurationFile.Constants.MAIN_ACTIVITY);
                }
            }

            @Override
            public void removeItemFromWishList(int id, ItemProductLayoutBinding binding) {
                if (isLoggedIn()) {
                    removeItemFromWishListDp(id, binding);
                } else {
                    SharedUtils.getInstance().showLoginDialog(context, ConfigurationFile.Constants.MAIN_ACTIVITY);
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
        for (int i = 0; i < loadedData.size(); i++) {
            if (loadedData.get(i).getId() == id) {
                ProductModel productModel = loadedData.get(i);
                productModel.setInWishlist(false);
                loadedData.set(i, productModel);
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
                        showSnackbar(startTripResponseResponse.body().getMessage());
                        addAndShowMessage(id, binding);
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
        for (int i = 0; i < loadedData.size(); i++) {
            if (loadedData.get(i).getId() == id) {
                ProductModel productModel = loadedData.get(i);
                productModel.setInWishlist(true);
                loadedData.set(i, productModel);
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

    private void checkIfLoginOrNot() {
        if (!isLoggedIn()) {
            binding.navigationView.navigationViewHeaderLayout.tvName.setText(getResources().getString(R.string.login));
            binding.navigationView.navigationViewHeaderLayout.ivUser.setImageDrawable(getResources().getDrawable(R.drawable.app_icon));
            binding.navigationView.tvNavItemLogout.setVisibility(View.GONE);
            binding.navigationView.tvNavItemWishList.setVisibility(View.GONE);
            binding.navigationView.tvNavItemOrderList.setVisibility(View.GONE);
            binding.navigationView.tvNavItemPendingOrders.setVisibility(View.GONE);
        } else {
            binding.navigationView.tvNavItemLogout.setText(getResources().getString(R.string.logout));
            ConfigurationFile.Constants.AUTHORIZATION = customUtilsLazy.getValue().getSavedMemberData().getToken();
            String name = customUtilsLazy.getValue().getSavedMemberData().getFirstName()
                    + ConfigurationFile.Constants.SPACE + customUtilsLazy.getValue().getSavedMemberData().getLastName();
            binding.navigationView.navigationViewHeaderLayout.tvName.setText(name);
            ImageView ivGalleryPhoto = binding.navigationView.navigationViewHeaderLayout.ivUser;
            Picasso.get().load(customUtilsLazy.getValue().getSavedMemberData().getProfilePictureUrl()).into(ivGalleryPhoto);
        }
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
        for (int i = 0; i < productModelList.size(); i++) {
            if (productModelList.get(i).getId() == productModel.getId()) {
                productModelList.remove(i);
                break;
            }
        }
        productModelList.add(productModel);
        customUtilsLazy.getValue().saveProductToPrefs(productModelList);
    }

    private void setupToolbar() {
        binding.ivShopCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopDetailsActivity.class);
            startActivity(intent);
        });
        binding.ivMenu.setOnClickListener(v -> MainActivity.drawer.openDrawer(GravityCompat.START));
    }

    private void initializeDrawerandNavigationView() {
        drawer = binding.drawerLayout;
        ConstraintLayout content = binding.contentLayout;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.Open, R.string.Close) {
            private float scaleFactor = 6f;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                if (customUtilsLazy.getValue().getSavedLanguageType().equals(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC)) {
                    content.setTranslationX(-slideX);
                } else {
                    content.setTranslationX(slideX);
                }
//                content.setScaleX(1 - (slideOffset / scaleFactor));
            }
        };
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setDrawerElevation(0f);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void makeActionOnMenuItems() {
        binding.navigationView.navigationViewHeaderLayout.getRoot().setOnClickListener(view -> openEditProfile());
        binding.navigationView.tvNavItemSale.setOnClickListener(view -> openSaleActivity());
        binding.navigationView.tvNavItemWishList.setOnClickListener(view -> openThisActivity(WishListActivity.class));
        binding.navigationView.tvNavItemPendingOrders.setOnClickListener(view -> openActivityWithMemberType(PendingOrdersActivity.class, ConfigurationFile.Constants.PENDING_ORDERS_ACTIVITY));
        binding.navigationView.tvNavItemOrderList.setOnClickListener(view -> openActivityWithMemberType(PendingOrdersActivity.class, ConfigurationFile.Constants.ORDERS_ACTIVITY));
        binding.navigationView.tvNavItemLanguage.setOnClickListener(view -> changeLanguage());
        binding.navigationView.tvNavItemRateUs.setOnClickListener(view -> openPlayStoreToRateApp());
        binding.navigationView.tvNavItemLogout.setOnClickListener(view -> makeLogAction());
    }

    private void openActivityWithMemberType(Class activityClass, String memberType) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        intent.putExtra(ConfigurationFile.Constants.ACTIVITY_NAME, memberType);
        startActivity(intent);
    }

    private void openEditProfile() {
        if (isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        } else {
            login();
        }
    }

    private void openSaleActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.putExtra(ConfigurationFile.Constants.Category_ID, ConfigurationFile.Constants.SALE_ID);
        startActivity(intent);
        finish();
    }

    private void openThisActivity(Class activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
    }

    private void changeLanguage() {
        if (customUtils.getValue().getSavedLanguageType().equals(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ENGLISH)) {
            customUtils.getValue().saveLanguageTypeToPrefs(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC);
            openAppAgain();
        } else {
            customUtils.getValue().saveLanguageTypeToPrefs(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ENGLISH);
            openAppAgain();
        }
    }

    private void openAppAgain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void makeLogAction() {
        if (isLoggedIn()) {
            logout();
        }
    }

    private boolean isLoggedIn() {
        if (customUtilsLazy.getValue().getSavedMemberData() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void login() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(ConfigurationFile.Constants.ACTIVITY_NAME, ConfigurationFile.Constants.MAIN_ACTIVITY);
        startActivity(intent);
    }

    private void logout() {
        String languageType = customUtils.getValue().getSavedLanguageType();
        customUtils.getValue().clearSharedPref();
        customUtils.getValue().saveLanguageTypeToPrefs(languageType);
        ConfigurationFile.Constants.AUTHORIZATION = null;
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
        if (saleId == ConfigurationFile.Constants.SALE_ID) {
            mainActivityViewModelLazy.getValue().getSaleProducts();
        } else {
            mainActivityViewModelLazy.getValue().getProducts(categoryId, pageId);

        }
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

    private void getBrowserResponse() {
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            String status = uri.getQueryParameter(ConfigurationFile.Constants.KEY_SUCCESS);
            if (status.equals(ConfigurationFile.Constants.KEY_TRUE)) {
//                String languageType = customUtils.getValue().getSavedLanguageType();
//                customUtils.getValue().clearSharedPref();
//                customUtils.getValue().saveLanguageTypeToPrefs(languageType);
            }
        }
    }

    private void observeViewmodel() {
        mainActivityViewModelLazy.getValue().getData().observe(this, productsResponseResponse -> {
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
        if (body.getPageLinks().getNextPageLink() != null) {
            next_page = body.getPageLinks().getNextPageLink();
        } else {
            next_page = null;
        }
        if (loadedData.isEmpty() && (next_page != null)) {
            makeRequest();
            observeViewmodel();
        }
        productsRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initializeRecyclerViewWithData() {
        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(loadedData, ConfigurationFile.Constants.DEFAULT_TYPE, onItemClickListener);
        int spanCount = 2;
        int spacing = 32;
        binding.rvProducts.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, false));
        gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rvProducts.setLayoutManager(gridLayoutManager);
        productsRecyclerViewAdapter.setPageImages(loadedData);
        binding.rvProducts.setAdapter(productsRecyclerViewAdapter);
        makeOnScrollOnRecyclerView();
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
        pageId++;
        SharedUtils.getInstance().showProgressDialog(this);
        makeRequest();
        observeViewmodel();
    }

    private void getAndSetSettings() {
        if (ValidationUtils.isConnectingToInternet(Objects.requireNonNull(this))) {
//            SharedUtils.getInstance().showProgressDialog(this);
            mainActivityViewModelLazy.getValue().getSettings().observe(this, categoriesResponseResponse -> {
                if (categoriesResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > categoriesResponseResponse.code()) {

                    if (categoriesResponseResponse.body() != null) {
                        initializeCategoriesRecyclerView(categoriesResponseResponse.body().getCategories());
                        initializeLinks(categoriesResponseResponse.body().getSettings().getSocialNetworks());
                    }
                } else {
                    showErrors(categoriesResponseResponse.errorBody());
                }
            });
        } else {
            Snackbar.make(binding.getRoot(), R.string.there_is_no_internet_connection, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initializeCategoriesRecyclerView(ArrayList<CategoriesResponeModel> categoriesResponeModels) {
        CategoriesRecyclerViewAdapter categoriesRecyclerViewAdapter = new CategoriesRecyclerViewAdapter(categoriesResponeModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.navigationView.rvCategory.setLayoutManager(linearLayoutManager);
        binding.navigationView.rvCategory.setAdapter(categoriesRecyclerViewAdapter);
    }

    private void initializeLinks(SocialNetworksModel socialNetworksModel) {
        binding.navigationView.navigationViewFooterLayout.facebookImage.setOnClickListener(v -> openLinkIntent(ConfigurationFile.Constants.FACEBOOK_NAME
                , socialNetworksModel.getFacebookUrl(), ConfigurationFile.Constants.FACEBOOK_PACKAGE_NAME));
        binding.navigationView.navigationViewFooterLayout.instagramImage.setOnClickListener(v -> openLinkIntent(ConfigurationFile.Constants.INSTAGRAM_NAME
                , socialNetworksModel.getInstagramUrl(), ConfigurationFile.Constants.INSTAGRAM_PACKAGE_NAME));
        binding.navigationView.navigationViewFooterLayout.twitterImage.setOnClickListener(v -> openLinkIntent(ConfigurationFile.Constants.TWITTER_NAME
                , socialNetworksModel.getTwitterUrl(), ConfigurationFile.Constants.TWITTER_PACKAGE_NAME));

        binding.navigationView.navigationViewFooterLayout.youtubeImage.setOnClickListener(v -> watchYoutubeVideo(socialNetworksModel.getYoutubeUrl()));
    }

    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void openLinkIntent(String memberType, String shareBodyText, String packageName) {

        if (memberType.equals(ConfigurationFile.Constants.BROWSER_NAME)) {
            Intent shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareBodyText));
            openThatIntent(shareIntent, shareBodyText, memberType);
        } else if (memberType.equals(ConfigurationFile.Constants.FACEBOOK_NAME)) {
            Intent shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getFacebookPageURL(this, shareBodyText)));
            shareIntent.setPackage(packageName);
            openThatIntent(shareIntent, shareBodyText, memberType);
        }/* else if (memberType.equals(ConfigurationFile.Constants.WATSAPP_NAME)) {
            try {
                Uri uri = Uri.parse("whatsapp://send?phone=+" + shareBodyText);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), getResources().getString(R.string.whats_app_not_installed), Snackbar.LENGTH_SHORT).show();
            }

        } */ else {
            Intent shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareBodyText));
            shareIntent.setPackage(packageName);
            openThatIntent(shareIntent, shareBodyText, memberType);
        }

    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context, String facebook_url) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo(ConfigurationFile.Constants.FACEBOOK_PACKAGE_NAME, 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return ConfigurationFile.Constants.FACEBOOK_PACKAGE_NAME_URL + facebook_url;
            } else {
                return facebook_url;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return facebook_url; //normal web url
        }
    }

    private void openPlayStoreToRateApp() {
        Uri uri = Uri.parse(ConfigurationFile.Constants.MARKET_URL + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(ConfigurationFile.Constants.PLAYSTORE_URL + this.getPackageName())));
        }
    }

    private void openThatIntent(Intent shareIntent, String shareBodyText, String memberType) {
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(binding.getRoot(), memberType + " " + getString(R.string.have_not_initialized), Snackbar.LENGTH_SHORT).show();
        }
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else if (doubleBackToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(binding.getRoot(), R.string.please_click_back_again_to_exit, Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

}
