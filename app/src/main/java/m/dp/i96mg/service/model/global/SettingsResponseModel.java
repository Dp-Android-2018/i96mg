package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class SettingsResponseModel {
    @SerializedName("social_networks")
    private SocialNetworksModel socialNetworks;

    public SocialNetworksModel getSocialNetworks() {
        return socialNetworks;
    }

    public void setSocialNetworks(SocialNetworksModel socialNetworks) {
        this.socialNetworks = socialNetworks;
    }
}
