package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class OrderResponseModel {
    @SerializedName("id")
    private int id;

    @SerializedName("no")
    private String orderNo;

    @SerializedName("status_string")
    private String orderStatus;

    @SerializedName("total")
    private float totalPrice;

    @SerializedName("time_left")
    private String timeLeft;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }
}
