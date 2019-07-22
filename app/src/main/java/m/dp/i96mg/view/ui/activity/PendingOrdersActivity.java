package m.dp.i96mg.view.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityPendingOrdersBinding;
import m.dp.i96mg.service.model.global.OrderResponseModel;
import m.dp.i96mg.service.model.response.AllOrdersResponse;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.GridSpacingItemDecoration;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.OrdersRecyclerViewAdapter;
import m.dp.i96mg.view.ui.adapter.ProductsRecyclerViewAdapter;
import m.dp.i96mg.viewmodel.OrdersViewModel;
import okhttp3.ResponseBody;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PendingOrdersActivity extends AppCompatActivity {

    ActivityPendingOrdersBinding binding;
    private Lazy<OrdersViewModel> ordersViewModelLazy = inject(OrdersViewModel.class);
    private OrdersRecyclerViewAdapter ordersRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String memberType;
    private int pageId = ConfigurationFile.Constants.DEFAULT_PAGE_ID;
    private String next_page = ConfigurationFile.Constants.DEFAULT_STRING_VALUE;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private int position = 0;
    ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
    private ArrayList<OrderResponseModel> loadedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_orders);
        loadedData = new ArrayList<>();
        memberType = getIntent().getStringExtra(ConfigurationFile.Constants.ACTIVITY_NAME);
        setupToolBar();
        if (memberType.equals(ConfigurationFile.Constants.PENDING_ORDERS_ACTIVITY)) {
            binding.tvActivityName.setText(getResources().getString(R.string.pending_payment_orders));
            getPendingOrders();
            initializeRecyclerViewWithData();
        } else {
            binding.tvActivityName.setText(getResources().getString(R.string.orders_list));
            getOrders();
            initializeRecyclerViewWithData();
        }
    }

    private void getPendingOrders() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            ordersViewModelLazy.getValue().getPendingOrders(pageId).observe(this, allOrdersResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (allOrdersResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > allOrdersResponseResponse.code()) {
                    if (allOrdersResponseResponse.body() != null) {
                        if (!allOrdersResponseResponse.body().getData().isEmpty()) {
                            addDataToLoadedData(allOrdersResponseResponse.body());
                        }
                    }
                } else {
                    showErrors(allOrdersResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void getOrders() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            ordersViewModelLazy.getValue().getOrders(pageId).observe(this, allOrdersResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (allOrdersResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > allOrdersResponseResponse.code()) {
                    if (allOrdersResponseResponse.body() != null) {
                        if (!allOrdersResponseResponse.body().getData().isEmpty()) {
                            addDataToLoadedData(allOrdersResponseResponse.body());
                        }
                    }
                } else {
                    showErrors(allOrdersResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void addDataToLoadedData(AllOrdersResponse body) {
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
            if (memberType.equals(ConfigurationFile.Constants.PENDING_ORDERS_ACTIVITY)) {
                getPendingOrders();
            } else {
                getOrders();
            }
//            makeRequest();
//            observeViewmodel();
        }
        ordersRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initializeRecyclerViewWithData() {
        ordersRecyclerViewAdapter = new OrdersRecyclerViewAdapter(loadedData, memberType);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvOrders.setLayoutManager(linearLayoutManager);
        ordersRecyclerViewAdapter.setPageImages(loadedData);
        binding.rvOrders.setAdapter(ordersRecyclerViewAdapter);
        makeOnScrollOnRecyclerView();
    }

    private void makeOnScrollOnRecyclerView() {
        binding.rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > ConfigurationFile.Constants.DEFAULT_INTEGER_VALUE) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
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
        if (memberType.equals(ConfigurationFile.Constants.PENDING_ORDERS_ACTIVITY)) {
            getPendingOrders();
        } else {
            getOrders();
        }
//        makeRequest();
//        observeViewmodel();
    }

    private void setupToolBar() {
        binding.ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
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


}
