package edu.nd.pmcburne.hwapp.one

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Requester {
    val api: APIInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https://ncaa-api.henrygd.me/scoreboard/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIInterface::class.java)
    }
}