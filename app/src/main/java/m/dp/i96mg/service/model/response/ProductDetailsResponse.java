package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import m.dp.i96mg.service.model.global.LinksModel;
import m.dp.i96mg.service.model.global.MetaDataModel;
import m.dp.i96mg.service.model.global.ProductModel;

public class ProductDetailsResponse {

    @SerializedName("data")
    private ProductModel data;

    @SerializedName("links")
    private LinksModel pageLinks;

    @SerializedName("meta")
    private MetaDataModel metaData;

    public ProductModel getData() {
        return data;
    }

    public void setData(ProductModel data) {
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
