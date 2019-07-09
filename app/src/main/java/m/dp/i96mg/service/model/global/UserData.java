package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("name")
    private String name;

    @SerializedName("profile_picture")
    private String profilePictureUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
