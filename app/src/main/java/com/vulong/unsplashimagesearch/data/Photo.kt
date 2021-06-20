package com.vulong.unsplashimagesearch.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: String,
    @SerializedName("created_at") val createdAt: String,
    val description: String?,
    val user: User,
    val urls: Urls,

    ) : Parcelable {

    @Parcelize
    data class User(
        val username: String,
        val name: String,
    ) : Parcelable

    @Parcelize
    data class Urls(
        val full: String,
        val small: String,
        val thumb: String,
    ) : Parcelable

}