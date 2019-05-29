package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("url")
    private String url;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
