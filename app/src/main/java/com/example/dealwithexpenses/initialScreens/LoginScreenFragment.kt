package com.example.dealwithexpenses.initialScreens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginScreenFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val SIGN_IN_CODE = 123
    private var allCheck: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!
        val isRegistered: Boolean = sharedPreferences.getBoolean("isRegistered", false)
        val isLoggedIn: Boolean = sharedPreferences.getBoolean("isLoggedIn", false)
        allCheck = sharedPreferences.getBoolean("allCheck", false)

        var action: NavDirections

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        binding.googleLoginButton.setOnClickListener {
            findNavController().navigate(LoginScreenFragmentDirections.actionLoginScreenFragmentToGoogleLoginFragment())
        }

        // Set the click listeners
        binding.customRegisterButton.setBackgroundColor(resources.getColor(R.color.white))
        binding.customRegisterButton.setTextColor(resources.getColor(R.color.black))
        binding.customRegisterButton.setOnClickListener {
            val action =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterScreenFragment()
            findNavController().navigate(action)
        }


        var stayLoggedIn = false
        binding.stayLoggedIn.setOnClickListener {
            stayLoggedIn = binding.stayLoggedIn.isChecked
        }

        binding.customLoginButton.setOnClickListener {
            handleCustomLogin()
        }

        binding.fbLoginButton.setOnClickListener {
            val action =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToFacebookLoginFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun handleCustomLogin() {
        val email = binding.userName.text.toString()
        val password = binding.passWord.text.toString()

        if (email.isEmpty() or password.isEmpty()) {
            Toast.makeText(
                context,
                "Please enter email and password",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT)
                    .show()
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Welcome to MakeMyBudget",
                                Toast.LENGTH_SHORT
                            ).show()
                            action()
                        } else {
                            Toast.makeText(
                                context,
                                "Invalid username or password",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
            }
        }
    }

    private fun action() {
        var action =
            LoginScreenFragmentDirections.actionLoginScreenFragmentToMainScreenFragment()
        if (!allCheck)
            action =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToUserBudgetDetailsFragment()

        findNavController().navigate(action)
    }
}