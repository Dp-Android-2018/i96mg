package m.dp.i96mg.view.ui.callback;

import m.dp.i96mg.service.model.global.ProductModel;

public interface OnOperationClicked {

    void plusIconClicked(int position, ProductModel productModel);

    void minusIconClicked(int position,ProductModel productModel);

}
