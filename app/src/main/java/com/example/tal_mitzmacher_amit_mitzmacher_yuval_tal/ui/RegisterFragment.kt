package com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tal_mitzmacher_amit_mitzmacher_yuval_tal.MainActivity
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

        binding.btnRegister.setOnClickListener {
            val email = binding.EmailReg.text.toString().trim()
            val password = binding.PasswordReg.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val mainActivity = activity as? MainActivity
                mainActivity?.createNewUser(email, password){
                    findNavController().navigate(R.id.action_registerFragment_to_logInFragment)
                }

            } else {
                Toast.makeText(requireContext(), getText(R.string.FillEmailPass), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}