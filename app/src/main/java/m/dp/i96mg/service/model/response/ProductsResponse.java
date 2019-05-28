package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.LinksModel;
import m.dp.i96mg.service.model.global.MetaDataModel;
import m.dp.i96mg.service.model.global.ProductModel;

public class ProductsResponse {
    @SerializedName("data")
    private ArrayList<ProductModel> data;

    @SerializedName("links")
    private LinksModel pageLinks;

    @SerializedName("meta")
    private MetaDataModel metaData;

    public ArrayList<ProductModel> getData() {
        return data;
    }

    public void setData(ArrayList<ProductModel> data) {
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
