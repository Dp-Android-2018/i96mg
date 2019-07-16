package m.dp.i96mg.service.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.ProductsInfoModel;

public class ProductsOrderRequest {
    @SerializedName("voucher")
    private String voucher;

    @SerializedName("products")
    private ArrayList<ProductsInfoModel> productsInfoModels;

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public ArrayList<ProductsInfoModel> getProductsInfoModels() {
        return productsInfoModels;
    }

    public void setProductsInfoModels(ArrayList<ProductsInfoModel> productsInfoModels) {
        this.productsInfoModels = productsInfoModels;
    }
}
