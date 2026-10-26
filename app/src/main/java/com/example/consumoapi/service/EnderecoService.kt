package com.example.consumoapi.service

import com.example.consumoapi.model.Endereco
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoService {

    @GET("https://viacep.com.br/ws/{cep}/json/")
    fun getEnderecoByCep(@Path("cep") cep: String): Call<Endereco>

    @GET(value = "https://viacep.com.br/ws/{uf}/{cidade}/{rua}/json/")
    fun getEnderecosByUfCidadeRua(
        @Path(value = "uf") uf: String,
        @Path(value = "cidade") cidade: String,
        @Path(value = "rua") rua: String
    ): Call<List<Endereco>>


}