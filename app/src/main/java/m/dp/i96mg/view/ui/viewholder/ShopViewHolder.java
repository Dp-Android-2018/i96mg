package m.dp.i96mg.view.ui.viewholder;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.utility.utils.CustomUtils;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class ShopViewHolder extends RecyclerView.ViewHolder {
    ItemShopCartBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private ProductModel productModel;
    private List<ProductModel> productModelList;
    private int quantity;
    private float price;

    public ShopViewHolder(ItemShopCartBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
    }

    public void bindClass(ProductModel productModel) {
        this.productModel = productModel;
        productModelList = new ArrayList<>();
        ImageView ivGalleryPhoto = binding.ivProductImage;
        Picasso.get().load(productModel.getImageUrl()).into(ivGalleryPhoto);
        binding.tvName.setText(productModel.getName());
        binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
        binding.tvQuantityNum.setText(String.valueOf(productModel.getOrderedQuantity()));
        float totalprice = productModel.getOrderedQuantity() * productModel.getOriginalPrice();
        binding.tvTotalPrice.setText(String.valueOf(totalprice));
        quantity = productModel.getOrderedQuantity();
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        makeActionOnClickOnQuantityIcons();
    }

    private void makeActionOnClickOnQuantityIcons() {
        binding.tvPlus.setOnClickListener(v -> {
            if (quantity < productModel.getQuantity()) {
                quantity++;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
                productModel.setOrderedQuantity(quantity);
                price = productModel.getOriginalPrice() * quantity;
                binding.tvTotalPrice.setText(String.valueOf(price));
                addThisProductToSharedPreferences();
            }
        });

        binding.tvMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
                productModel.setOrderedQuantity(quantity);
                price = productModel.getOriginalPrice() * quantity;
                binding.tvTotalPrice.setText(String.valueOf(price));
                addThisProductToSharedPreferences();
            }
        });
    }

    private void addThisProductToSharedPreferences() {
        for (int i = 0; i < productModelList.size(); i++) {
            if (productModelList.get(i).getId() == productModel.getId()) {
                productModelList.remove(i);
            }
        }
        productModelList.add(productModel);
        customUtilsLazy.getValue().saveProductToPrefs(productModelList);
    }
}
