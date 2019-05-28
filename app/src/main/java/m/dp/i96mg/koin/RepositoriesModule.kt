package m.dp.i96mg.koin

import android.os.Parcel
import m.dp.i96mg.service.repository.remotes.MainRepository
import org.koin.dsl.module.module

@JvmField
val DependencyModule = module {

    single { MainRepository() }

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