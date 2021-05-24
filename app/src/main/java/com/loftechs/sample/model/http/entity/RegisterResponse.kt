package com.loftechs.sample.model.http.entity

data class RegisterResponse(var users: List<User>?) : Response() {

    data class User (
        var semiUID: String,
        var userID: String,
        var uuid: String,
        var err: String?
    )
}