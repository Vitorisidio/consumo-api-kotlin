package com.example.consumoapi.model

import com.google.gson.annotations.SerializedName

data class Endereco (
    val cep: String = "",
    @SerializedName(value = "logradouro") val rua: String = "",
    @SerializedName(value = "localidade") val cidade: String = "",
    val bairro: String = "",
    val uf: String = "",
)