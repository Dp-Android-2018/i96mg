package m.dp.i96mg.service.repository.remotes;

import io.reactivex.Observable;
import m.dp.i96mg.service.model.request.BankRequest;
import m.dp.i96mg.service.model.request.CartRequest;
import m.dp.i96mg.service.model.request.CheckRequest;
import m.dp.i96mg.service.model.request.LoginRequest;
import m.dp.i96mg.service.model.request.ProductsOrderRequest;
import m.dp.i96mg.service.model.request.ReviewRequest;
import m.dp.i96mg.service.model.request.SignUpRequest;
import m.dp.i96mg.service.model.request.WishListRequest;
import m.dp.i96mg.service.model.response.AllOrdersResponse;
import m.dp.i96mg.service.model.response.BankAccountsResponse;
import m.dp.i96mg.service.model.response.CartResponse;
import m.dp.i96mg.service.model.response.CategoriesResponse;
import m.dp.i96mg.service.model.response.DataResponse;
import m.dp.i96mg.service.model.response.LoginResponse;
import m.dp.i96mg.service.model.response.MessageResponse;
import m.dp.i96mg.service.model.response.OrderResponse;
import m.dp.i96mg.service.model.response.ProductDetailsResponse;
import m.dp.i96mg.service.model.response.ProductReviewsResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterfaces {

    //search for products
    @GET("/api/search")
    Observable<Response<ProductsResponse>> getProducts(@Query("category") String categoryId, @Query("page") int pageNumber);

    //Products on sale
    @GET("/api/search/sale")
    Observable<Response<ProductsResponse>> getSaleProducts();

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
    Observable<Response<MessageResponse>> createOrder(@Body ProductsOrderRequest productsOrderRequest);

    //Get voucher data
    @GET("/api/voucher")
    Observable<Response<DataResponse>> getVoucherData(@Query("voucher") String voucher);

    //Check cart products quantities
    @POST("/api/order/check")
    Observable<Response<Void>> checkProducts(@Body CheckRequest checkRequest);

    //Send login code
    @POST("/api/send-login-code")
    Observable<Response<MessageResponse>> sendLoginCode(@Body LoginRequest loginRequest);

    //Login
    @POST("/api/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequest loginRequest);

    //Update account details (complete registration)
    @Multipart
    @POST("/api/account")
    Observable<Response<MessageResponse>> signUp(@Part("phone") RequestBody phone
            , @Part("first_name") RequestBody firstName, @Part("last_name") RequestBody lastName
            , @Part MultipartBody.Part imageFile);

    //Get settings
    @GET("/api/settings")
    Observable<Response<CategoriesResponse>> getSettings();

    //Add items to the cart
    @POST("/api/cart")
    Observable<Response<MessageResponse>> addItemsToCart(@Body CartRequest cartRequest);

    //Add items to the wishlist
    @POST("/api/wishlist")
    Observable<Response<MessageResponse>> addItemsToWishList(@Body WishListRequest wishListRequest);

    //Get cart items
    @GET("/api/cart")
    Observable<Response<CartResponse>> getCartItems();

    //Get wishlist items
    @GET("/api/wishlist")
    Observable<Response<ProductsResponse>> getWishListItems();

    //Remove an item from the cart
    @DELETE("/api/cart/{id}")
    Observable<Response<MessageResponse>> removeItemFromCart(@Path("id") int productId);

    //Remove an item from the wishlist
    @DELETE("/api/wishlist/{id}")
    Observable<Response<MessageResponse>> removeItemFromWishlist(@Path("id") int productId);

    //Get all pending orders
    @GET("/api/order/pending")
    Observable<Response<AllOrdersResponse>> getPendingOrders(@Query("page") int pageNumber);

    //Get all orders
    @GET("/api/order")
    Observable<Response<AllOrdersResponse>> getOrders(@Query("page") int pageNumber);

    //Complete the payment for an order using paypal
    @POST("/api/order/{order}/pay/paypal")
    Observable<Response<OrderResponse>> payUsingPaybal(@Path("order") int orderId);

    //Get bank accounts
    @GET("/api/bank-accounts")
    Observable<Response<BankAccountsResponse>> getBankAccounts();

    //Complete the payment for an order using a bank transfer
    @Multipart
    @POST("/api/order/{order}/pay/bank-transfer")
    Observable<Response<MessageResponse>> payUsingBankAccount(
            @Path("order") int orderId,@Part("bank_account_id") RequestBody bankAccountId
            , @Part("full_name") RequestBody fullName, @Part MultipartBody.Part imageFile);
}
