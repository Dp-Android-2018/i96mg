package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class LinksModel {
    @SerializedName("first")
    private String firstPageLink;

    @SerializedName("last")
    private String lastPageLink;

    @SerializedName("prev")
    private String prevPageLink;

    @SerializedName("next")
    private String nextPageLink;

    public String getFirstPageLink() {
        return firstPageLink;
    }

    public void setFirstPageLink(String firstPageLink) {
        this.firstPageLink = firstPageLink;
    }

    public String getLastPageLink() {
        return lastPageLink;
    }

    public void setLastPageLink(String lastPageLink) {
        this.lastPageLink = lastPageLink;
    }

    public String getPrevPageLink() {
        return prevPageLink;
    }

    public void setPrevPageLink(String prevPageLink) {
        this.prevPageLink = prevPageLink;
    }

    public String getNextPageLink() {
        return nextPageLink;
    }

    public void setNextPageLink(String nextPageLink) {
        this.nextPageLink = nextPageLink;
    }
}
