package com.vulong.unsplashimagesearch.data

data class ResponseObject(
    val total:Int,
    val results: ArrayList<Photo>
)