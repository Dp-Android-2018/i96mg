package m.dp.i96mg.service.model.request;

import com.google.gson.annotations.SerializedName;

public class BankRequest {
    @SerializedName("bank_account_id")
    private int bankAccountId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("picture")
    private String pictureUrl;

    public int getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(int bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
