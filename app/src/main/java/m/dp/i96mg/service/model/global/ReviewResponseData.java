package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class ReviewResponseData {

    @SerializedName("id")
    private int id;

    @SerializedName("rating")
    private float rating;

    @SerializedName("review")
    private String review;

    @SerializedName("user")
    private UserData user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }
}
