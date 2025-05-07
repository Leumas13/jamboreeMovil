package com.example.jamboreesportsexperiences.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.jamboreesportsexperiences.R
import com.example.jamboreesportsexperiences.login.PeticionLoginAPI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Base_Theme_JamboreeSportsExperiences)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Recuperar valores introducidos
        val username = findViewById<EditText>(R.id.usernameText)
        val password = findViewById<EditText>(R.id.passwordTest)

        //Botón de iniciar sesión
        val botInicio = findViewById<Button>(R.id.buttonLogIn)

        //Recuperar marcas de error
        val as1 = findViewById<TextView>(R.id.ast1)
        val as2 = findViewById<TextView>(R.id.ast2)
        var error = false

        botInicio.setOnClickListener {
            //Llamada al método para iniciar sesión, se realiza así para poder recuperar el valor devuelto
            val pAPI = PeticionLoginAPI()
            pAPI.login(username.text.toString(),password.text.toString()){res->
                if(res!=null){
                    val intent = Intent(this, CalendarioEntrenamientos::class.java)
                    intent.putExtra("email",username.text.toString())
                    startActivity(intent)
                }else{
                    var alert = AlertDialog.Builder(this)
                        .setTitle("Error de autenticación")
                        .setMessage("La contraseña o el nombre de usuario son incorrectos. Por favor introduzca los datos correctamente.")
                        .setPositiveButton("Ok",null)
                        .setIcon(R.drawable.error_icono)
                        .create()

                    alert.show()

                    if(!error){
                        as1.isVisible = true
                        as2.isVisible = true
                    }

                    error = true
                }
            }
        }
    }


}