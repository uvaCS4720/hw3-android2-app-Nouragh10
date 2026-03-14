package edu.nd.pmcburne.hwapp.one.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://ncaa-api.henrygd.me/"
    
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: BasketballApiService = retrofit.create(BasketballApiService::class.java)
}
