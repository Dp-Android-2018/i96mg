package m.dp.i96mg.koin

import m.dp.i96mg.utility.utils.SharedPreferenceHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

@JvmField
val SharedPreferenceModule = module {
    single { SharedPreferenceHandler(androidContext()) }
}