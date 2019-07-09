package m.dp.i96mg.view.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityWishListBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.GridSpacingItemDecoration;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ProductsRecyclerViewAdapter;
import m.dp.i96mg.viewmodel.WishListViewModel;
import okhttp3.ResponseBody;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class WishListActivity extends BaseActivity {

    private Lazy<WishListViewModel> wishListViewModelLazy = inject(WishListViewModel.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wish_list);
        setUpToolbar();
        initializeViewModel();
        initializeRecyclerViewWithData();
        initializeSwipeRefreshLayout();
    }

    private void initializeViewModel() {
        if (ValidationUtils.isConnectingToInternet(Objects.requireNonNull(this))) {
//            SharedUtils.getInstance().showProgressDialog(this);
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
        binding.swipeRefreshLayout.setOnRefreshListener(() -> initializeViewModel());
        // Configure the refreshing colors
        binding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.darker_gray,
                android.R.color.holo_green_light,
                android.R.color.background_dark,
                android.R.color.holo_purple);

    }

    private void observeViewmodel() {
        wishListViewModelLazy.getValue().getData().observe(this, productsResponseResponse -> {
//            SharedUtils.getInstance().cancelDialog();
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
        productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(loadedData, ConfigurationFile.Constants.WISHLIST_TYPE);
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
        binding.ivBack.setOnClickListener(view -> onBackPressed());
    }
}
