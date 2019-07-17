package m.dp.i96mg.view.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
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
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.OrdersRecyclerViewAdapter;
import m.dp.i96mg.viewmodel.OrdersViewModel;
import okhttp3.ResponseBody;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PendingOrdersActivity extends AppCompatActivity {

    ActivityPendingOrdersBinding binding;
    private Lazy<OrdersViewModel> ordersViewModelLazy = inject(OrdersViewModel.class);
    private OrdersRecyclerViewAdapter ordersRecyclerViewAdapter;
    private String memberType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_orders);
        memberType = getIntent().getStringExtra(ConfigurationFile.Constants.ACTIVITY_NAME);
        setupToolBar();
        if (memberType.equals(ConfigurationFile.Constants.PENDING_ORDERS_ACTIVITY)) {
            getPendingOrders();
        } else {
            getOrders();
        }
    }

    private void getPendingOrders() {
        if (ValidationUtils.isConnectingToInternet(this)) {
            SharedUtils.getInstance().showProgressDialog(this);
            ordersViewModelLazy.getValue().getPendingOrders().observe(this, allOrdersResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (allOrdersResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > allOrdersResponseResponse.code()) {
                    if (allOrdersResponseResponse.body() != null) {
                        initializeRecyclerViewWithData(allOrdersResponseResponse.body().getData(), memberType);
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
            ordersViewModelLazy.getValue().getOrders().observe(this, allOrdersResponseResponse -> {
                SharedUtils.getInstance().cancelDialog();
                if (allOrdersResponseResponse.code() >= ConfigurationFile.Constants.SUCCESS_CODE_FROM
                        && ConfigurationFile.Constants.SUCCESS_CODE_TO > allOrdersResponseResponse.code()) {
                    if (allOrdersResponseResponse.body() != null) {
                        initializeRecyclerViewWithData(allOrdersResponseResponse.body().getData(), memberType);
                    }
                } else {
                    showErrors(allOrdersResponseResponse.errorBody());
                }
            });
        } else {
            showSnackbar(getResources().getString(R.string.there_is_no_internet_connection));
        }
    }

    private void initializeRecyclerViewWithData(ArrayList<OrderResponseModel> data, String memberType) {
        ordersRecyclerViewAdapter = new OrdersRecyclerViewAdapter(data, memberType);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvOrders.setAdapter(ordersRecyclerViewAdapter);
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
