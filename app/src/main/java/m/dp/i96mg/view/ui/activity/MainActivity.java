package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityMainBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.GridSpacingItemDecoration;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ProductsRecyclerViewAdapter;
import m.dp.i96mg.viewmodel.MainActivityViewModel;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class MainActivity extends BaseActivity {

    private Lazy<MainActivityViewModel> mainActivityViewModelLazy = inject(MainActivityViewModel.class);
    ActivityMainBinding binding;
    ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private List<ProductModel> loadedData;
    private int pageId = ConfigurationFile.Constants.DEFAULT_PAGE_ID;
    private String categoryId = ConfigurationFile.Constants.DEFAULT_CATEGORY_ID;
    private String next_page = ConfigurationFile.Constants.DEFAULT_STRING_VALUE;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private int position = 0;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        loadedData = new ArrayList<>();
        setupToolbar();
        initializeViewModel();
        initializeRecyclerViewWithData();
        getBrowserResponse();
    }

    private void getBrowserResponse() {
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            String status = uri.getQueryParameter("success");
            if (status.equals("true")) {
                String languageType = customUtils.getValue().getSavedLanguageType();
                customUtils.getValue().clearSharedPref();
                customUtils.getValue().saveLanguageTypeToPrefs(languageType);
            }
        }
    }

    private void setupToolbar() {
        binding.ivShopCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopDetailsActivity.class);
            startActivity(intent);
        });

        binding.ivMoreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, binding.ivMoreMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(item -> {
                    if (customUtils.getValue().getSavedLanguageType().equals(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ENGLISH)) {
                        customUtils.getValue().saveLanguageTypeToPrefs(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ARABIC);
                        openAppAgain();
                    } else {
                        customUtils.getValue().saveLanguageTypeToPrefs(ConfigurationFile.Constants.ACCEPT_LANGUAGE_ENGLISH);
                        openAppAgain();
                    }
                    return true;
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method
    }

    private void openAppAgain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeViewModel() {
        if (ValidationUtils.isConnectingToInternet(Objects.requireNonNull(this))) {
            SharedUtils.getInstance().showProgressDialog(this);
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
                        addDataToLoadedData(productsResponseResponse.body());
                    }
                }
            } else {
                showErrors(productsResponseResponse);
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
            mainActivityViewModelLazy.getValue().getProducts(categoryId, pageId);
            observeViewmodel();
        }
        productsRecyclerViewAdapter.notifyDataSetChanged();
    }


    private void initializeRecyclerViewWithData() {
        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(loadedData);
        int spanCount = 2;
        int spacing = 16;
        binding.rvProducts.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, false));
        gridLayoutManager = new GridLayoutManager(this, 2);
        binding.rvProducts.setLayoutManager(gridLayoutManager);
//        binding.rvProducts.addOnScrollListener(onScrollListener());
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
        position = totalItemCount;
        pageId++;
        SharedUtils.getInstance().showProgressDialog(this);
        mainActivityViewModelLazy.getValue().getProducts(categoryId, pageId);
        observeViewmodel();
    }


    private void showErrors(Response<ProductsResponse> productsResponseResponse) {

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
