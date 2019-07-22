package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.BankAccountResponseModel;

public class BankAccountsResponse {
    @SerializedName("data")
    private ArrayList<BankAccountResponseModel> data;

    public ArrayList<BankAccountResponseModel> getData() {
        return data;
    }

    public void setData(ArrayList<BankAccountResponseModel> data) {
        this.data = data;
    }
}
