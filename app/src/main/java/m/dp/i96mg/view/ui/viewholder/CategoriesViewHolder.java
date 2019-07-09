package m.dp.i96mg.view.ui.viewholder;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import kotlin.Lazy;
import m.dp.i96mg.databinding.ItemMenuCategoryBinding;
import m.dp.i96mg.service.model.global.CategoriesResponeModel;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.view.ui.activity.MainActivity;

import static org.koin.java.standalone.KoinJavaComponent.inject;


public class CategoriesViewHolder extends RecyclerView.ViewHolder {
    ItemMenuCategoryBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private CategoriesResponeModel categoriesResponeModel;

    public CategoriesViewHolder(ItemMenuCategoryBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
    }

    public void bindClass(CategoriesResponeModel categoriesResponeModel) {
        this.categoriesResponeModel = categoriesResponeModel;
        binding.tvCategory.setText(categoriesResponeModel.getName());
        openMainActivity();
    }

    private void openMainActivity() {
        binding.tvCategory.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.putExtra(ConfigurationFile.Constants.Category_ID,categoriesResponeModel.getId());
            view.getContext().startActivity(intent);
            ((Activity) view.getContext()).finish();
        });

    }

}
