package com.example.jamboreesportsexperiences.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.jamboreesportsexperiences.R
import com.example.jamboreesportsexperiences.odoo.OdooRequestGeneral
import com.example.jamboreesportsexperiences.odoo.OdooResponseSearch
import com.example.jamboreesportsexperiences.odoo.RequestParams
import com.example.jamboreesportsexperiences.retrofitYservicio.OdooApiService
import com.example.jamboreesportsexperiences.retrofitYservicio.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatosTutor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_tutor)

        //Recuperar mail del tutor e id del administrador
        val emailTutor = intent.getStringExtra("email")
        val idAdmin = intent.getIntExtra("idAdmin", 0)

        //Recuperar todos los campos donde se van a almacenar los datos del tutor
        val nombre = findViewById<EditText>(R.id.nombre)
        val apellidos = findViewById<EditText>(R.id.apellidos)
        val email = findViewById<EditText>(R.id.email)
        val telefono = findViewById<EditText>(R.id.numTel)
        val direccion = findViewById<EditText>(R.id.direccion)
        val ciudad = findViewById<EditText>(R.id.ciudad)

        //Campos auxiliares
        var nomAux:String? = null
        var apesAux:String? = null
        var telAux:String? = null
        var direcAux:String? = null
        var ciuAux:String? = null

        //Cargar datos
        if(idAdmin!=null && idAdmin!=0){
            recuperarTutor(idAdmin,emailTutor,nombre,apellidos,email,telefono,direccion,ciudad,
                           nomAux,apesAux,telAux, direcAux,ciuAux)
        }else{
            var toast = Toast.makeText(this, "No se han podido cargar los datos. " +
                    "Por favor, vuelva a intentarlo una vez se hayan cargado los datos en el calendario.", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
            toast.show()
        }

        //Recuperar botón para salir y función que realiza la acción
        val btSal = findViewById<ImageButton>(R.id.btSalir)

        btSal.setOnClickListener {
            val intent = Intent(this, CalendarioEntrenamientos::class.java)
            intent.putExtra("email", emailTutor)
            startActivity(intent)
        }

        //-----Botones para modificar, cancelar la modificación y guardar los datos-----
        val btMod = findViewById<ImageButton>(R.id.btMod)
        val btCan = findViewById<Button>(R.id.btCanc)
        val btGuardar = findViewById<Button>(R.id.btGuardar)

        //Empezar a modificar
        btMod.setOnClickListener {
            //Botones
            btMod.isEnabled = false
            btSal.isEnabled = false
            btCan.isEnabled = true
            btGuardar.isEnabled = true

            //Campos
            nombre.isEnabled = true
            apellidos.isEnabled = true
            telefono.isEnabled = true
            direccion.isEnabled = true
            ciudad.isEnabled = true
        }

        //Cancelar la modificación
        btCan.setOnClickListener {
            //Botones
            btMod.isEnabled = true
            btSal.isEnabled = true
            btCan.isEnabled = false
            btGuardar.isEnabled = false

            //Reiniciar valores
            nombre.setText(nomAux)
            apellidos.setText(apesAux)
            telefono.setText(telAux)
            direccion.setText(direcAux)
            ciudad.setText(ciuAux)

            //Campos
            nombre.isEnabled = false
            apellidos.isEnabled = false
            telefono.isEnabled = false
            direccion.isEnabled = false
            ciudad.isEnabled = false
        }

    }

    private fun recuperarTutor(sessionId: Int, email: String?, nombre:EditText, apellidos:EditText,
                               emailText:EditText, telefono:EditText, direcc:EditText, ciudad:EditText,
                               nomAux:String?,apesAux:String?,telAux:String?,direcAux:String?,ciuAux:String?) {

        val apiService = RetrofitClient.retrofit.create(OdooApiService::class.java)

        val request = OdooRequestGeneral(
            params = RequestParams(
                service = "object",
                method = "execute_kw",  //metodo general
                args = listOf(
                    "admin",  // Nombre de la base de datos
                    sessionId,                // Id del usuario
                    "clave$1",        // Contraseña del usuario
                    "res.partner",  // Modelo a utilizar
                    "search_read",         // Método a realizar en el modelo
                    listOf(
                        // Filtrar por email
                        listOf(
                            listOf("email", "=", email)
                        )
                    ),
                ),
                kwargs = emptyMap()  // Sin parámetros adicionales
            )
        )

        apiService.recuperarInfo(request).enqueue(object :
            Callback<OdooResponseSearch<Map<String, Any>>> {
            override fun onResponse(
                call: Call<OdooResponseSearch<Map<String, Any>>>,
                response: Response<OdooResponseSearch<Map<String, Any>>>
            ) {

                val resultado = response.body()?.result

                //Nombre y apellidos
                val nombreCompleto = resultado?.get(0)?.get("complete_name") as String
                val nombreYape = nombreCompleto.split("  ")
                nombre.setText(nombreYape[0])
                apellidos.setText(nombreYape[1])

                //Email
                emailText.setText(resultado?.get(0)?.get("email") as String)

                //Teléfono
                telefono.setText(resultado?.get(0)?.get("phone") as String)

                //Direccion
                val dir = resultado?.get(0)?.get("street")

                if(dir!=false){
                    direcc.setText(dir.toString())
                }

                //Ciudad
                val ciu = resultado?.get(0)?.get("city")

                if(ciu!=false){
                    ciudad.setText(ciu.toString())
                }

                //Auxiliares
               /* nomAux = nombre.text.toString()
                apesAux = apellidos.text.toString()
                telAux = telefono.text.toString()
                direcAux = direcc.text.toString()
                ciuAux = ciudad.text.toString()*/
            }

            override fun onFailure(call: Call<OdooResponseSearch<Map<String, Any>>>, t: Throwable) {
                recuperarTutor(sessionId, email, nombre, apellidos, emailText, telefono, direcc, ciudad)
            }
        })
    }
}