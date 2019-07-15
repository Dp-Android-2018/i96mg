package m.dp.i96mg.service.model.request;

import com.google.gson.annotations.SerializedName;

public class WishListRequest {
    @SerializedName("product_id")
    private int productId;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
