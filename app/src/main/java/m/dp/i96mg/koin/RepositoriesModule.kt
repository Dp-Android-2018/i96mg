package m.dp.i96mg.koin

import android.os.Parcel
import m.dp.i96mg.service.model.request.OrderRequest
import m.dp.i96mg.service.repository.remotes.MainRepository
import m.dp.i96mg.service.repository.remotes.PayCardRepository
import org.koin.dsl.module.module

@JvmField
val DependencyModule = module {

    single { MainRepository() }
    single { PayCardRepository() }
    factory { OrderRequest(Parcel.obtain()) }

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