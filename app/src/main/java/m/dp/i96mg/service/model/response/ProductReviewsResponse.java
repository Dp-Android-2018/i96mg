package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.ReviewResponseData;
import m.dp.i96mg.service.model.global.VoucherResponssData;

public class ProductReviewsResponse {

    @SerializedName("data")
    private ArrayList<ReviewResponseData> data;

    public ArrayList<ReviewResponseData> getData() {
        return data;
    }

    public void setData(ArrayList<ReviewResponseData> data) {
        this.data = data;
    }
}
