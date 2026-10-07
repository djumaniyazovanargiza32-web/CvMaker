package com.nargiza.cvmaker

import android.app.Application
import com.nargiza.cvmaker.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CvMakerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CvMakerApp)
            modules(appModules)
        }
    }
}
