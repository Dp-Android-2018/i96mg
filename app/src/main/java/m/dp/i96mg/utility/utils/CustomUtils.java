package m.dp.i96mg.utility.utils;


import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.LoginResponseModel;
import m.dp.i96mg.service.model.global.ProductsInfoModel;

import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.FAVORITE_PRODUCTS_LIST;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PRODUCTS_INFO;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PRODUCTS_LIST;
import static org.koin.java.standalone.KoinJavaComponent.inject;

public class CustomUtils {
    private Lazy<SharedPreferenceHandler> prefrenceUtils = inject(SharedPreferenceHandler.class);

    public LoginResponseModel getSavedMemberData() {
        return (LoginResponseModel) prefrenceUtils.getValue().getSavedObject(ConfigurationFile.SharedPrefConstants.SHARED_PREF_NAME, LoginResponseModel.class);
    }

    public void saveMemberDataToPrefs(LoginResponseModel data) {
        prefrenceUtils.getValue().saveObjectToSharedPreferences(ConfigurationFile.SharedPrefConstants.SHARED_PREF_NAME, data);
    }

    /*public void saveProductIdToPrefs(int id) {
        prefrenceUtils.getValue().saveProductIdsToSharedPreferences(ConfigurationFile.Constants.PRODUCT_IDS, id);
    }

    public List<Integer> getSavedProductIds(int id) {
        return (List<Integer>) prefrenceUtils.getValue().getSavedObject(String.valueOf(id), List<Integer>.class);
    }*/

    public void saveProductToPrefs(List<ProductModel> productModels) {
        prefrenceUtils.getValue().saveObjectToSharedPreferences(PRODUCTS_LIST, productModels);
    }

    public List<ProductModel> getSavedProductsData() {
        return prefrenceUtils.getValue().getSavedProducts(PRODUCTS_LIST);
    }

    public void saveProductInfoToPrefs(ArrayList<ProductsInfoModel> productsInfoModels) {
        prefrenceUtils.getValue().saveObjectToSharedPreferences(PRODUCTS_INFO, productsInfoModels);
    }

    public ArrayList<ProductsInfoModel> getSavedProductsInfo() {
        return prefrenceUtils.getValue().getSavedProductsInfo(PRODUCTS_INFO);
    }

    public void saveFavoriteProductToPrefs(List<ProductModel> productModels) {
        prefrenceUtils.getValue().saveObjectToSharedPreferences(FAVORITE_PRODUCTS_LIST, productModels);
    }

    public List<ProductModel> getFavoriteSavedProductsData() {
        return prefrenceUtils.getValue().getSavedProducts(FAVORITE_PRODUCTS_LIST);
    }

    public void saveActivationTypeToPrefs(String objName, boolean objValue) {
        prefrenceUtils.getValue().saveMemberTypeSharedPreferences(objName, objValue);
    }

    public boolean getSavedActivationType() {
        return prefrenceUtils.getValue().getSavedActivationType();
    }


    public void saveLanguageTypeToPrefs(String langValue) {
        prefrenceUtils.getValue().saveLanguageTypeSharedPreferences(ConfigurationFile.Constants.LANGUAGE_TYPE, langValue);
    }

    public String getSavedLanguageType() {
        return prefrenceUtils.getValue().getSavedLanguageType();
    }


    public void clearSharedPref() {
        prefrenceUtils.getValue().clearToken();
    }
}
