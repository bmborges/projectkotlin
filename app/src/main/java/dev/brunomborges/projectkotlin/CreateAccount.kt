package dev.brunomborges.projectkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import dev.brunomborges.projectkotlin.fragments.ChuckNorrisFragment
import dev.brunomborges.projectkotlin.fragments.InputEmail
import dev.brunomborges.projectkotlin.fragments.InputPassword

class CreateAccount : AppCompatActivity() {
    lateinit var etEmail: InputEmail
    lateinit var etPassword: InputPassword
    lateinit var etConfirmPassword: EditText
    lateinit var createAccountInputArray: Array<Any>

    val Req_Code:Int=123;
    lateinit var mGoogleSignInClient: GoogleSignInClient;
    private lateinit var firebaseAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        supportActionBar?.hide()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_msg_chuck_norris, ChuckNorrisFragment()).commit()


        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance();

        etEmail = supportFragmentManager.findFragmentById(R.id.etEmail) as InputEmail
        etPassword = supportFragmentManager.findFragmentById(R.id.etPassword) as InputPassword
        etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        createAccountInputArray = arrayOf(etEmail, etPassword, etConfirmPassword)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("sdfdsf")
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        findViewById<View>(R.id.loginView).setOnClickListener{
            val activity = Intent(this, LoginScreen::class.java);
            startActivity(activity);
        }


        findViewById<View>(R.id.btnCreateAccount).setOnClickListener {
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Req_Code){
            val result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(result);
        }
    }

    private fun sendEmailVerification(){
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "E-mail enviado com sucesso", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java);
            Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
            if(account != null){
                UpdateUser(account)
            }
        }catch (e: ApiException){
            println(e)
            Toast.makeText(this, "Falha ao tentar logar", Toast.LENGTH_SHORT).show();
        }
    }

    private fun UpdateUser(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
                finish()
            }
        }
    }

    private fun notEmpty(): Boolean = etPassword.text.toString().trim().isNotEmpty() &&
            etEmail.text.toString().trim().isNotEmpty() &&
            etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun verifyIdenticalPassword(): Boolean{
        var identical = false;
        if(etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()){
            identical = true;
        }

        return identical;
    }

    private fun verifySizePassword(): Boolean{
        var correct = false
        if(etPassword.text.toString().trim().length >= 6) {
            correct = true
        }
        return correct;
    }

    private fun signIn(){
        if(notEmpty()){
            if(verifyIdenticalPassword()){
                if(verifySizePassword()){
                    val userEmail = etEmail.text.toString().trim()
                    val userPassword = etPassword.text.toString().trim()

                    firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            sendEmailVerification()
                            Toast.makeText(this, "Usuário criado com sucesso. Verifique sua caixa de e-mail", Toast.LENGTH_SHORT).show();
                            finish()
                        }else{
                            val exception = task.exception;
                            if(exception is FirebaseAuthException && exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE"){
                                Toast.makeText(this, "Email já cadastrado.", Toast.LENGTH_SHORT).show();
                            }else if(exception is FirebaseAuthException && exception.errorCode == "ERROR_WEAK_PASSWORD"){
                                Toast.makeText(this, "Senha não obedece as regras de criação de senha.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(this, "Erro ao criar usuário.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }else{
                    Toast.makeText(this, "As senhas são iguais mas de tamanho inadequado.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "As senhas devem ser iguais.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, R.string.todos_os_campos_devem_ser_preenchidos, Toast.LENGTH_SHORT).show();
        }
    }


}