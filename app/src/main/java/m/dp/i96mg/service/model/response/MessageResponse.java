package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("url")
    private String imageUrl;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
