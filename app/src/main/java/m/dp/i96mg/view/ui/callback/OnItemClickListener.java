package m.dp.i96mg.view.ui.callback;


import m.dp.i96mg.databinding.ItemProductLayoutBinding;

public interface OnItemClickListener {
    void addItemToWishList(int id, ItemProductLayoutBinding binding);
    void removeItemFromWishList(int id, ItemProductLayoutBinding binding);

    void onDeleteClick(int id);

//    void onStartClick(int position, ItemDestinationRvLayoutBinding binding);

//    void onEndClick(int position, ItemDestinationRvLayoutBinding binding);


}
