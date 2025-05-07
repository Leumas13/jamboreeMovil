package com.example.jamboreesportsexperiences.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.example.jamboreesportsexperiences.R
import com.example.jamboreesportsexperiences.login.PeticionLoginAPI
import com.example.jamboreesportsexperiences.odoo.OdooRequestGeneral
import com.example.jamboreesportsexperiences.odoo.OdooResponseSearch
import com.example.jamboreesportsexperiences.odoo.RequestParams
import com.example.jamboreesportsexperiences.retrofitYservicio.OdooApiService
import com.example.jamboreesportsexperiences.retrofitYservicio.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class CalendarioEntrenamientos : AppCompatActivity() {
    //Mapa que contendrá la información de los entrenamientos
    private var mapaEntrenamientos: MutableMap<String, CalendarDay> = mutableMapOf()
    private var mapaEventos: MutableMap<String, String> = mutableMapOf()
    private var llamadasAPI=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_entrenamientos)

        //Recuperar el email del usuario
        val emailTutor = intent.getStringExtra("email")

        //Recuperar calendario
        val calendarView = findViewById<CalendarView>(R.id.calendar)

        //Recuperar la tabla
        val tabla = findViewById<TableLayout>(R.id.tabla)
        filaPrincipal(tabla)

        var idAdmin: Int? = null
        //Login como administrador
        val pAPI = PeticionLoginAPI()
        pAPI.login("aitor","clave$1"){uid->
            if(uid!=null){
                if (emailTutor != null) {
                    recuperarTutor(uid, emailTutor)
                    idAdmin = uid
                }
            }else{
                var alert = AlertDialog.Builder(this)
                    .setTitle("Error de conexión")
                    .setMessage("Ha ocurrido un error de conexión. Por favor, vuelva a iniciar sesión para solucionarlo.")
                    .setPositiveButton("Ok"){ _, _ ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .setIcon(R.drawable.error_icono)
                    .create()

                alert.show()
            }
        }

        //Recuperar botón para salir y función que realiza la acción
        val btSalir = findViewById<ImageButton>(R.id.btSalirCal)

        btSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Recuperar botón para ir al perfil y función que realiza la acción
        val btPerfil = findViewById<ImageButton>(R.id.btPerfil)

        btPerfil.setOnClickListener {
            val intent = Intent(this, DatosTutor::class.java)
            intent.putExtra("email",emailTutor)
            intent.putExtra("idAdmin",idAdmin)
            startActivity(intent)
        }

        calendarView.setOnCalendarDayClickListener(object: OnCalendarDayClickListener {
            @SuppressLint("ResourceAsColor")
            override fun onClick(calendarDay: CalendarDay) {
                val dia = calendarDay.calendar.get(Calendar.DAY_OF_MONTH)
                val mes = calendarDay.calendar.get(Calendar.MONTH)
                val anyo = calendarDay.calendar.get(Calendar.YEAR)

                tabla.removeAllViews()
                filaPrincipal(tabla)

                if(mapaEventos.containsKey("$anyo-$mes-$dia")){
                    val jugadores = mapaEventos["$anyo-$mes-$dia"]?.split("\n")

                    if (jugadores != null) {
                        for(jugador in jugadores){
                            val valores = jugador.split(";")
                            val nombreJ = valores[0]
                            val nombreS = valores[1]
                            val h = valores[2]
                            val tur = valores[3]

                            //crear una nueva fila
                            val fila = TableRow(this@CalendarioEntrenamientos)

                            //fuente
                            val fuente = ResourcesCompat.getFont(this@CalendarioEntrenamientos, R.font.montserrat_medium)

                            //crear columnas
                            val nombreJugador = TextView(this@CalendarioEntrenamientos)
                            nombreJugador.text = nombreJ
                            nombreJugador.typeface = fuente
                            nombreJugador.textSize = 18f // Tamaño del texto
                            nombreJugador.gravity = Gravity.CENTER // Centrado
                            nombreJugador.setBackgroundResource(R.drawable.bordes_celda) //bordes
                            nombreJugador.setPadding(0, 0, 0, 20)//Espacio por abajo
                            nombreJugador.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                            val nombreSede = TextView(this@CalendarioEntrenamientos)
                            nombreSede.text = nombreS
                            nombreSede.typeface = fuente
                            nombreSede.textSize = 18f // Tamaño del texto
                            nombreSede.gravity = Gravity.CENTER // Centrado
                            nombreSede.setBackgroundResource(R.drawable.bordes_celda) //bordes
                            nombreSede.setPadding(0, 0, 0, 20)//Espacio por abajo
                            nombreSede.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                            val hora = TextView(this@CalendarioEntrenamientos)
                            hora.text = h
                            hora.typeface = fuente
                            hora.textSize = 18f // Tamaño del texto
                            hora.gravity = Gravity.CENTER // Centrado
                            hora.setBackgroundResource(R.drawable.bordes_celda) //bordes
                            hora.setPadding(0, 0, 0, 20)//Espacio por abajo
                            hora.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                            val tipo = TextView(this@CalendarioEntrenamientos)
                            tipo.text = tur
                            tipo.typeface = fuente
                            tipo.textSize = 18f // Tamaño del texto
                            tipo.gravity = Gravity.CENTER // Centrado
                            tipo.setBackgroundResource(R.drawable.bordes_celda) //bordes
                            tipo.setPadding(0, 0, 0, 20)//Espacio por abajo
                            tipo.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

                            //añadir columnas
                            fila.addView(nombreJugador)
                            fila.addView(nombreSede)
                            fila.addView(hora)
                            fila.addView(tipo)

                            //añadir fila a la tabla
                            tabla.addView(fila)

                        }
                    }

                }
            }
        })


        calendarView.setOnPreviousPageChangeListener(object: OnCalendarPageChangeListener {
            override fun onChange() {
                val month = calendarView.currentPageDate.get(Calendar.MONTH)+1
                val year = calendarView.currentPageDate.get(Calendar.YEAR)
                val toast = Toast.makeText(baseContext, "$month/$year", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 1100)
                toast.show()
            }
        })

        calendarView.setOnForwardPageChangeListener(object: OnCalendarPageChangeListener{
            override fun onChange() {
                val month = calendarView.currentPageDate.get(Calendar.MONTH)+1
                val year = calendarView.currentPageDate.get(Calendar.YEAR)
                val toast = Toast.makeText(baseContext, "$month/$year", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 1100)
                toast.show()
            }
        })

    }

    private fun recuperarTutor(sessionId: Int, email:String) {
        val apiService = RetrofitClient.retrofit.create(OdooApiService::class.java)

        val request = OdooRequestGeneral(
            params = RequestParams(
                service = "object",
                method = "execute_kw",  //metodo general
                args = listOf(
                    "admin",  // Nombre de la base de datos
                    sessionId,                // Id del usuario
                    "clave$1",        // Contraseña del usuario
                    "stmg_jamboree.tutor",  // Modelo a utilizar
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

                /*response.body()?.result?.let { result ->
                    println("Resultado de la búsqueda: $result")
                }*/
                val resultado = response.body()?.result;
                val arrayJug = resultado?.get(0)?.get("jugador_ids") as ArrayList<Double>;

                //Barra de progreso
                var barraProg = findViewById<ProgressBar>(R.id.barraP)
                barraProg.progress += 10
                for (id in arrayJug){
                    if(barraProg.progress<100){
                        barraProg.progress += 10
                    }else{
                        barraProg.progress =0
                    }
                    recuperarJugadores(sessionId,id.toInt())
                }


            }

            override fun onFailure(call: Call<OdooResponseSearch<Map<String, Any>>>, t: Throwable) {
                recuperarTutor(sessionId, email)
            }
        })
    }

    private fun recuperarJugadores(sessionId: Int, id:Int) {
        val apiService = RetrofitClient.retrofit.create(OdooApiService::class.java)

        val request = OdooRequestGeneral(
            params = RequestParams(
                service = "object",
                method = "execute_kw",
                args = listOf(
                    "admin",
                    sessionId,
                    "clave$1",
                    "stmg_jamboree.jugador",
                    "search_read",
                    listOf(
                        listOf(
                            listOf("id", "=", id)
                        )
                    )
                ),
                kwargs = emptyMap()
            )
        )

        apiService.recuperarInfo(request).enqueue(object :
            Callback<OdooResponseSearch<Map<String, Any>>> {
            override fun onResponse(
                call: Call<OdooResponseSearch<Map<String, Any>>>,
                response: Response<OdooResponseSearch<Map<String, Any>>>
            ) {

                val resultado = response.body()?.result;
                val arrayEntrenamientos = resultado?.get(0)?.get("entrenamiento_ids") as ArrayList<Double>;
                val nombreJug = resultado?.get(0)?.get("nombre") as String;

                //Barra de progreso
                var barraProg = findViewById<ProgressBar>(R.id.barraP)
                barraProg.progress += 10
                for (i in arrayEntrenamientos){
                    if(barraProg.progress<100){
                        barraProg.progress += 10
                    }else{
                        barraProg.progress =0
                    }
                    recuperarEntrenamientos(sessionId,i.toInt(),nombreJug)
                }

            }

            override fun onFailure(call: Call<OdooResponseSearch<Map<String, Any>>>, t: Throwable) {
                recuperarJugadores(sessionId,id)
            }
        })
    }

    private fun recuperarEntrenamientos(sessionId: Int, id:Int,nombreJug:String) {
        llamadasAPI++
        val apiService = RetrofitClient.retrofit.create(OdooApiService::class.java)

        val request = OdooRequestGeneral(
            params = RequestParams(
                service = "object",
                method = "execute_kw",
                args = listOf(
                    "admin",
                    sessionId,
                    "clave$1",
                    "stmg_jamboree.entrenamiento",
                    "search_read",
                    listOf(
                        listOf(
                            listOf("id", "=", id)
                        )
                    )
                ),
                kwargs = emptyMap()
            )
        )

        apiService.recuperarInfo(request).enqueue(object :
            Callback<OdooResponseSearch<Map<String, Any>>> {
            override fun onResponse(
                call: Call<OdooResponseSearch<Map<String, Any>>>,
                response: Response<OdooResponseSearch<Map<String, Any>>>
            ) {
                val resultado = response.body()?.result;

                //Recupero el turno
                val turno = resultado?.get(0)?.get("turno").toString()
                val fechaYhora = turno.split(" ")
                val fecha = fechaYhora[0]
                val hora = fechaYhora[1]

                val fechaSeparada = fecha.split("-")
                val anyo = fechaSeparada[0].toInt()
                val mes = fechaSeparada[1].toInt()-1
                val dia = fechaSeparada[2].toInt()

                //Recupero el nombre de la sede(su ubicación)
                val nombreSede = resultado?.get(0)?.get("sede_nombre").toString()

                //Recupero el tipo de entrenamiento
                val tipo = resultado?.get(0)?.get("tipo").toString()

                if(mapaEntrenamientos.containsKey(fecha)){
                    var diaRecuperado = mapaEntrenamientos[fecha]
                    diaRecuperado?.imageResource = R.drawable.dos_jug
                    mapaEventos["$anyo-$mes-$dia"] = mapaEventos["$anyo-$mes-$dia"]+"\n$nombreJug;$nombreSede;$hora;$tipo"
                }else{
                    val calendar = Calendar.getInstance()
                    calendar.set(anyo, mes, dia)

                    val calendarDay = CalendarDay(calendar)
                    calendarDay.labelColor = R.color.black
                    calendarDay.imageResource = R.drawable.un_jug

                    mapaEntrenamientos[fecha] = calendarDay
                    mapaEventos["$anyo-$mes-$dia"] = "$nombreJug;$nombreSede;$hora;$tipo"
                }

                //Barra de progreso
                var barraProg = findViewById<ProgressBar>(R.id.barraP)
                if(barraProg.progress<100){
                    barraProg.progress += 10
                }else{
                    barraProg.progress = 0
                }

                //Se llama a pintar el calendario, esto solo funcionará si no hay más llamadas a la API (llamadasAPI=0)
                llamadasAPI--
                pintarCalendario()
            }

            override fun onFailure(call: Call<OdooResponseSearch<Map<String, Any>>>, t: Throwable) {
                llamadasAPI--
                recuperarEntrenamientos(sessionId, id, nombreJug)
            }
        })
    }

    private fun pintarCalendario(){
            if(llamadasAPI==0){
                val calendars: ArrayList<CalendarDay> = ArrayList()
                val calendarView = findViewById<CalendarView>(R.id.calendar)
                for ((clave, valor) in mapaEntrenamientos) {
                    calendars.add(valor)
                }

                calendarView.setCalendarDays(calendars)

                val btPerfil = findViewById<ImageButton>(R.id.btPerfil)
                btPerfil.isClickable = true

                //Barra de progreso
                var barraProg = findViewById<ProgressBar>(R.id.barraP)
                barraProg.progress = 100
                barraProg.isVisible = false
            }

    }

    private fun filaPrincipal(tabla:TableLayout){

        //crear una nueva fila
        val fila = TableRow(this@CalendarioEntrenamientos)
        //crear una nueva fila
        val fila2 = TableRow(this@CalendarioEntrenamientos)
        //fuente
        val fuente = ResourcesCompat.getFont(this, R.font.montserrat_bold)

        //crear columnas
        val e = TextView(this@CalendarioEntrenamientos)
        e.text = "Detalles"
        e.typeface = fuente// Negrita
        e.textSize = 30f // Tamaño del texto
        e.setTextColor(Color.BLACK) // Color del texto
        e.gravity = Gravity.CENTER // Centrado
        e.setBackgroundResource(R.drawable.bordes_celda) //bordes
        e.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        //crear columnas
        val nombreJugador = TextView(this@CalendarioEntrenamientos)
        nombreJugador.text = "Jugador"
        nombreJugador.typeface = fuente// Negrita
        nombreJugador.textSize = 20f // Tamaño del texto
        nombreJugador.setTextColor(Color.BLACK) // Color del texto
        nombreJugador.gravity = Gravity.CENTER // Centrado
        nombreJugador.setBackgroundResource(R.drawable.bordes_celda) //bordes
        nombreJugador.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        val nombreSede = TextView(this@CalendarioEntrenamientos)
        nombreSede.text = "Sede"
        nombreSede.typeface = fuente // Negrita
        nombreSede.textSize = 20f // Tamaño del texto
        nombreSede.setTextColor(Color.BLACK) // Color del texto
        nombreSede.gravity = Gravity.CENTER // Centrado
        nombreSede.setBackgroundResource(R.drawable.bordes_celda) //bordes
        nombreSede.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        val hora = TextView(this@CalendarioEntrenamientos)
        hora.text = "Hora"
        hora.typeface = fuente// Negrita
        hora.textSize = 20f // Tamaño del texto
        hora.setTextColor(Color.BLACK) // Color del texto
        hora.gravity = Gravity.CENTER // Centrado
        hora.setBackgroundResource(R.drawable.bordes_celda) //bordes
        hora.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        val tipo = TextView(this@CalendarioEntrenamientos)
        tipo.text = "Tipo"
        tipo.typeface = fuente // Negrita
        tipo.textSize = 20f // Tamaño del texto
        tipo.setTextColor(Color.BLACK) // Color del texto
        tipo.gravity = Gravity.CENTER // Centrado
        tipo.setBackgroundResource(R.drawable.bordes_celda) //bordes
        tipo.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        //añadir columnas
        fila.addView(nombreJugador)
        fila.addView(nombreSede)
        fila.addView(hora)
        fila.addView(tipo)

        fila2.addView(e)
        tabla.addView(fila2)
        //añadir fila a la tabla
        tabla.addView(fila)
    }
}