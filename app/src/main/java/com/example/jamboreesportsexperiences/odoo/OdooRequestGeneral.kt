package com.example.jamboreesportsexperiences.odoo

data class OdooRequestGeneral(
    val jsonrpc: String = "2.0",
    val method: String = "call",
    val params: RequestParams
)
