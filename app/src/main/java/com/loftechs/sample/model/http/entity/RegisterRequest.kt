package com.loftechs.sample.model.http.entity

data class RegisterRequest (
    var turnkey: String,
    var users: List<User>
) {

    var verifyMode = "turnkey"

    data class User(
            var semiUID: String,
            var semiUID_PW: String
    )
}