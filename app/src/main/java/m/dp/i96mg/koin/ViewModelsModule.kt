package m.dp.i96mg.koin

import m.dp.i96mg.viewmodel.MainActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

@JvmField
val ViewModelModule = module {

    viewModel { MainActivityViewModel(androidApplication()) }
}