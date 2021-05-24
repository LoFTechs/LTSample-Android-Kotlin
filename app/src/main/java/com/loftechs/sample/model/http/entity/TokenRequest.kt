package com.loftechs.sample.model.http.entity

data class TokenRequest (
    var scope: String = "tw:api:sdk"
)