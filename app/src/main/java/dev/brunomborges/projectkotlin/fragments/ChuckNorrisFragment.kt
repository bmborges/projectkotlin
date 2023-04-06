package dev.brunomborges.projectkotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import dev.brunomborges.projectkotlin.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
class ChuckNorrisFragment : Fragment() {

    data class RandomMsg(val icon_url: String, val id: String, val url: String, val value: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chuck_norris, container, false)

        showMsg(view);

        return view;
    }

    companion object {

    }

    private fun showMsg(view: View){
        lifecycleScope.launch{
            val msg = getRandomMsg()

            updateUi(msg, view)
        }
    }

    private fun updateUi(msgData: RandomMsg, view: View){
        val msgChuckNorrisTextView = view.findViewById<TextView>(R.id.msgChuckNorris)
        msgChuckNorrisTextView.text = "${msgData.value}"
    }
    private suspend fun getRandomMsg():RandomMsg {

        val url = "https://api.chucknorris.io/jokes/random"
        val jsonText = withContext(Dispatchers.IO) { URL(url).readText() }
        val jsonObject = JSONObject(jsonText)
        val icon_url = jsonObject.getString("icon_url")
        val id = jsonObject.getString("id")
        val value = jsonObject.getString("value")


        return RandomMsg(icon_url, id, "", value)

    }
}