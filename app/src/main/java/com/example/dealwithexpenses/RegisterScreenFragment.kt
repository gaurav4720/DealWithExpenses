package com.example.dealwithexpenses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.dealwithexpenses.databinding.FragmentRegisterScreenBinding
import com.example.dealwithexpenses.entities.User
import com.google.firebase.auth.FirebaseAuth

class RegisterScreenFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentRegisterScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)

        binding.registerButton.setOnClickListener {
            handleCustomLogin()
        }
        return binding.root
    }

    private fun handleCustomLogin() {
        val email = binding.userName.text.toString()
        val password = binding.passWord.text.toString()
        val confirmation = binding.confirmPassWord.text.toString()

        if (email.isEmpty() or password.isEmpty() or confirmation.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else if (password != confirmation) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Successfully registered", Toast.LENGTH_SHORT)
                            .show()
                        val user = User(email, password, "0")
                        firebaseAuth.currentUser?.uid?.let {
                            user.user_id = it
                        }
                    } else {
                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

}