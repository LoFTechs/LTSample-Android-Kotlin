package com.loftechs.sample

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.loftechs.sample.extensions.logError
import com.loftechs.sdk.LTSDK
import com.loftechs.sdk.nativetools.NativeUtils
import com.loftechs.sdk.utils.RootDetect
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
        initSDK()
        initRxErrorHandler()
        if (RootDetect(context).isRooted) {
            //toast once and stored in shared preference
            Toast.makeText(this, "Your phone is not safe! Rooted or debug mode.", Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(this, "Your phone is safe.", Toast.LENGTH_SHORT).show()
        }
        if (!NativeUtils.verifyApkSignHash1(context)) {
            //toast warning
            Toast.makeText(this, "Your App is not authorized to use LTSDK !", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initSDK() {
        LTSDK.initContext(context)
    }

    private fun initRxErrorHandler() {
        RxJavaPlugins.setErrorHandler { throwable ->
            logError("RxErrorHandler", throwable)
            throwable.printStackTrace()
        }
    }
}