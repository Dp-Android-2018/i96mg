package m.dp.i96mg.koin

import android.os.Parcel
import m.dp.i96mg.service.model.request.OrderRequest
import m.dp.i96mg.service.repository.remotes.*
import org.koin.dsl.module.module

@JvmField
val DependencyModule = module {

    single { MainRepository() }
    single { PayCardRepository() }
    factory { OrderRequest(Parcel.obtain()) }
    single { ShopDetailsRepository() }
    single { ProductDetailsRepository() }
    single { LoginRepository() }
    single { SignUpRepository() }
    single { WishListRepository() }
    single { OrdersRepository() }

}