package m.dp.i96mg.view.ui.viewholder;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.view.ui.activity.ProductDetailsActivity;

import static java.security.AccessController.getContext;
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
        binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
        if (productModel.isHasDiscount()) {
            binding.tvPriceDiscount.setText(String.valueOf(productModel.getOriginalPrice()));
            binding.tvPriceDiscount.setPaintFlags(binding.tvPriceDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvPriceDiscount.setTextColor(Color.GRAY);
            binding.tvPriceDiscount.setTextSize(9);
            binding.tvPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
        }
        makeActionOnClickOnItem();
    }

    private void makeActionOnClickOnItem() {
        binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putExtra(PRODUCT_DETAIL, new Gson().toJson(productModel));

//            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(v.getContext(),
//                    R.anim.fade_in, R.anim.fade_out).toBundle();
//            v.getContext().startActivity(intent, bundle);
            v.getContext().startActivity(intent);

        });
    }
}
