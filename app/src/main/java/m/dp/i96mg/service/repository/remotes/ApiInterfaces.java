package m.dp.i96mg.service.repository.remotes;

import io.reactivex.Observable;
import m.dp.i96mg.service.model.request.CheckRequest;
import m.dp.i96mg.service.model.request.LoginRequest;
import m.dp.i96mg.service.model.request.OrderRequest;
import m.dp.i96mg.service.model.request.ReviewRequest;
import m.dp.i96mg.service.model.request.SignUpRequest;
import m.dp.i96mg.service.model.response.CategoriesResponse;
import m.dp.i96mg.service.model.response.LoginResponse;
import m.dp.i96mg.service.model.response.OrderResponse;
import m.dp.i96mg.service.model.response.ProductDetailsResponse;
import m.dp.i96mg.service.model.response.ProductReviewsResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.service.model.response.DataResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterfaces {

    //search for products
    @GET("/api/search")
    Observable<Response<ProductsResponse>> getProducts(@Query("category") String categoryId, @Query("page") int pageNumber);

    //Products on sale
    @GET("/api/search/sale")
    Observable<Response<ProductsResponse>> getSaleProducts();

    //Get wishlist items
    @GET("/api/wishlist")
    Observable<Response<ProductsResponse>> getWishListItems();

    //Get a product by its id
    @GET("/api/product/{id}")
    Observable<Response<ProductDetailsResponse>> getProductById(@Path("id") int productId);

    //Get product reviews for a specific product
    @GET("/api/product/{id}/reviews")
    Observable<Response<ProductReviewsResponse>> getProductReviews(@Path("id") int productId);

    //Post a review for a specific product
    @POST("/api/product/review")
    Observable<Response<MessageResponse>> postReview(@Body ReviewRequest reviewRequest);

    //Create an order
    @POST("/api/order")
    Observable<Response<OrderResponse>> createOrder(@Body OrderRequest orderRequest);

    //Get voucher data
    @GET("/api/voucher")
    Observable<Response<DataResponse>> getVoucherData(@Query("voucher") String voucher);

    //Check cart products quantities
    @POST("/api/order/check")
    Observable<Response<Void>> checkProducts(@Body CheckRequest checkRequest );

    //Send login code
    @POST("/api/send-login-code")
    Observable<Response<MessageResponse>> sendLoginCode(@Body LoginRequest loginRequest );

    //Login
    @POST("/api/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequest loginRequest );

    //Update account details (complete registration)
    @POST("/api/account")
    Observable<Response<MessageResponse>> signUp(@Body SignUpRequest signUpRequest );

    //Get settings
    @GET("/api/settings")
    Observable<Response<CategoriesResponse>> getSettings();

}
