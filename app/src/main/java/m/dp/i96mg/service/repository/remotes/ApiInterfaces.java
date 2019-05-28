package m.dp.i96mg.service.repository.remotes;

import io.reactivex.Observable;
import m.dp.i96mg.service.model.response.ProductsResponse;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterfaces {

    //Search for products
    @GET("/api/search")
    Observable<Response<ProductsResponse>> getProducts(@Query("category") String categoryId, @Query("page") int pageNumber);

}
