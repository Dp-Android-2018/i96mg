package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import m.dp.i96mg.service.model.global.LoginResponseModel;

public class LoginResponse {
    @SerializedName("data")
    private LoginResponseModel data;

    public LoginResponseModel getData() {
        return data;
    }

    public void setData(LoginResponseModel data) {
        this.data = data;
    }
}
