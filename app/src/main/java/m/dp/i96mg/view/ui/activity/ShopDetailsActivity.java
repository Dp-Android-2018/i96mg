package m.dp.i96mg.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityShopDetailsBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.view.ui.adapter.ShopRecyclerViewAdapter;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ShopDetailsActivity extends AppCompatActivity {

    ActivityShopDetailsBinding binding;
    private List<ProductModel> productModelList;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private float totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_details);
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        productModelList = new ArrayList<>();
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        getAndSetTotalPrice();
        initializeRecyclerView();
    }

    private void getAndSetTotalPrice() {
        if (productModelList != null) {
            for (int i = 0; i < productModelList.size(); i++) {
                if (productModelList.get(i).isHasDiscount()) {
                    totalPrice += productModelList.get(i).getDiscountedPrice();
                } else {
                    totalPrice += productModelList.get(i).getOriginalPrice();
                }
            }
            binding.tvTotalPrice.setText(String.valueOf(totalPrice));
        }
    }

    private void initializeRecyclerView() {
        ShopRecyclerViewAdapter shopRecyclerViewAdapter = new ShopRecyclerViewAdapter(productModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvProducts.setLayoutManager(linearLayoutManager);
        binding.rvProducts.setAdapter(shopRecyclerViewAdapter);
        binding.rvProducts.setItemAnimator(new DefaultItemAnimator());
    }

    public void goToPayActivity(View view) {
        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
        finish();
    }
}
