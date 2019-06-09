package m.dp.i96mg.service.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.ProductData;

public class CheckRequest {
    @SerializedName("products")
    private ArrayList<ProductData> productsData;

    public ArrayList<ProductData> getProductsData() {
        return productsData;
    }

    public void setProductsData(ArrayList<ProductData> productsData) {
        this.productsData = productsData;
    }
}
