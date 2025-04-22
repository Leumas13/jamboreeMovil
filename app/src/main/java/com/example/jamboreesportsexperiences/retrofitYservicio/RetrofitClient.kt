package com.example.jamboreesportsexperiences.retrofitYservicio

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
     val retrofit = Retrofit.Builder()
        .baseUrl("http://52.47.154.185:8069/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val odooApi: OdooApiService = retrofit.create(OdooApiService::class.java)
}