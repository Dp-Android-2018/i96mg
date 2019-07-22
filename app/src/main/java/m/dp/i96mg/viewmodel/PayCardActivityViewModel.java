package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.request.BankRequest;
import m.dp.i96mg.service.model.response.BankAccountsResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.model.response.OrderResponse;
import m.dp.i96mg.service.repository.remotes.PayCardRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PayCardActivityViewModel extends AndroidViewModel {

    private Lazy<PayCardRepository> payCardRepositoryLazy = inject(PayCardRepository.class);

    public PayCardActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Response<OrderResponse>> payUsingPaybal(int orderId) {
        return payCardRepositoryLazy.getValue().payUsingPaybal(orderId);
    }

    public LiveData<Response<BankAccountsResponse>> getBankAccounts() {
        return payCardRepositoryLazy.getValue().getBankAccounts();
    }

    public LiveData<Response<MessageResponse>> payUsingBankAccount(int orderId, BankRequest bankRequest) {
        return payCardRepositoryLazy.getValue().payUsingBankAccount(orderId,bankRequest);
    }

}