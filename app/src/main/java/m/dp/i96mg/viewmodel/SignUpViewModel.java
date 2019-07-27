package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.request.SignUpRequest;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.repository.remotes.SignUpRepository;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class SignUpViewModel extends AndroidViewModel {

    private Lazy<SignUpRepository> signUpRepositoryLazy = inject(SignUpRepository.class);

    public SignUpViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Response<MessageResponse>> signUp(RequestBody phone,RequestBody firstName,RequestBody secondName, MultipartBody.Part imageFile) {
        return signUpRepositoryLazy.getValue().signUp(phone,firstName,secondName,imageFile);
    }

}