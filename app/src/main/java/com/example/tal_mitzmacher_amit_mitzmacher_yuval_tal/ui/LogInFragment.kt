package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.MainActivity
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log_in, container, false)

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val buttonLogin = view.findViewById<Button>(R.id.btnLogin)
        val buttonRegister = view.findViewById<Button>(R.id.btnGoToRegister)

        buttonLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val mainActivity = activity as? MainActivity

                mainActivity?.login(email, password) {
                    findNavController().navigate(R.id.action_logInFragment_to_recipeListFragment)
                }
            } else {
                Toast.makeText(context, getString(R.string.FillEmailPass), Toast.LENGTH_SHORT).show()
            }
        }

        buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
        }

        return view
    }
}