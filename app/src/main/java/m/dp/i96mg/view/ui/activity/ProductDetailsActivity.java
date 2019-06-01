package m.dp.i96mg.view.ui.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ActivityProductDetailsBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;

import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PRODUCT_DETAIL;
import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ProductDetailsActivity extends BaseActivity {

    ActivityProductDetailsBinding binding;
    private ProductModel productModel;
    private int quantity = ConfigurationFile.Constants.DEFAULT_QUANTITY;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private float price;
    private List<ProductModel> productModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        productModel = new Gson().fromJson(getIntent().getStringExtra(PRODUCT_DETAIL), ProductModel.class);
        productModelList = new ArrayList<>();
        if (customUtilsLazy.getValue().getSavedProductsData() != null) {
            productModelList.addAll(customUtilsLazy.getValue().getSavedProductsData());
        }
        makeActionOnToolbat();
        makeActionOnClickOnQuantityIcons();
        initializeUiWithData();
    }

    private void makeActionOnClickOnQuantityIcons() {
        binding.tvPlus.setOnClickListener(v -> {
            if (quantity < productModel.getQuantity()) {
                quantity++;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
//                price = productModel.getOriginalPrice() * quantity;
//                binding.tvPrice.setText(String.valueOf(price));
            }
        });

        binding.tvMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
//                price = productModel.getOriginalPrice() * quantity;
//                binding.tvPrice.setText(String.valueOf(price));
            }
        });
    }

    private void makeActionOnToolbat() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void initializeUiWithData() {
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
            if (productModel.isHasDiscount()) {
                binding.tvPrice.setPaintFlags(binding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvDiscountedPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
            }
        }
    }


    public void addProductToCart(View view) {
        addItsDataToSharedPreferences();
        onBackPressed();
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
}
