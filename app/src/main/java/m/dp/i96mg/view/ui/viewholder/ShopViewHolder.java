package m.dp.i96mg.view.ui.viewholder;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Lazy;
import m.dp.i96mg.R;
import m.dp.i96mg.databinding.ItemShopCartBinding;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.global.ProductModel;
import m.dp.i96mg.service.model.global.ProductsInfoModel;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.response.ErrorResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.utility.utils.ConfigurationFile;
import m.dp.i96mg.utility.utils.CustomUtils;
import m.dp.i96mg.utility.utils.SharedUtils;
import m.dp.i96mg.utility.utils.ValidationUtils;
import m.dp.i96mg.view.ui.adapter.ShopRecyclerViewAdapter;
import m.dp.i96mg.view.ui.callback.OnOperationClicked;
import m.dp.i96mg.view.ui.callback.OnQuantityChanged;
import m.dp.i96mg.viewmodel.ProductDetailsViewModel;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.EMAIL_EDITTEXT;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PASSWORD_EDITTEXT;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.PRODUCTTYPE_EDITTEXT;
import static m.dp.i96mg.utility.utils.ConfigurationFile.Constants.WHATSAPP_EDITTEXT;
import static org.koin.java.standalone.KoinJavaComponent.inject;


public class ShopViewHolder extends RecyclerView.ViewHolder {
    ItemShopCartBinding binding;
    private Lazy<CustomUtils> customUtilsLazy = inject(CustomUtils.class);
    private Lazy<ProductDetailsViewModel> productDetailsViewModelLazy = inject(ProductDetailsViewModel.class);
    private ShopRecyclerViewAdapter shopRecyclerViewAdapter;
    private ProductModel productModel;
    private List<ProductModel> productModelList;
    private int quantity;
    private float price;
    private float totalprice;
    private OnQuantityChanged onQuantityChanged;
    private int index;
    private OnOperationClicked onOperationClicked;
    private ArrayList<ProductsInfoModel> productsInfoModels;
    private ArrayList<ProductsInfoModel> savedProductsInfo;
    private ProductsInfoModel mProductsInfoModel;
    private int position;


    public ShopViewHolder(ItemShopCartBinding itemProductLayoutBinding) {
        super(itemProductLayoutBinding.getRoot());
        this.binding = itemProductLayoutBinding;
    }

