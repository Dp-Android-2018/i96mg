package m.dp.i96mg.service.model.global;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ProductModel implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("has_discount")
    private boolean hasDiscount;

    @SerializedName("original_price")
    private float originalPrice;

    @SerializedName("discounted_price")
    private float discountedPrice;

    @SerializedName("ordered_quantity")
    private int orderedQuantity;

    @SerializedName("in_cart")
    private boolean inCart;

    @SerializedName("in_wishlist")
    private boolean inWishlist;

    @SerializedName("description")
    private String description;

    @SerializedName("rating")
    private float rating;

    public ProductModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imageUrl = in.readString();
        quantity = in.readInt();
        hasDiscount = in.readByte() != 0;
        originalPrice = in.readFloat();
        discountedPrice = in.readFloat();
        orderedQuantity = in.readInt();
        inCart = in.readByte() != 0;
        inWishlist = in.readByte() != 0;
        description = in.readString();
        rating = in.readFloat();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public float getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(float discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }

    public boolean isInWishlist() {
        return inWishlist;
    }

    public void setInWishlist(boolean inWishlist) {
        this.inWishlist = inWishlist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(imageUrl);
        parcel.writeInt(quantity);
        parcel.writeByte((byte) (hasDiscount ? 1 : 0));
        parcel.writeFloat(originalPrice);
        parcel.writeFloat(discountedPrice);
        parcel.writeInt(orderedQuantity);
        parcel.writeByte((byte) (inCart ? 1 : 0));
        parcel.writeByte((byte) (inWishlist ? 1 : 0));
        parcel.writeString(description);
        parcel.writeFloat(rating);
    }
}
