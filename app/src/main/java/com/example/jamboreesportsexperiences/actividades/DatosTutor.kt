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
import com.example.jamboreesportsexperiences.odoo.OdooResponseWrite
import com.example.jamboreesportsexperiences.odoo.RequestParams
import com.example.jamboreesportsexperiences.retrofitYservicio.OdooApiService
import com.example.jamboreesportsexperiences.retrofitYservicio.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatosTutor : AppCompatActivity() {
    //Campos auxiliares
    private var nomAux = ""
    private var telAux = ""
    private var codPosAux = ""
    private var ciuAux: Int = 0
    private var provAux: Int = 0
    private var paAux: Int = 0
    private var idTutor:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_tutor)

        //Recuperar mail del tutor e id del administrador
        val emailTutor = intent.getStringExtra("email")
        val idAdmin = intent.getIntExtra("idAdmin", 0)

        //Recuperar todos los campos donde se van a almacenar los datos del tutor
        val nombre = findViewById<EditText>(R.id.nombre)
        val email = findViewById<EditText>(R.id.email)
        val telefono = findViewById<EditText>(R.id.numTel)

        //Campos a utilizar en diferentes funciones privadas
        val codPos = findViewById<EditText>(R.id.codPos)
        val pais = findViewById<EditText>(R.id.pais)
        val provincia = findViewById<EditText>(R.id.provincia)
        val ciudad = findViewById<EditText>(R.id.ciudad)

        //Cargar datos
        if (idAdmin != null && idAdmin != 0) {
            recuperarTutor(idAdmin, emailTutor, nombre, email, telefono,ciudad,provincia,pais)
        } else {
            var toast = Toast.makeText(
                this,
                "No se han podido cargar los datos. " +
                     "Por favor, vuelva a intentarlo una vez se hayan cargado los datos en el calendario.",
                Toast.LENGTH_LONG
            )
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
            telefono.isEnabled = true
            codPos.isEnabled = true
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
            telefono.setText(telAux)
            codPos.setText(codPosAux)

            //Campos
            nombre.isEnabled = false
            telefono.isEnabled = false
            codPos.isEnabled = false
        }

        //Guardar los Datos
        btGuardar.setOnClickListener {
            if(nombre.text.toString().isNullOrBlank() || telefono.text.toString().isNullOrBlank() || codPos.text.toString().isNullOrBlank()){
                var toast = Toast.makeText(this, "No puede dejar ningún campo vacío.", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
                toast.show()
            }else{
                //Botones
                btMod.isEnabled = true
                btSal.isEnabled = true
                btCan.isEnabled = false
                btGuardar.isEnabled = false

                //Campos
                nombre.isEnabled = false
                telefono.isEnabled = false
                codPos.isEnabled = false

                //Modificar
                recuperarCodPost(idAdmin,"name",codPos.text.toString(),true)
                modGeneral(idAdmin, nombre.text.toString(), telefono.text.toString())
                nomAux = nombre.text.toString()
                telAux = telefono.text.toString()
            }
        }
    }

    private fun recuperarTutor(sessionId: Int, email: String?, nombre: EditText,
                               emailText: EditText, telefono: EditText,
                               ciudad:EditText,provincia:EditText,pais:EditText) {
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

                //Id
                val id = resultado?.get(0)?.get("id") as Double
                idTutor = id.toInt()

                //Nombre y apellidos
                val nombreCompleto = resultado?.get(0)?.get("complete_name") as String
                nombre.setText(nombreCompleto)

                //Email
                emailText.setText(resultado?.get(0)?.get("email") as String)

                //Teléfono
                val tel = resultado?.get(0)?.get("phone")
                if (tel != false) {
                    telefono.setText(tel as String)
                }

                //Ciudad y Código postal
                val ciu = resultado?.get(0)?.get("city_id")

                if (ciu is ArrayList<*>) {
                    val ciu2 = ciu as ArrayList<Any>
                    val codZip = resultado?.get(0)?.get("zip_id") as ArrayList<Any>

                    val codCiu = ciu2[0] as Double
                    ciuAux = codCiu.toInt()

                    val zipId = codZip[0] as Double
                    val zipInt = zipId.toInt()
                    recuperarCodPost(sessionId, "id", zipInt,false)
                    ciudad.setText(ciu[1] as String)
                }

                //Provincia
                val prov = resultado?.get(0)?.get("state_id")
                if (prov is ArrayList<*>) {
                    val prov2 = prov as ArrayList<Any>
                    val codP = prov2[0] as Double
                    provAux = codP.toInt()
                    provincia.setText(prov2[1] as String)
                }

                //País
                val paisA = resultado?.get(0)?.get("country_id")
                if (paisA is ArrayList<*>) {
                    val paisA2 = paisA as ArrayList<Any>
                    val codPr = paisA2[0] as Double
                    paAux = codPr.toInt()
                    pais.setText(paisA2[1] as String)
                }

                //Auxiliares
                nomAux = nombre.text.toString()
                telAux = telefono.text.toString()

            }

            override fun onFailure(call: Call<OdooResponseSearch<Map<String, Any>>>, t: Throwable) {
                recuperarTutor(sessionId, email, nombre, emailText,telefono, ciudad, provincia, pais)
            }
        })
    }

    private fun recuperarCodPost(sessionId: Int, campo:String,cod: Any, modificar: Boolean) {
        val apiService = RetrofitClient.retrofit.create(OdooApiService::class.java)

        val request = OdooRequestGeneral(
            params = RequestParams(
                service = "object",
                method = "execute_kw",  //metodo general
                args = listOf(
                    "admin",  // Nombre de la base de datos
                    sessionId,                // Id del usuario
                    "clave$1",        // Contraseña del usuario
                    "res.city.zip",  // Modelo a utilizar
                    "search_read",         // Método a realizar en el modelo
                    listOf(
                        listOf(
                            listOf(campo, "=", cod)
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

                val codPos = findViewById<EditText>(R.id.codPos)
                if (modificar) {
                    if (resultado.isNullOrEmpty()) {
                        var toast = Toast.makeText(this@DatosTutor, "Ese código postal no existe.", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
                        toast.show()
                        codPos.setText(codPosAux)
                    } else {
                        //Recuperar campos
                        val paisC = findViewById<EditText>(R.id.pais)
                        val provinciaC = findViewById<EditText>(R.id.provincia)
                        val ciudadC = findViewById<EditText>(R.id.ciudad)

                        codPosAux = cod.toString()

                        //Recuperar codigo de ciudad, país y provincia
                        val ciudad = resultado?.get(0)?.get("city_id") as ArrayList<Any>
                        val provincia = resultado?.get(0)?.get("state_id") as ArrayList<Any>
                        val pais = resultado?.get(0)?.get("country_id") as ArrayList<Any>
                        val zip = resultado?.get(0)?.get("id") as Double

                        //Separar ciudad
                        val codDoubleCi = ciudad[0] as Double
                        val ciudadFinal = codDoubleCi.toInt()
                        ciudadC.setText(ciudad[1] as String)

                        //Separar provincia
                        val codDoubleProv = provincia[0] as Double
                        val provinciaFinal = codDoubleProv.toInt()
                        provinciaC.setText(provincia[1] as String)

                        //Separar pais
                        val codDoublePais = pais[0] as Double
                        val paisFinal = codDoublePais.toInt()
                        paisC.setText(pais[1] as String)

                        //Zip
                        val zipFinal = zip.toInt()

                        modGeografia(sessionId,ciudadFinal,provinciaFinal,paisFinal,zipFinal)
                    }
                } else {
                    val codP = resultado?.get(0)?.get("name") as String
                    codPosAux = codP
                    codPos.setText(codP)
                }
            }

            override fun onFailure(call: Call<OdooResponseSearch<Map<String, Any>>>, t: Throwable) {
                recuperarCodPost(sessionId, campo,cod, modificar)
            }
        })
    }

    private fun modGeografia(sessionId: Int,codCiu:Int,codProv:Int,codPais:Int, zip:Int) {
        val apiService = RetrofitClient.retrofit.create(OdooApiService::class.java)

        val request = OdooRequestGeneral(
            params = RequestParams(
                service = "object",  // El servicio
                method = "execute_kw",  // El método a llamar
                args = listOf(
                    "admin",  // Nombre de la base de datos
                    sessionId,                // UID del usuario
                    "clave$1",        // Contraseña del usuario
                    "res.partner",  // El modelo que estamos utilizando
                    "write",         // El método en el modelo (write)
                    listOf(
                        listOf(idTutor)  // Filtrar por id
                        ,
                        mapOf( "city_id" to codCiu,"state_id" to codProv, "country_id" to codPais, "zip_id" to zip)
                    ),
                ),
                kwargs = emptyMap()
            )
        )

        apiService.modificarDatos(request).enqueue(object : Callback<OdooResponseWrite> {
            override fun onResponse(
                call: Call<OdooResponseWrite>,
                response: Response<OdooResponseWrite>
            ) {
                val response = response.body()?.result as Boolean

                if(!response){
                    var toast = Toast.makeText(this@DatosTutor, "Ha ocurrido un error, intente modificar de nuevo.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
                    toast.show()
                }else{
                    var toast = Toast.makeText(this@DatosTutor, "Datos modificados Correctamente.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
                    toast.show()
                }
            }

            override fun onFailure(call: Call<OdooResponseWrite>, t: Throwable) {
                println("Error en la modificación")
            }
        })
    }

    private fun modGeneral(sessionId: Int, nombre:String, telefono:String) {
        val apiService = RetrofitClient.retrofit.create(OdooApiService::class.java)

        val request = OdooRequestGeneral(
            params = RequestParams(
                service = "object",  // El servicio
                method = "execute_kw",  // El método a llamar
                args = listOf(
                    "admin",  // Nombre de la base de datos
                    sessionId,                // UID del usuario
                    "clave$1",        // Contraseña del usuario
                    "res.partner",  // El modelo que estamos utilizando
                    "write",         // El método en el modelo (write)
                    listOf(
                        listOf(idTutor)  // Filtrar por id
                        ,
                        mapOf( "complete_name" to nombre,"name" to nombre,"phone" to telefono)
                    ),
                ),
                kwargs = emptyMap()
            )
        )

        apiService.modificarDatos(request).enqueue(object : Callback<OdooResponseWrite> {
            override fun onResponse(
                call: Call<OdooResponseWrite>,
                response: Response<OdooResponseWrite>
            ) {
                val response = response.body()?.result as Boolean

                if(!response){
                    var toast = Toast.makeText(this@DatosTutor, "Ha ocurrido un error, intente modificar de nuevo.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
                    toast.show()
                }else{
                    var toast = Toast.makeText(this@DatosTutor, "Datos modificados Correctamente.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
                    toast.show()
                }
            }

            override fun onFailure(call: Call<OdooResponseWrite>, t: Throwable) {
                println("Error en la modificación")
            }
        })
    }
}