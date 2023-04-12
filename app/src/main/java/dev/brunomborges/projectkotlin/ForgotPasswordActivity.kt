package dev.brunomborges.projectkotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import dev.brunomborges.projectkotlin.databinding.ActivityForgotPasswordBinding
import dev.brunomborges.projectkotlin.fragments.InputEmail

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

        findViewById<View>(R.id.btnRecuperar).setOnClickListener{
            val emailAddress = supportFragmentManager.findFragmentById(R.id.etEmail) as InputEmail

            firebaseAuth.sendPasswordResetEmail(emailAddress.text.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email enviado com sucesso!", Toast.LENGTH_SHORT).show()
                    returnToLogin()
                }
            }
        }
    }

    fun returnToLogin(){
        val activity = Intent(this, LoginScreen::class.java);
        startActivity(activity)
        finish()
    }
}