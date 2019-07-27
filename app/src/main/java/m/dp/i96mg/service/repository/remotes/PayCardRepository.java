package m.dp.i96mg.service.repository.remotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Lazy;
import m.dp.i96mg.service.model.request.BankRequest;
import m.dp.i96mg.service.model.request.LoginRequest;
import m.dp.i96mg.service.model.request.ProductsOrderRequest;
import m.dp.i96mg.service.model.response.BankAccountsResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.model.response.OrderResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Part;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PayCardRepository {

    private Lazy<ApiInterfaces> endPointsLazy = inject(ApiInterfaces.class);

    public LiveData<Response<OrderResponse>> payUsingPaybal(int orderId) {
        MutableLiveData<Response<OrderResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().payUsingPaybal(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<OrderResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<OrderResponse> orderResponseResponse) {
                        data.setValue(orderResponseResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return data;
    }

    public LiveData<Response<BankAccountsResponse>> getBankAccounts() {
        MutableLiveData<Response<BankAccountsResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().getBankAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<BankAccountsResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<BankAccountsResponse> bankAccountsResponseResponse) {
                        data.setValue(bankAccountsResponseResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return data;
    }

    public LiveData<Response<MessageResponse>> payUsingBankAccount(/*int orderId, BankRequest bankRequest*/
            int orderId, RequestBody bankAccountId,RequestBody fullName, MultipartBody.Part imageFile) {
        MutableLiveData<Response<MessageResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().payUsingBankAccount(orderId,bankAccountId,fullName,imageFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<MessageResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<MessageResponse> messageResponseResponse) {
                        data.setValue(messageResponseResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return data;
    }

}
