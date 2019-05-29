package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.request.OrderRequest;
import m.dp.i96mg.service.model.response.OrderResponse;
import m.dp.i96mg.service.repository.remotes.PayCardRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PayCardActivityViewModel extends AndroidViewModel {

    private Lazy<PayCardRepository> payCardRepositoryLazy = inject(PayCardRepository.class);
    private LiveData<Response<OrderResponse>> data;

    public PayCardActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void createOrder(OrderRequest orderRequest) {
        data=payCardRepositoryLazy.getValue().createOrder(orderRequest);
    }

    public LiveData<Response<OrderResponse>> getData() {
        return data;
    }
}