package m.dp.i96mg.view.ui.callback;


import m.dp.i96mg.databinding.ItemProductLayoutBinding;
import m.dp.i96mg.service.model.global.ProductModel;

public interface OnItemClickListener {
    void addItemToWishList(int id, ItemProductLayoutBinding binding);
    void removeItemFromWishList(int id, ItemProductLayoutBinding binding);
    void addItemToCart(ProductModel productModel, ItemProductLayoutBinding binding);

}
