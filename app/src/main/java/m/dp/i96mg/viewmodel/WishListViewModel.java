package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.service.repository.remotes.WishListRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class WishListViewModel extends AndroidViewModel {

    private Lazy<WishListRepository> wishListRepositoryLazy = inject(WishListRepository.class);
    private LiveData<Response<ProductsResponse>> data;

    public WishListViewModel(@NonNull Application application) {
        super(application);
    }

    public void getWishListItems() {
        data=wishListRepositoryLazy.getValue().getWishListItems();
    }


    public LiveData<Response<ProductsResponse>> getData() {
        return data;
    }
}