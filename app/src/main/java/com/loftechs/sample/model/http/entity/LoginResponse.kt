package com.loftechs.sample.model.http.entity

data class LoginResponse(
        var semiUID: String,
        var userID: String,
        var uuid: String
) : Response()