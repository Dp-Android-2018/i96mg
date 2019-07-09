package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.request.LoginRequest;
import m.dp.i96mg.service.model.response.LoginResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.repository.remotes.LoginRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class LoginViewModel extends AndroidViewModel {

    private Lazy<LoginRepository> loginRepositoryLazy = inject(LoginRepository.class);

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Response<MessageResponse>> sendLoginCode(LoginRequest loginRequest) {
        return loginRepositoryLazy.getValue().sendLoginCode(loginRequest);
    }

    public LiveData<Response<LoginResponse>> login(LoginRequest loginRequest) {
        return loginRepositoryLazy.getValue().login(loginRequest);
    }
}