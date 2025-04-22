package com.example.jamboreesportsexperiences.odoo

data class OdooResponseSearch<T>(
    val result: List<T>? // El 'result' es la lista de datos que Odoo devuelve, por ejemplo, productos.
)
