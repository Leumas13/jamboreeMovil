package com.example.jamboreesportsexperiences.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.jamboreesportsexperiences.R
import com.example.jamboreesportsexperiences.login.PeticionLoginAPI

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Recuperar valores introducidos
        val username = findViewById<EditText>(R.id.usernameText)
        val password = findViewById<EditText>(R.id.passwordTest)

        //Botón de iniciar sesión
        val botInicio = findViewById<Button>(R.id.buttonLogIn)

        botInicio.setOnClickListener {
            //Llamada al método para iniciar sesión, se realiza así para poder recuperar el valor devuelto
            val pAPI = PeticionLoginAPI()
            pAPI.login(username.text.toString(),password.text.toString()){res->
                if(res!=null){
                    val intent = Intent(this, CalendarioEntrenamientos::class.java)
                    intent.putExtra("email",username.text.toString())
                    startActivity(intent)
                }else{
                    var toast = Toast.makeText(this, "La contraseña o el nombre de usuario " +
                    "son incorrectos. Por favor introduzca los datos correctamente.", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, 800)
                    toast.show()
                }
            }
        }
    }


}