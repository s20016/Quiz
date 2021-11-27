package jp.ac.it_college.std.s20016.quiz

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("exec?f=version")
    fun getVersion(): Call<ApiVersionItem>

    @GET("exec?f=data")
    fun getData(): Call<List<ApiDataItem>>
}