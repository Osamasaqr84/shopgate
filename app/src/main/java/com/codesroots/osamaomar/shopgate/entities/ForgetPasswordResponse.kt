package com.codesroots.osamaomar.shopgate.entities


data class ForgetPasswordResponse(
    val `data`: Data
)

data class Data(
    val `data`: String,
    val state: String
)