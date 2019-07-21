package m.dp.i96mg.service.repository.remotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Lazy;
import m.dp.i96mg.service.model.request.CheckRequest;
import m.dp.i96mg.service.model.request.ProductsOrderRequest;
import m.dp.i96mg.service.model.response.CartResponse;
import m.dp.i96mg.service.model.response.DataResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ShopDetailsRepository {

    private Lazy<ApiInterfaces> endPointsLazy = inject(ApiInterfaces.class);

    public LiveData<Response<DataResponse>> getVoucherData(String voucher) {
        MutableLiveData<Response<DataResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().getVoucherData(voucher)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<DataResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<DataResponse> voucherResponseResponse) {
                        data.setValue(voucherResponseResponse);
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

    public LiveData<Response<Void>> checkProducts(CheckRequest checkRequest) {
        MutableLiveData<Response<Void>> data = new MutableLiveData<>();
        endPointsLazy.getValue().checkProducts(checkRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        data.setValue(voidResponse);
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

    public LiveData<Response<CartResponse>> getCartItems() {
        MutableLiveData<Response<CartResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().getCartItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CartResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<CartResponse> cartResponseResponse) {
                        data.setValue(cartResponseResponse);
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

    public LiveData<Response<MessageResponse>> createOrder(ProductsOrderRequest productsOrderRequest) {
        MutableLiveData<Response<MessageResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().createOrder(productsOrderRequest)
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
