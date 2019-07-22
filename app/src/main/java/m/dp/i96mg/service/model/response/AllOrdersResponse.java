package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.LinksModel;
import m.dp.i96mg.service.model.global.MetaDataModel;
import m.dp.i96mg.service.model.global.OrderResponseModel;

public class AllOrdersResponse {
    @SerializedName("data")
    private ArrayList<OrderResponseModel> data;

    @SerializedName("links")
    private LinksModel pageLinks;

    @SerializedName("meta")
    private MetaDataModel metaData;

    public ArrayList<OrderResponseModel> getData() {
        return data;
    }

    public void setData(ArrayList<OrderResponseModel> data) {
        this.data = data;
    }

    public LinksModel getPageLinks() {
        return pageLinks;
    }

    public void setPageLinks(LinksModel pageLinks) {
        this.pageLinks = pageLinks;
    }

    public MetaDataModel getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaDataModel metaData) {
        this.metaData = metaData;
    }
}
