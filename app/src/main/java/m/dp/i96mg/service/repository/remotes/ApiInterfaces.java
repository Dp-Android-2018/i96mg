package m.dp.i96mg.service.repository.remotes;

import java.util.ArrayList;

import io.reactivex.Observable;
import m.dp.i96mg.service.model.global.ProductData;
import m.dp.i96mg.service.model.request.CheckRequest;
import m.dp.i96mg.service.model.request.OrderRequest;
import m.dp.i96mg.service.model.response.OrderResponse;
import m.dp.i96mg.service.model.response.ProductsResponse;
import m.dp.i96mg.service.model.response.VoucherResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterfaces {

    //Search for products
    @GET("/api/search")
    Observable<Response<ProductsResponse>> getProducts(@Query("category") String categoryId, @Query("page") int pageNumber);

    //Create an order
    @POST("/api/order")
    Observable<Response<OrderResponse>> createOrder(@Body OrderRequest orderRequest);

    //Get voucher data
    @GET("/api/voucher")
    Observable<Response<VoucherResponse>> getVoucherData(@Query("voucher") String voucher);

    //Check cart products quantities
    @POST("/api/order/check")
    Observable<Response<Void>> checkProducts(@Body CheckRequest checkRequest );


}
