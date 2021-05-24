package com.loftechs.sample.model.http

import com.loftechs.sample.model.http.entity.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface IService {
    @POST("/oauth2/getDeveloperToken")
    fun getAccessToken(@Body request: TokenRequest): Observable<TokenResponse>

    @POST("/oauth2/register")
    fun register(@Body request: RegisterRequest): Observable<RegisterResponse>

    @POST("/oauth2/authenticate")
    fun login(@Body request: LoginRequest): Observable<LoginResponse>
}