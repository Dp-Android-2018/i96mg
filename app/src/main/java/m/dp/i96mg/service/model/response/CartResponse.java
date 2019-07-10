package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.CartResponseModel;

public class CartResponse {
    @SerializedName("data")
    private ArrayList<CartResponseModel> data;

    @SerializedName("total")
    private float total;

    public ArrayList<CartResponseModel> getData() {
        return data;
    }

    public void setData(ArrayList<CartResponseModel> data) {
        this.data = data;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
