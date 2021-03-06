package m.dp.i96mg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import kotlin.Lazy;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.ReviewRequest;
import m.dp.i96mg.service.model.request.WishListRequest;
import m.dp.i96mg.service.model.response.ProductDetailsResponse;
import m.dp.i96mg.service.model.response.ProductReviewsResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.repository.remotes.ProductDetailsRepository;
import retrofit2.Response;

import static org.koin.java.standalone.KoinJavaComponent.inject;

public class ProductDetailsViewModel extends AndroidViewModel {

    private Lazy<ProductDetailsRepository> productDetailsRepositoryLazy = inject(ProductDetailsRepository.class);


    public ProductDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Response<ProductDetailsResponse>> getProductById(int productId) {
        return productDetailsRepositoryLazy.getValue().getProductById(productId);
    }

    public LiveData<Response<ProductReviewsResponse>> getProductReviews(int productId) {
        return productDetailsRepositoryLazy.getValue().getProductReviews(productId);
    }

    public LiveData<Response<MessageResponse>> postReview(ReviewRequest reviewRequest) {
        return productDetailsRepositoryLazy.getValue().postReview(reviewRequest);
    }

    public LiveData<Response<MessageResponse>> addItemsToCart(CartRequest cartRequest) {
        return productDetailsRepositoryLazy.getValue().addItemsToCart(cartRequest);
    }

    public LiveData<Response<MessageResponse>> removeItemFromCart(int productId) {
        return productDetailsRepositoryLazy.getValue().removeItemFromCart(productId);
    }

    public LiveData<Response<MessageResponse>> addItemsToWishList(WishListRequest wishListRequest) {
        return productDetailsRepositoryLazy.getValue().addItemsToWishList(wishListRequest);
    }

    public LiveData<Response<MessageResponse>> removeItemFromWishlist(int productId) {
        return productDetailsRepositoryLazy.getValue().removeItemFromWishlist(productId);
    }

}