package m.dp.i96mg.service.model.request;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {
    @SerializedName("product_id")
    private int productId;

    @SerializedName("rating")
    private float rating;

    @SerializedName("review")
    private String review;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
