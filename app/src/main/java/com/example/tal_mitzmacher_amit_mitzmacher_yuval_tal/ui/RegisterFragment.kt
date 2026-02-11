package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.R
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val TAG = "RegisterFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // הגדרת מאזין לכפתור ההרשמה
        binding.btnRegister.setOnClickListener {
            val email = binding.EmailReg.text.toString().trim()
            val password = binding.PasswordReg.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                createNewUser(email, password)
            } else {
                Toast.makeText(requireContext(), "נא למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun createNewUser(email: String, password: String) {
        // ב-Fragment משתמשים ב-requireActivity() במקום ב-this
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // הצלחה בהרשמה
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    Toast.makeText(requireContext(), "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show()

                    // מעבר למסך הבא (RecipeList)
                    findNavController().navigate(R.id.action_registerFragment_to_logInFragment)
                } else {
                    // כישלון בהרשמה
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "שגיאה בתהליך ההרשמה: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}