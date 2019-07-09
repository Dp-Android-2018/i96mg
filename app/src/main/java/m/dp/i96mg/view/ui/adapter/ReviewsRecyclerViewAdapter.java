package m.dp.i96mg.view.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.cdsrecyclerview.CdsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.databinding.ReviewItemLayoutBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.ReviewResponseData;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;
import m.dp.i96mg.view.ui.viewholder.ProductsViewHolder;
import m.dp.i96mg.view.ui.viewholder.ReviewsViewHolder;
import m.dp.i96mg.view.ui.viewholder.ShopViewHolder;


public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsViewHolder> {
    private ArrayList<ReviewResponseData> reviewResponseData;

    public ReviewsRecyclerViewAdapter(ArrayList<ReviewResponseData> reviewResponseData) {
        this.reviewResponseData = reviewResponseData;
    }


    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReviewItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.review_item_layout, parent, false);
        return new ReviewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        holder.bindClass(reviewResponseData.get(position));
    }

    @Override
    public int getItemCount() {
        if (reviewResponseData != null) {
            return reviewResponseData.size();
        } else {
            return 0;
        }
    }

    public void setPageImages(ArrayList<ReviewResponseData> reviewResponseData) {
        this.reviewResponseData = reviewResponseData;
    }
}
