package m.dp.i96mg.service.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.ProductData;

public class CartRequest {
    @SerializedName("items")
    private ArrayList<ProductData> items;

    public ArrayList<ProductData> getItems() {
        return items;
    }

    public void setItems(ArrayList<ProductData> items) {
        this.items = items;
    }
}
