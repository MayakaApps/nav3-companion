package com.mayakapps.nav3companion.sample

import android.app.Application
import com.mayakapps.nav3sample.di.initKoin
import org.koin.android.ext.koin.androidContext

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@SampleApplication)
        }
    }
}