    public void bindClass(ProductModel productModel, OnQuantityChanged onQuantityChanged, OnOperationClicked onOperationClicked, List<ProductModel> pageImages, int position) {
        this.productModel = productModel;
        this.onQuantityChanged = onQuantityChanged;
        this.onOperationClicked = onOperationClicked;
        productModelList = new ArrayList<>();
        mProductsInfoModel = new ProductsInfoModel();
        productsInfoModels = new ArrayList<>();
        if (customUtilsLazy.getValue().getSavedProductsInfo() != null) {
            productsInfoModels = customUtilsLazy.getValue().getSavedProductsInfo();
        }
        for (int i = 0; i < productsInfoModels.size(); i++) {
            if (productsInfoModels.get(i).getId() == productModel.getId()) {
                mProductsInfoModel = productsInfoModels.get(i);
            }
        }

        this.position = position;
        ImageView ivGalleryPhoto = binding.ivProductImage;
        Picasso.get().load(productModel.getImageUrl()).into(ivGalleryPhoto);
        binding.tvName.setText(productModel.getName());
        binding.tvPrice.setText(String.valueOf(productModel.getOriginalPrice()));
        if (productModel.isHasDiscount()) {
            binding.tvDiscountPrice.setText(String.valueOf(productModel.getOriginalPrice()));
            binding.tvDiscountPrice.setPaintFlags(binding.tvDiscountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvSr.setPaintFlags(binding.tvDiscountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvDiscountPrice.setTextColor(Color.GRAY);
            binding.tvSr.setTextColor(Color.GRAY);
            binding.tvDiscountPrice.setTextSize(9);
            binding.tvSr.setTextSize(9);
            binding.tvSr.setVisibility(View.VISIBLE);
            binding.tvPrice.setText(String.valueOf(productModel.getDiscountedPrice()));
        }
        binding.tvQuantityNum.setText(String.valueOf(productModel.getOrderedQuantity()));
        if (productModel.isHasDiscount()) {
            totalprice = productModel.getOrderedQuantity() * productModel.getDiscountedPrice();
        } else {
            totalprice = productModel.getOrderedQuantity() * productModel.getOriginalPrice();
        }
        binding.tvTotalPrice.setText(String.valueOf(totalprice));
        quantity = productModel.getOrderedQuantity();
        productModelList = customUtilsLazy.getValue().getSavedProductsData();
        makeActionOnClickOnQuantityIcons();
        makeListenersOnItemsData();
    }

    private void makeActionOnClickOnQuantityIcons() {
        binding.tvPlus.setOnClickListener(v -> {
            if (quantity < productModel.getQuantity()) {
                quantity++;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
                productModel.setOrderedQuantity(quantity);
                if (productModel.isHasDiscount()) {
                    price = productModel.getDiscountedPrice() * quantity;
                } else {
                    price = productModel.getOriginalPrice() * quantity;
                }
                binding.tvTotalPrice.setText(String.valueOf(price));
                if (isLoggedIn()) {
                    onOperationClicked.plusIconClicked(getAdapterPosition(), productModel);
                } else {
                    addThisProductToSharedPreferences();
                }
            }
        });

        binding.tvMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantityNum.setText(String.valueOf(quantity));
                productModel.setOrderedQuantity(quantity);
                if (productModel.isHasDiscount()) {
                    price = productModel.getDiscountedPrice() * quantity;
                } else {
                    price = productModel.getOriginalPrice() * quantity;
                }
                binding.tvTotalPrice.setText(String.valueOf(price));
                if (isLoggedIn()) {
                    onOperationClicked.minusIconClicked(getAdapterPosition(), productModel);
                } else {
                    addThisProductToSharedPreferences();
                }
            }
        });
    }

    private void addThisProductToSharedPreferences() {
        for (int i = 0; i < productModelList.size(); i++) {
            if (productModelList.get(i).getId() == productModel.getId()) {
                productModelList.remove(i);
                break;
            }
        }
        productModelList.add(productModel);
        customUtilsLazy.getValue().saveProductToPrefs(productModelList);
    }

    private void makeListenersOnItemsData() {
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mProductsInfoModel.setEmail(editable.toString());
//                saveProductInfo();
                onOperationClicked.dataChanged(position,binding, mProductsInfoModel);
            }
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mProductsInfoModel.setPassword(editable.toString());
//                saveProductInfo();
                onOperationClicked.dataChanged(position,binding, mProductsInfoModel);
            }
        });
        binding.etProductType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mProductsInfoModel.setType(editable.toString());
//                saveProductInfo();
                onOperationClicked.dataChanged(position,binding, mProductsInfoModel);
            }
        });
        binding.etWhatsapp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mProductsInfoModel.setWhatsapp(editable.toString());
//                saveProductInfo();
                onOperationClicked.dataChanged(position,binding, mProductsInfoModel);
            }
        });

    }

    private void saveProductInfo() {
        for (int i = 0; i < productsInfoModels.size(); i++) {
            if (productsInfoModels.get(i).getId() == mProductsInfoModel.getId()) {
                productsInfoModels.remove(i);
            }
        }
        productsInfoModels.add(mProductsInfoModel);
        customUtilsLazy.getValue().saveProductInfoToPrefs(productsInfoModels);
    }

    private boolean isLoggedIn() {
        if (customUtilsLazy.getValue().getSavedMemberData() != null) {
            return true;
        } else {
            return false;
        }
    }

}
