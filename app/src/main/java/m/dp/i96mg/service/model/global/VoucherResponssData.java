package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class VoucherResponssData {
    @SerializedName("id")
    private int id;

    @SerializedName("is_flat")
    private boolean isFlat;

    @SerializedName("discount_amount")
    private float discountAmount;

    @SerializedName("max_discount")
    private float maxDiscount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFlat() {
        return isFlat;
    }

    public void setFlat(boolean flat) {
        isFlat = flat;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public float getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(float maxDiscount) {
        this.maxDiscount = maxDiscount;
    }
}
