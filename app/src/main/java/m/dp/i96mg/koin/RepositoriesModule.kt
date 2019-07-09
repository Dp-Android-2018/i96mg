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

    /* single { Register1Repository() }
     single { Register2Repository() }
     factory { RegisterRequest(Parcel.obtain()) }

     single { LoginRepository() }
     factory { LoginRequest() }

     single { ActivationRepository() }
     factory { ActivationRequest() }

     single { MainRepository() }
     factory { SearchRequestsRequest() }

     single { RequestDetailsRepository() }
     single { AccountRepository() }
     single { OffersRepository() }
     single { TripsRepository() }
     single { StartTripRepository() }
     single { FinancialRepository() }
     single { ChatRepository() }*/

}