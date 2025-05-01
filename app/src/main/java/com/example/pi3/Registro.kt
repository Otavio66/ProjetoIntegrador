package com.example.pi3

// Modelo de Dados para Registro
data class Registro(
    val nomeProblema: String,
    val descricao: String,
    val fotoUrl: String?,
    val localizacao: String?,
    val categoria: String?,
    val status: String?,
    val classificacao: Float
)

