package com.example.dealwithexpenses.authentication

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
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class LoginScreenFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var action: NavDirections
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callBackManager: CallbackManager
    private var isLoggedIn: Boolean = false

    private val signInCode = 12345

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)

        // Configure Google Sign In

        // Build a GoogleSignInClient with the options specified by googleSignInOptions.

        // Set the click listeners
        binding.customRegisterButton.setBackgroundColor(resources.getColor(R.color.white))
        binding.customRegisterButton.setTextColor(resources.getColor(R.color.black))
        binding.googleLoginButton.setOnClickListener {
            GoogleLogin.initialisation(this)
        }

        binding.fbLoginButton.setOnClickListener {
            callBackManager = CallbackManager.Factory.create()
            FBLogin.login(this, callBackManager)
        }

        binding.stayLoggedIn.setOnClickListener {
            isLoggedIn = binding.stayLoggedIn.isChecked
        }

        binding.customLoginButton.setOnClickListener {
            handleCustomLogin()
        }

        binding.customRegisterButton.setOnClickListener {
            action =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterScreenFragment()
            if(isLoggedIn){
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", true).apply()
            }
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun handleCustomLogin() {
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
                                "Welcome to MakeMyBudget",
                                Toast.LENGTH_SHORT
                            ).show()
                            Navigate.action(this)
                        } else {
                            Toast.makeText(
                                context,
                                "Invalid username or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == signInCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                GoogleLogin.firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign in failed ☹", Toast.LENGTH_SHORT)
                    .show()
            }
            super.onActivityResult(requestCode, resultCode, data)
        } else
            callBackManager.onActivityResult(requestCode, resultCode, data)
    }
}