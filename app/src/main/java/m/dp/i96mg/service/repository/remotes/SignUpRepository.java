package m.dp.i96mg.service.repository.remotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Lazy;
import m.dp.i96mg.service.model.request.SignUpRequest;
import m.dp.i96mg.service.model.response.MessageResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class SignUpRepository {

    private Lazy<ApiInterfaces> endPointsLazy = inject(ApiInterfaces.class);

    public LiveData<Response<MessageResponse>> signUp(RequestBody phone,RequestBody firstName,RequestBody secondName,MultipartBody.Part imageFile) {
        MutableLiveData<Response<MessageResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().signUp(phone,firstName,secondName,imageFile)
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
