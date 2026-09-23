package city.explorer

import android.app.Application
import city.explorer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CityExplorerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CityExplorerApplication)
            modules(appModule)
        }
    }
}
