package m.dp.i96mg.view.ui.viewholder;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ShopRecyclerViewAdapter;
import m.dp.i96mg.view.ui.callback.OnOperationClicked;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class ShopViewHolder extends RecyclerView.ViewHolder {
    ItemShopCartBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private ShopRecyclerViewAdapter shopRecyclerViewAdapter;
    private ProductModel productModel;
    private List<ProductModel> productModelList;
    private int quantity;
    private float price;
    private float totalprice;
    private OnQuantityChanged onQuantityChanged;
    private int index;
    private OnOperationClicked onOperationClicked;

    public ShopViewHolder(ItemShopCartBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
    }

    public void bindClass(ProductModel productModel, OnQuantityChanged onQuantityChanged, OnOperationClicked onOperationClicked) {
        this.productModel = productModel;
        this.onQuantityChanged = onQuantityChanged;
        this.onOperationClicked = onOperationClicked;
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
//        getAndDeleteItem();
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
                if (isLoggedIn()) {
                    onOperationClicked.plusIconClicked(getAdapterPosition(),productModel);
                } else {
                    addThisProductToSharedPreferences();
                }
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
                if (isLoggedIn()) {
                    onOperationClicked.minusIconClicked(getAdapterPosition(),productModel);
                } else {
                    addThisProductToSharedPreferences();
                }
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

   /* private void getAndDeleteItem() {
        binding.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOperationClicked.itemClicked(getAdapterPosition());
//                removeThisProductFromSharedPreferences(productModel, getAdapterPosition());
            }
        });

    }*/

    private boolean isLoggedIn() {
        if (customUtilsLazy.getValue().getSavedMemberData() != null) {
            return true;
        } else {
            return false;
        }
    }

}
