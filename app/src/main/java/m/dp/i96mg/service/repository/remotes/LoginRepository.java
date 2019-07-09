package m.dp.i96mg.service.repository.remotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Lazy;
import m.dp.i96mg.service.model.request.LoginRequest;
import m.dp.i96mg.service.model.response.LoginResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class LoginRepository {

    private Lazy<ApiInterfaces> endPointsLazy = inject(ApiInterfaces.class);

    public LiveData<Response<MessageResponse>> sendLoginCode(LoginRequest loginRequest) {
        MutableLiveData<Response<MessageResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().sendLoginCode(loginRequest)
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

    public LiveData<Response<LoginResponse>> login(LoginRequest loginRequest) {
        MutableLiveData<Response<LoginResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().login(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<LoginResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<LoginResponse> loginResponseResponse) {
                        data.setValue(loginResponseResponse);
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
