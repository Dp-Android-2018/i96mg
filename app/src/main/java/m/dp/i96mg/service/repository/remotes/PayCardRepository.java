package m.dp.i96mg.service.repository.remotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Lazy;
import m.dp.i96mg.service.model.request.ProductsOrderRequest;
import m.dp.i96mg.service.model.response.MessageResponse;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class PayCardRepository {

    private Lazy<ApiInterfaces> endPointsLazy = inject(ApiInterfaces.class);



}
