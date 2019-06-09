package m.dp.i96mg.view.ui.viewholder;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class ShopViewHolder extends RecyclerView.ViewHolder {
    ItemShopCartBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private ProductModel productModel;
    private List<ProductModel> productModelList;
    private int quantity;
    private float price;
    private float totalprice;
    private OnQuantityChanged onQuantityChanged;

    public ShopViewHolder(ItemShopCartBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
    }

    public void bindClass(ProductModel productModel, OnQuantityChanged onQuantityChanged) {
        this.productModel = productModel;
        this.onQuantityChanged = onQuantityChanged;
        productModelList = new ArrayList<>();
        ImageView ivGalleryPhoto = binding.ivProductImage;
        Picasso.get().load(productModel.getImageUrl()).into(ivGalleryPhoto);
        binding.tvName.setText(productModel.getName());
        binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
        if (productModel.isHasDiscount()) {
            binding.tvDiscountPrice.setText(String.valueOf(productModel.getOriginalPrice()));
            binding.tvDiscountPrice.setPaintFlags(binding.tvDiscountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvSr.setPaintFlags(binding.tvDiscountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvDiscountPrice.setTextColor(Color.GRAY);
            binding.tvSr.setTextColor(Color.GRAY);
            binding.tvDiscountPrice.setTextSize(9);
            binding.tvSr.setTextSize(9);
            binding.tvSr.setVisibility(View.VISIBLE);
            binding.tvPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
        }
        binding.tvQuantityNum.setText(String.valueOf(productModel.getOrderedQuantity()));
        if (productModel.isHasDiscount()) {
            totalprice = productModel.getOrderedQuantity() * productModel.getDiscountedPrice();
        } else {
            totalprice = productModel.getOrderedQuantity() * productModel.getOriginalPrice();
        }
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
                if (productModel.isHasDiscount()) {
                    price = productModel.getDiscountedPrice() * quantity;
                } else {
                    price = productModel.getOriginalPrice() * quantity;
                }
                binding.tvTotalPrice.setText(String.valueOf(price));
                addThisProductToSharedPreferences();
            }
        });

        binding.tvMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
                productModel.setOrderedQuantity(quantity);
                if (productModel.isHasDiscount()) {
                    price = productModel.getDiscountedPrice() * quantity;
                } else {
                    price = productModel.getOriginalPrice() * quantity;
                }
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
        onQuantityChanged.onQuantityChange(true);
    }
}
