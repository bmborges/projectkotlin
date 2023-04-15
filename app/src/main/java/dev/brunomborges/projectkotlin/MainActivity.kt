package dev.brunomborges.projectkotlin

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import android.widget.ArrayAdapter
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.brunomborges.projectkotlin.fragments.ChuckNorrisFragment
import dev.brunomborges.projectkotlin.fragments.WeatherFragment

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var gson = Gson()
    lateinit var mGoogleSignClient: GoogleSignInClient;

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks")
    val listItems = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, WeatherFragment()).commit()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_msg_chuck_norris, ChuckNorrisFragment()).commit()

        sharedPreferences = getSharedPreferences("lista_de_tarefas", Context.MODE_PRIVATE)

        val listViewTasks = findViewById<android.widget.ListView>(R.id.listViewTasks);
        val createTask = findViewById<android.widget.EditText>(R.id.createTask);

        val itemList = getData();

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        val listView = findViewById<android.widget.ListView>(R.id.listViewTasks)
        listView.adapter = adapter

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        mGoogleSignClient = GoogleSignIn.getClient(this, gso);

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

        listViewTasks.adapter = adapter;
        adapter.notifyDataSetChanged()

        findViewById<View>(R.id.add).setOnClickListener {
            val activity = Intent(this, TaskActivity::class.java);
            startActivity(activity)
            finish()
        }

//        findViewById<View>(R.id.delete).setOnClickListener {
//            val position: SparseBooleanArray = listViewTasks.checkedItemPositions;
//            val count = listViewTasks.count;
//            var item = count - 1;
//            while(item >= 0){
//                if(position.get(item)){
//                    adapter.remove(itemList.get(item))
//                }
//                item--;
//            }
//
//            saveData(itemList)
//            position.clear();
//            adapter.notifyDataSetChanged()
//        }

//        findViewById<View>(R.id.clear).setOnClickListener{
//            itemList.clear()
//            saveData(itemList)
//            adapter.notifyDataSetChanged()
//        }

        findViewById<View>(R.id.logout).setOnClickListener{
            firebaseAuth.signOut();
            mGoogleSignClient.signOut();

            val activity = Intent(this, LoginScreen::class.java);
            startActivity(activity);
        }

        findViewById<View>(R.id.profile).setOnClickListener{
            val activity = Intent(this, ProfileActivity::class.java);
            startActivity(activity)
            finish()
        }

        ref.addValueEventListener(object: ValueEventListener {
            val ctx = this@MainActivity;

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listItems.clear()

                for(child in dataSnapshot.children){
                    listItems.add(child.child("titulo").value.toString())
                }

                adapter.notifyDataSetChanged()

                listView.setOnItemLongClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    if(itemId != null){
                        AlertDialog.Builder(ctx)
                            .setTitle("Deletar tarefa")
                            .setMessage("Deseja deletar a tarefa?")
                            .setPositiveButton("Sim"){ dialog, which ->
                                ref.child(itemId).removeValue()
                                Toast.makeText(ctx, "Tarefa deletada com sucesso", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Não"){ dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }
                listView.setOnItemClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    val activity = Intent(ctx, TaskActivity::class.java)
                    activity.putExtra("id", itemId)
                    startActivity(activity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, "Erro ao carregar tarefas", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getData(): ArrayList<String> {
        val arrayJson = sharedPreferences.getString("lista", null);
        return if(arrayJson.isNullOrEmpty()){
            arrayListOf();
        }else{
            gson.fromJson(arrayJson, object: TypeToken<ArrayList<String>>(){}.type)
        }
    }

    private fun saveData(array: ArrayList<String>){
        val arrayJson = gson.toJson(array);
        val editor = sharedPreferences.edit();
        editor.putString("lista", arrayJson);
        editor.apply();
    }


}