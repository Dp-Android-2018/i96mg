package m.dp.i96mg.view.ui.callback;

import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.ProductsInfoModel;

public interface OnOperationClicked {

    void plusIconClicked(int position, ProductModel productModel);

    void minusIconClicked(int position,ProductModel productModel);

    void dataChanged(int position, ProductsInfoModel productsInfoModel);

}
