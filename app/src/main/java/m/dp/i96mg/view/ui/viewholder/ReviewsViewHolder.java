package m.dp.i96mg.view.ui.viewholder;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.databinding.ReviewItemLayoutBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.ReviewResponseData;
import m.dp.i96mg.utility.utils.CustomUtils;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class ReviewsViewHolder extends RecyclerView.ViewHolder {
    ReviewItemLayoutBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private ReviewResponseData productModel;
    private List<ProductModel> productModelList;
    private int quantity;
    private float price;
    private float totalprice;

    public ReviewsViewHolder(ReviewItemLayoutBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
    }

    public void bindClass(ReviewResponseData productModel) {
        this.productModel = productModel;
        binding.tvName.setText(productModel.getUser().getName());
        binding.tvComment.setText(productModel.getReview());
        binding.ratingBar.setRating(productModel.getRating());
        ImageView ivGalleryPhoto = binding.ivUser;
        Picasso.get().load(productModel.getUser().getProfilePictureUrl()).into(ivGalleryPhoto);

    }

}
