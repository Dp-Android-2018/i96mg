package m.dp.i96mg.service.repository.remotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Lazy;
import m.dp.i96mg.service.model.request.ReviewRequest;
import m.dp.i96mg.service.model.response.ProductDetailsResponse;
import m.dp.i96mg.service.model.response.ProductReviewsResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ProductDetailsRepository {

    private Lazy<ApiInterfaces> endPointsLazy = inject(ApiInterfaces.class);

    public LiveData<Response<ProductDetailsResponse>> getProductById(int productId) {
        MutableLiveData<Response<ProductDetailsResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().getProductById(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ProductDetailsResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ProductDetailsResponse> productDetailsResponseResponse) {
                        data.setValue(productDetailsResponseResponse);
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

    public LiveData<Response<ProductReviewsResponse>> getProductReviews(int productId) {
        MutableLiveData<Response<ProductReviewsResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().getProductReviews(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ProductReviewsResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ProductReviewsResponse> productDetailsResponseResponse) {
                        data.setValue(productDetailsResponseResponse);
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

    public LiveData<Response<MessageResponse>> postReview(ReviewRequest reviewRequest) {
        MutableLiveData<Response<MessageResponse>> data = new MutableLiveData<>();
        endPointsLazy.getValue().postReview(reviewRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<MessageResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<MessageResponse> dataResponseResponse) {
                        data.setValue(dataResponseResponse);
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
