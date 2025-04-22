package com.example.jamboreesportsexperiences.odoo

data class OdooRequestLogin(
    val jsonrpc: String = "2.0",
    val method: String = "call",
    val params: Map<String, Any>
) {
    companion object {
        fun login(db: String, username: String, password: String): OdooRequestLogin {
            return OdooRequestLogin(
                params = mapOf(
                    "service" to "common",
                    "method" to "authenticate",
                    "args" to listOf(db, username, password, emptyMap<String, Any>())
                )
            )
        }
    }
}
