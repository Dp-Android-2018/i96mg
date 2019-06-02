package m.dp.i96mg.service.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.ProductData;

public class OrderRequest implements Parcelable {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("country")
    private String country;

    @SerializedName("region")
    private String region;

    @SerializedName("zip_code")
    private String zipCode;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("products")
    private ArrayList<ProductData> productsData;

    @SerializedName("voucher")
    private String voucher;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("expiration_date")
    private String expirationDate;

    @SerializedName("security_code")
    private String securityCode;

    @SerializedName("cardholder_name")
    private String cardholderName;

    public OrderRequest(Parcel in) {
        name = in.readString();
        email = in.readString();
        address = in.readString();
        country = in.readString();
        region = in.readString();
        zipCode = in.readString();
        phoneNumber = in.readString();
        voucher = in.readString();
        paymentMethod = in.readString();
        cardNumber = in.readString();
        expirationDate = in.readString();
        securityCode = in.readString();
        cardholderName = in.readString();
    }

    public static final Creator<OrderRequest> CREATOR = new Creator<OrderRequest>() {
        @Override
        public OrderRequest createFromParcel(Parcel in) {
            return new OrderRequest(in);
        }

        @Override
        public OrderRequest[] newArray(int size) {
            return new OrderRequest[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<ProductData> getProductsData() {
        return productsData;
    }

    public void setProductsData(ArrayList<ProductData> productsData) {
        this.productsData = productsData;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(country);
        dest.writeString(region);
        dest.writeString(zipCode);
        dest.writeString(phoneNumber);
        dest.writeString(voucher);
        dest.writeString(paymentMethod);
        dest.writeString(cardNumber);
        dest.writeString(expirationDate);
        dest.writeString(securityCode);
        dest.writeString(cardholderName);
    }
}
