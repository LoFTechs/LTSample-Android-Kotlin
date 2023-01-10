package com.loftechs.sample.model.api

import android.util.Base64
import com.google.gson.Gson
import com.loftechs.sample.BuildConfig
import com.loftechs.sample.model.PreferenceSetting
import com.loftechs.sample.model.http.AuthHttpClientHelper
import com.loftechs.sample.model.http.TokenHttpClientHelper
import com.loftechs.sample.model.http.entity.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.*
import java.util.concurrent.TimeUnit

object AccountManager {
    private val tokenHttpClientHelper: TokenHttpClientHelper by lazy {
        TokenHttpClientHelper()
    }

    private val authHttpClientHelper: AuthHttpClientHelper by lazy {
        AuthHttpClientHelper()
    }

    private val token: Observable<String>
        get() {
            val token: String = PreferenceSetting.token
            return if (token.isNotEmpty()) {
                getTokenTime(token)
                        .flatMap { time: Long ->
                            if (time == 0L || System.currentTimeMillis() > time - TimeUnit.MINUTES.toMillis(10)) {
                                return@flatMap tokenByServer
                            }
                            Observable.just(token)
                        }
            } else tokenByServer
        }

    private val tokenByServer: Observable<String>
        get() = tokenHttpClientHelper.service.getAccessToken(TokenRequest())
                .map { tokenResponse ->
                    PreferenceSetting.token = tokenResponse.accessToken
                    tokenResponse.accessToken
                }

    private fun getTokenTime(token: String?): Observable<Long> {
        return Observable.create { emitter: ObservableEmitter<Long> ->
            var exp = 0L
            token?.split("\\.".toRegex())?.toTypedArray()?.let {
                if (it.size > 2) {
                    val base64EncodedBody = it[1].replace("-", "+").replace("_", "/")
                    val tokenBody = String(Base64.decode(base64EncodedBody, Base64.NO_WRAP), Charsets.UTF_8)
                    val jwtObject = Gson().fromJson(tokenBody, JWTObject::class.java)
                    exp = jwtObject.exp * 1000
                }
            }

            emitter.onNext(exp)
            emitter.onComplete()
        }
    }

    data class JWTObject(
            val id: String,
            val jti: String,
            val iss: String,
            val aud: String,
            val sub: String,
            val exp: Long,
            val iat: Long,
            val token_type: String,
            val scope: String,
            val deviceID: String
    )

    fun register(account: String, pwd: String): Observable<RegisterResponse> {
        return token
                .flatMap {
                    val user = RegisterRequest.User(account, pwd)
                    val registerRequest = RegisterRequest(BuildConfig.LTSDK_TurnKey, Collections.singletonList(user))
                    authHttpClientHelper.service.register(registerRequest)
                }
    }

    fun login(account: String, pwd: String): Observable<LoginResponse> {
        return token
                .flatMap {
                    val loginRequest = LoginRequest(account, pwd)
                    authHttpClientHelper.service.login(loginRequest)
                }
    }
}