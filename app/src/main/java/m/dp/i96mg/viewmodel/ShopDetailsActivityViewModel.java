package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import kotlin.Lazy;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.request.CheckRequest;
import m.dp.i96mg.service.model.response.VoucherResponse;
import m.dp.i96mg.service.repository.remotes.ShopDetailsRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ShopDetailsActivityViewModel extends AndroidViewModel {

    private Lazy<ShopDetailsRepository> shopDetailsRepositoryLazy = inject(ShopDetailsRepository.class);
    private LiveData<Response<VoucherResponse>> data;

    public ShopDetailsActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void getVoucherData(String voucher) {
        data=shopDetailsRepositoryLazy.getValue().getVoucherData(voucher);
    }

    public LiveData<Response<Void>> checkProducts(CheckRequest checkRequest) {
        return shopDetailsRepositoryLazy.getValue().checkProducts(checkRequest);
    }


    public LiveData<Response<VoucherResponse>> getData() {
        return data;
    }
}