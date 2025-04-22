package com.example.jamboreesportsexperiences.retrofitYservicio

import com.example.jamboreesportsexperiences.odoo.OdooRequestGeneral
import com.example.jamboreesportsexperiences.odoo.OdooRequestLogin
import com.example.jamboreesportsexperiences.odoo.OdooResponseLogin
import com.example.jamboreesportsexperiences.odoo.OdooResponseSearch
import com.example.jamboreesportsexperiences.odoo.OdooResponseWrite
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OdooApiService {
    @POST("jsonrpc")
    fun login(@Body request: OdooRequestLogin): Call<OdooResponseLogin>

    @POST("/jsonrpc")
    fun recuperarInfo(@Body request: OdooRequestGeneral): Call<OdooResponseSearch<Map<String, Any>>>

    @POST("/jsonrpc")
    fun modificarDatos(@Body request: OdooRequestGeneral): Call<OdooResponseWrite>
}