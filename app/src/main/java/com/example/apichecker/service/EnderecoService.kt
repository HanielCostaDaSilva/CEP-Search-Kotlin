package com.example.apichecker.service

import com.example.apichecker.model.Address
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoService {

    @GET("{cep}/json/")
    suspend fun findByCep(@Path("cep") cep: String): Address

}
