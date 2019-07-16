package m.dp.i96mg.view.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.gaurav.cdsrecyclerview.CdsRecyclerViewAdapter;

import java.util.List;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.view.ui.callback.OnOperationClicked;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;
import m.dp.i96mg.view.ui.viewholder.ShopViewHolder;


public class ShopRecyclerViewAdapter extends CdsRecyclerViewAdapter<ProductModel,ShopViewHolder> {
    private List<ProductModel> pageImages;
    private OnQuantityChanged onQuantityChanged;
    private OnOperationClicked onOperationClicked;

    public ShopRecyclerViewAdapter(Context context, List<ProductModel> pageImages, OnQuantityChanged onQuantityChanged, OnOperationClicked onOperationClicked) {
        super(context,pageImages);
        this.pageImages=pageImages;
        this.onQuantityChanged=onQuantityChanged;
        this.onOperationClicked = onOperationClicked;
    }


    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShopCartBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_shop_cart, parent, false);
        return new ShopViewHolder(binding);
    }

    @Override
    public void bindHolder(ShopViewHolder holder, int position) {
        holder.bindClass(pageImages.get(position),onQuantityChanged, onOperationClicked);
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

    public ProductModel getItemAtPosition(int position){
        return pageImages.get(position);
    }
}
