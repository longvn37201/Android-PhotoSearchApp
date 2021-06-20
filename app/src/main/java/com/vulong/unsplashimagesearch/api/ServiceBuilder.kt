package com.vulong.unsplashimagesearch.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    const val BASE_URL = "https://api.unsplash.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}
