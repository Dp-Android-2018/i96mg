package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class SocialNetworksModel {
    @SerializedName("facebook")
    private String facebookUrl;

    @SerializedName("twitter")
    private String twitterUrl;

    @SerializedName("instagram")
    private String instagramUrl;

    @SerializedName("youtube")
    private String youtubeUrl;

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }
}
