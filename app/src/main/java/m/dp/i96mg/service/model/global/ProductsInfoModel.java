package m.dp.i96mg.service.model.global;

import com.google.gson.annotations.SerializedName;

public class ProductsInfoModel {
    @SerializedName("id")
    private int id;

    @SerializedName("type")
    private String type;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("whatsapp")
    private String whatsapp;

    public ProductsInfoModel() {
    }

    public ProductsInfoModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
}
