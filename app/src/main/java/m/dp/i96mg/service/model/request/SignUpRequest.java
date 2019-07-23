package m.dp.i96mg.service.model.request;

import android.graphics.Bitmap;
import android.media.Image;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import retrofit2.http.Part;

public class SignUpRequest {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("phone")
    private String phone;

    /*@SerializedName("profile_picture")
    private MultipartBody.Part fileImageUrl;*/

    @SerializedName("profile_picture")
    private MultipartBody.Part imageBitmap;

    /*@SerializedName("profile_picture")
    private String profilePictureUrl;*/

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MultipartBody.Part getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(MultipartBody.Part imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
