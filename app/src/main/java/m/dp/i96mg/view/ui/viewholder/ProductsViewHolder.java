package m.dp.i96mg.view.ui.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.view.ui.activity.ProductDetailsActivity;

import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PRODUCT_DETAIL;


public class ProductsViewHolder extends RecyclerView.ViewHolder {
    ItemProductLayoutBinding binding;
    private ProductModel productModel;

    public ProductsViewHolder(ItemProductLayoutBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
    }

    public void bindClass(ProductModel productModel) {
        this.productModel = productModel;
        ImageView ivGalleryPhoto = binding.ivProductImage;
        Picasso.get().load(productModel.getImageUrl()).into(ivGalleryPhoto);
        binding.tvName.setText(productModel.getName());
        if (productModel.isHasDiscount()) {
            binding.tvPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
        } else {
            binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
        }
        makeActionOnClickOnItem();
    }

    private void makeActionOnClickOnItem() {
        binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putExtra(PRODUCT_DETAIL, new Gson().toJson(productModel));
            v.getContext().startActivity(intent);
        });
    }
}
