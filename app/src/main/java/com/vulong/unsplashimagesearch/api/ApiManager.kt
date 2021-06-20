package com.vulong.unsplashimagesearch.api

import com.vulong.unsplashimagesearch.data.ResponseObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiManager {

    companion object {
        const val API_KEY = "O_U0fnXKFyv979lGABh_BTLPrUFnN0TiyH7uaa8gcC0"
    }

    @Headers("Authorization: Client-ID $API_KEY")
    @GET("search/photos")
    fun getPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") parPage: Int = 5,
    ): Call<ResponseObject>


}
