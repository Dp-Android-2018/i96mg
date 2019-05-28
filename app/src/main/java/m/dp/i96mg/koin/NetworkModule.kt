package m.dp.i96mg.koin

import m.dp.i96mg.service.repository.remotes.ApiInterfaces
import m.dp.i96mg.utility.utils.ConfigurationFile
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@JvmField
val NetworkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { getRerofitEndPoint(get()) }
}


internal fun provideOkHttpClient(): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()
    okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
    okHttpClient.addInterceptor {
        val original = it.request()

        // Request customization: add request headers
        val requestBuilder = original.newBuilder()
        requestBuilder.header("Content-Type", ConfigurationFile.Constants.CONTENT_TYPE)
        requestBuilder.header("Accept", ConfigurationFile.Constants.ACCEPT)
        requestBuilder.header("x-api-key", ConfigurationFile.Constants.API_KEY)
        requestBuilder.header("Accept-Language", ConfigurationFile.Constants.ACCEPT_LANGUAGE)
        requestBuilder.header("Authorization", ConfigurationFile.Constants.BEARER_STRING + ConfigurationFile.Constants.AUTHORIZATION)
        //requestBuilder.head("", );
        // <-- this is the important line

        val request = requestBuilder.build()
        val response = it.proceed(request)
        if (response.code() == 404 || response.code() == 401) {
            EventBus.getDefault().post(response.message())
        }
        response
    }

    return okHttpClient.build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
            .baseUrl(ConfigurationFile.UrlConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
}

internal fun getRerofitEndPoint(retrofit: Retrofit): ApiInterfaces {
    return retrofit.create(ApiInterfaces::class.java)
}