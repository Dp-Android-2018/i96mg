package m.dp.i96mg.koin

import m.dp.i96mg.viewmodel.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

@JvmField
val ViewModelModule = module {

    viewModel { MainActivityViewModel(androidApplication()) }
    viewModel { PayCardActivityViewModel(androidApplication()) }
    viewModel { ShopDetailsActivityViewModel(androidApplication()) }
    viewModel { ProductDetailsViewModel(androidApplication()) }
    viewModel { LoginViewModel(androidApplication()) }
    viewModel { SignUpViewModel(androidApplication()) }
    viewModel { WishListViewModel(androidApplication()) }
    viewModel { OrdersViewModel(androidApplication()) }
}