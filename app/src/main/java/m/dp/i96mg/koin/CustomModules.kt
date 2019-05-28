package m.dp.i96mg.koin

import m.dp.i96mg.utility.utils.ConnectionReceiver
import m.dp.i96mg.utility.utils.CustomUtils
import org.koin.dsl.module.module

@JvmField
val CustomModules= module {

    single { ConnectionReceiver() }
    single { CustomUtils() }
}