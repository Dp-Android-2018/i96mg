package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.service.repository.remotes.MainRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class MainActivityViewModel extends AndroidViewModel {

    private Lazy<MainRepository> mainRepositoryLazy = inject(MainRepository.class);
    private LiveData<Response<ProductsResponse>> data;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void getProducts(String categoryId, int pageNumber) {
        data=mainRepositoryLazy.getValue().getProducts(categoryId,pageNumber);
    }

    public LiveData<Response<ProductsResponse>> getData() {
        return data;
    }
}