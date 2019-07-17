package m.dp.i96mg.view.ui.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.request.WishListRequest;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.view.ui.activity.ProductDetailsActivity;
import m.dp.i96mg.view.ui.callback.OnItemClickListener;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;

import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PRODUCTS_LIST;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PRODUCT_ID;
import static org.koin.java.standalone.KoinJavaComponent.inject;


public class ProductsViewHolder extends RecyclerView.ViewHolder {
    ItemProductLayoutBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private ArrayList<ProductModel> productModelList;
    private List<ProductModel> savedCartProducts;
    private List<ProductModel> savedFavoriteProducts;
    private ProductModel productModel;
    private String listType;
    private OnItemClickListener onItemClickListener;

    public ProductsViewHolder(ItemProductLayoutBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
        savedCartProducts = new ArrayList<>();
        savedFavoriteProducts = new ArrayList<>();
        savedCartProducts = customUtilsLazy.getValue().getSavedProductsData();
        savedFavoriteProducts = customUtilsLazy.getValue().getFavoriteSavedProductsData();
    }

    public void bindClass(ProductModel productModel, ArrayList<ProductModel> productModelList, String listType, OnItemClickListener onItemClickListener) {
        this.productModelList = productModelList;
        this.productModel = productModel;
        this.onItemClickListener = onItemClickListener;
        this.listType = listType;
        if (listType.equals(ConfigurationFile.Constants.WISHLIST_TYPE)) {
            binding.ivFavorite.setImageDrawable(binding.getRoot().getResources().getDrawable(R.drawable.heart_filled));
        }
        ImageView ivGalleryPhoto = binding.ivProductImage;
        Picasso.get().load(productModel.getImageUrl()).into(ivGalleryPhoto);
        binding.tvName.setText(productModel.getName());
        binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
        if (productModel.isHasDiscount()) {
          /*  binding.tvPriceDiscount.setText(String.valueOf(productModel.getOriginalPrice()));
            binding.tvPriceDiscount.setPaintFlags(binding.tvPriceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvSr.setPaintFlags(binding.tvPriceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvPriceDiscount.setTextColor(Color.GRAY);
            binding.tvSr.setTextColor(Color.GRAY);
            binding.tvPriceDiscount.setTextSize(9);
            binding.tvSr.setTextSize(9);
            binding.tvSr.setVisibility(View.VISIBLE);*/
            binding.tvPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
            int discountRatio = (int) ((productModel.getOriginalPrice() - productModel.getDiscountedPrice()) * 100 / productModel.getOriginalPrice());
            String discountValue = discountRatio + ConfigurationFile.Constants.PERCENT + binding.getRoot().getResources().getString(R.string.off_percent);
            binding.tvDiscountRatio.setVisibility(View.VISIBLE);
            binding.tvDiscountRatio.setText(discountValue);
        }
        if (productModel.isInWishlist()) {
            binding.ivFavorite.setImageDrawable(binding.getRoot().getResources().getDrawable(R.drawable.heart_filled));
        } else {
            binding.ivFavorite.setImageDrawable(binding.getRoot().getResources().getDrawable(R.drawable.heart_empty));
        }
        makeFavorite(productModel);
        makeActionOnClickOnItem();

    }

    private void makeFavorite(ProductModel productModel) {
        binding.ivFavorite.setOnClickListener(view -> {
            if (productModel.isInWishlist()) {
                onItemClickListener.removeItemFromWishList(productModel.getId(), binding);
            } else {
                onItemClickListener.addItemToWishList(productModel.getId(), binding);
            }
        });

        binding.ivAddToCart.setOnClickListener(view -> {
            onItemClickListener.addItemToCart(productModel, binding);
        });
    }

    private void makeActionOnClickOnItem() {
        binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putParcelableArrayListExtra(PRODUCTS_LIST, productModelList);
            intent.putExtra(PRODUCT_ID, productModel.getId());
            v.getContext().startActivity(intent);
        });
    }
}
