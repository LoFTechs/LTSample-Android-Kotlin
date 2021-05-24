package com.loftechs.sample

import android.app.Application
import android.content.Context
import com.loftechs.sample.extensions.logError
import com.loftechs.sdk.utils.LTLog
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import timber.log.Timber.DebugTree

class SampleApp : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        initTimber()
        initRxErrorHandler()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initRxErrorHandler() {
        RxJavaPlugins.setErrorHandler { throwable ->
            logError("RxErrorHandler", throwable)
            throwable.printStackTrace()
        }
    }
}