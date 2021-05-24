package com.loftechs.sample.model.http

import com.loftechs.sample.BuildConfig
import com.loftechs.sample.model.PreferenceSetting
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class AuthHttpClientHelper : HttpClientHelper() {

    companion object {
        private val TAG = AuthHttpClientHelper::class.java.simpleName
        const val CONTENT_TYPE_JSON = "application/json"
    }

    init {
        init()
    }

    @Throws(IOException::class)
    override fun setInterceptorAction(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        Timber.tag(TAG).i("request url: ${original.url}")
        val authorization = "Bearer " + PreferenceSetting.token
        original.headers["Command"]
        val request = original.newBuilder()
                .addHeader("User-Agent", "android")
                .addHeader("Authorization", authorization)
                .addHeader("Brand-Id", BuildConfig.Brand_ID)
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .method(original.method, original.body)
                .build()

        //nonce check
        val response: Response = chain.proceed(request)
        Timber.tag(TAG).i("response: ${response.code}")
        return response
    }

    override val baseUrl: String
        get() = BuildConfig.LTSDK_API
}