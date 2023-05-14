package com.example.githubreposmvi.shared

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class RetrofitClient {

    companion object {

        fun <T> getInstance(service: Class<T>): T {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConstantLinks.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(service)
        }

    }

}