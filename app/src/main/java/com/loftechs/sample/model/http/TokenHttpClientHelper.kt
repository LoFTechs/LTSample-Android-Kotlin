package com.loftechs.sample.model.http

import android.util.Base64
import com.loftechs.sample.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class TokenHttpClientHelper : HttpClientHelper() {

    companion object {
        private val TAG = TokenHttpClientHelper::class.java.simpleName
    }

    init {
        super.init()
    }

    @Throws(IOException::class)
    override fun setInterceptorAction(chain: Interceptor.Chain): Response {
        val authorization = createAuthorization()
        val original: Request = chain.request()

        Timber.tag(TAG).i("request url: ${original.url}")
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
        get() = BuildConfig.Auth_API

    private fun createAuthorization(): String {
        val plain = "${BuildConfig.Developer_Account}:${BuildConfig.Developer_Password}"
        val encoded = Base64.encodeToString(plain.toByteArray(), Base64.NO_WRAP)
        return "Basic $encoded"
    }
}