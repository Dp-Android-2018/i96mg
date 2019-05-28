package m.dp.i96mg.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import m.dp.i96mg.koin.*
import m.dp.i96mg.utility.utils.ConnectionReceiver
import m.dp.i96mg.utility.utils.ConnectionReceiver.connectionReceiverListener
import org.koin.android.ext.android.startKoin

class MyApplication : Application() {

    val CHANNEL_ID = "exampleChannel"

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin(this, listOf(DependencyModule,
                NetworkModule, ViewModelModule, CustomModules, SharedPreferenceModule))
        createNotificationChannel()
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    fun setConnectionListener(listener: ConnectionReceiver.ConnectionReceiverListener) {
        connectionReceiverListener = listener
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Example Channel",
                    NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}