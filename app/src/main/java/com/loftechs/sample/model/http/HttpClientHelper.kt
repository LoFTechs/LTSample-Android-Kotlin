package com.loftechs.sample.model.http

import com.google.gson.*
import com.loftechs.sample.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient.Builder
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

abstract class HttpClientHelper {
    private var client: Builder = Builder()
    private var gsonFactory: GsonConverterFactory
    private var scalarsFactory: ScalarsConverterFactory
    private lateinit var mRetrofit: Retrofit

    private val logger by lazy {
        ApiLogger()
    }

    companion object {
        const val CONTENT_TYPE_JSON = "application/json"
    }

    init {
        gsonFactory = GsonConverterFactory.create(
            GsonBuilder()
                .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
                .create()
        )
        scalarsFactory = ScalarsConverterFactory.create()
    }

    @Throws(IOException::class)
    protected abstract fun setInterceptorAction(chain: Interceptor.Chain): Response

    protected abstract val baseUrl: String

    protected fun init() {
        restClient()
        resetApp()
    }

    private fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add("baby.juiker.net", "sha256/jR3zdhzwnG+GruQYsx51BYWBVVqcOipsvA7l9l8KpHA=")
            .build()
    }

    private fun restClient() {
        client.certificatePinner(createCertificatePinner())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(LTIntercept())

        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor(logger)
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            client.addInterceptor(httpLoggingInterceptor)
        }
    }

    private fun resetApp() {
        mRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client.build())
            .addConverterFactory(scalarsFactory)
            .addConverterFactory(gsonFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    val service: IService
        get() = mRetrofit.create(IService::class.java)

    internal inner class LTIntercept : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            return setInterceptorAction(chain)
        }
    }

    internal inner class BooleanTypeAdapter : JsonDeserializer<Boolean> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Boolean {
            return json.asInt != 0
        }
    }

    private inner class ApiLogger : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Timber.tag("ApiLogger").d(message)
        }
    }
}