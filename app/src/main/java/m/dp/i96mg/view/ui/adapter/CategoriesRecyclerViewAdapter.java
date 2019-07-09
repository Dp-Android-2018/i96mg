package m.dp.i96mg.view.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemMenuCategoryBinding;
import m.dp.i96mg.service.model.global.CategoriesResponeModel;
import m.dp.i96mg.view.ui.viewholder.CategoriesViewHolder;


public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {
    private ArrayList<CategoriesResponeModel> categoriesResponeModels;

    public CategoriesRecyclerViewAdapter(ArrayList<CategoriesResponeModel> categoriesResponeModels) {
        this.categoriesResponeModels = categoriesResponeModels;
    }


    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_menu_category, parent, false);
        return new CategoriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        holder.bindClass(categoriesResponeModels.get(position));
    }

    @Override
    public int getItemCount() {
        if (categoriesResponeModels != null) {
            return categoriesResponeModels.size();
        } else {
            return 0;
        }
    }
}
