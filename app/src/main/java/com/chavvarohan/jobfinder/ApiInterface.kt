package com.chavvarohan.jobfinder

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("AKfycbxZUPM34-uDQt2r3wFbkRGSlwb7cF_am2pD-JnXiSLNNj-gVw3NeiYmKtA7mrEulyNBOw/exec")
    fun getJobs(): Call<List<JobApiItem>>
}