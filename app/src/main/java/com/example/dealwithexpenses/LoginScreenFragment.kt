package com.example.dealwithexpenses

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.dealwithexpenses.databinding.FragmentLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth


class LoginScreenFragment : Fragment() {
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.customRegisterButton.setOnClickListener {
            val action = LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterScreenFragment()
            it.findNavController().navigate(action)
        }
        binding.customLoginButton.setOnClickListener {
            val email = binding.userName.text.toString()
            val password = binding.passWord.text.toString()

            if (email.isEmpty() or password.isEmpty()) {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT).show()
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Welcome to Deal With Expenses",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

        }

    }
}