package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.request.LoginRequest;
import m.dp.i96mg.service.model.response.AllOrdersResponse;
import m.dp.i96mg.service.repository.remotes.OrdersRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class OrdersViewModel extends AndroidViewModel {

    private Lazy<OrdersRepository> ordersRepositoryLazy = inject(OrdersRepository.class);

    public OrdersViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Response<AllOrdersResponse>> getPendingOrders() {
        return ordersRepositoryLazy.getValue().getPendingOrders();
    }

    public LiveData<Response<AllOrdersResponse>> getOrders() {
        return ordersRepositoryLazy.getValue().getOrders();
    }
}