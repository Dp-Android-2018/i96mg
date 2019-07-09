package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import m.dp.i96mg.service.model.global.CategoriesResponeModel;
import m.dp.i96mg.service.model.global.SettingsResponseModel;

public class CategoriesResponse {

    @SerializedName("categories")
    private ArrayList<CategoriesResponeModel> categories;

    @SerializedName("settings")
    private SettingsResponseModel settings;

    public ArrayList<CategoriesResponeModel> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoriesResponeModel> categories) {
        this.categories = categories;
    }

    public SettingsResponseModel getSettings() {
        return settings;
    }

    public void setSettings(SettingsResponseModel settings) {
        this.settings = settings;
    }
}
