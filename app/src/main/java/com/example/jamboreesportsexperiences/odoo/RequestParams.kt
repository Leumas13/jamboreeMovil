package com.example.jamboreesportsexperiences.odoo

data class RequestParams(
    val service: String,
    val method: String, // El método que queremos ejecutar, por ejemplo, "search_read"
    val args: List<Any>, // Argumentos que el método requiere (filtro y campos)
    val kwargs: Map<String, Any> // Parámetros adicionales como los campos que queremos obtener
)

