package m.dp.i96mg.view.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;
import m.dp.i96mg.view.ui.viewholder.ShopViewHolder;


public class ShopRecyclerViewAdapter extends RecyclerView.Adapter<ShopViewHolder> {
    private List<ProductModel> pageImages;
    private OnQuantityChanged onQuantityChanged;

    public ShopRecyclerViewAdapter(List<ProductModel> pageImages, OnQuantityChanged onQuantityChanged) {
        this.pageImages=pageImages;
        this.onQuantityChanged=onQuantityChanged;
    }


    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShopCartBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_shop_cart, parent, false);
        return new ShopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        holder.bindClass(pageImages.get(position),onQuantityChanged);
    }

    @Override
    public int getItemCount() {
        if (pageImages !=null){
            return pageImages.size();
        }else {
            return 0;
        }
    }

    public void setPageImages(List<ProductModel> pageImages) {
        this.pageImages = pageImages;
    }
}
