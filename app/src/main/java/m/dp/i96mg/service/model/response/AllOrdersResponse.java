package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.OrderResponseModel;

public class AllOrdersResponse {
    @SerializedName("data")
    private ArrayList<OrderResponseModel> data;

    public ArrayList<OrderResponseModel> getData() {
        return data;
    }

    public void setData(ArrayList<OrderResponseModel> data) {
        this.data = data;
    }
}
