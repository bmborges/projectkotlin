package dev.tiagosilva.finalprojectclassroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        var listViewTasks = findViewById<android.widget.ListView>(R.id.listViewTasks);
        var createTask = findViewById<android.widget.EditText>(R.id.createTask);

        var itemList = arrayListOf<String>();
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, itemList);

        findViewById<View>(R.id.add).setOnClickListener {
            itemList.add(createTask.text.toString());
            listViewTasks.adapter = adapter;
            adapter.notifyDataSetChanged()

            createTask.text.clear()
        }

        findViewById<View>(R.id.logout).setOnClickListener{
            val activity = Intent(this, LoginScreen::class.java);
            startActivity(activity);
        }
    }
}