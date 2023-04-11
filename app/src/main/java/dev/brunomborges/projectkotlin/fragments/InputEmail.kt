package dev.brunomborges.projectkotlin.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import dev.brunomborges.projectkotlin.R

class InputEmail : Fragment() {

    private lateinit var textViewErrorEmailValue: TextView
    lateinit var etEmail: EditText
    lateinit var text: Editable;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_input_email, container, false)

        textViewErrorEmailValue = view.findViewById<TextView>(R.id.textViewErrorEmailValue)
        etEmail = view.findViewById<EditText>(R.id.etEmail)

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    text = s
                }

                if(text.toString().length > 0) {
                    if(!isValidEmail(text.toString())) {
                        textViewErrorEmailValue.text = "Email Não é Valido"
                    } else {
                        textViewErrorEmailValue.text = "Email Valido"
                    }
                } else {
                    textViewErrorEmailValue.text = ""
                }

            }

        })

        return view;
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    companion object {
    }
}