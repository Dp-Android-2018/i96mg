package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import m.dp.i96mg.service.model.global.VoucherResponssData;

public class VoucherResponse {
    @SerializedName("data")
    private VoucherResponssData data;

    public VoucherResponssData getData() {
        return data;
    }

    public void setData(VoucherResponssData data) {
        this.data = data;
    }
}
