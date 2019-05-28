package m.dp.i96mg.view.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.view.ui.viewholder.ProductsViewHolder;


public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsViewHolder> {
    private List<ProductModel> pageImages;

    public ProductsRecyclerViewAdapter(List<ProductModel> pageImages) {
        this.pageImages=pageImages;
    }


    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_product_layout, parent, false);
        return new ProductsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        holder.bindClass(pageImages.get(position));
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
