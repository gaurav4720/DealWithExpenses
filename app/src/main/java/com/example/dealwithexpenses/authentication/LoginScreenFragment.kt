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
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*


class LoginScreenFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var action : NavDirections
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val SIGN_IN_CODE = 12345
    private lateinit var callBackManager: CallbackManager

    private var allCheck: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!
        val isRegistered: Boolean = sharedPreferences.getBoolean("isRegistered", false)
        val isLoggedIn: Boolean = sharedPreferences.getBoolean("isLoggedIn", false)
        allCheck = sharedPreferences.getBoolean("allCheck", false)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)

        // Configure Google Sign In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        // Build a GoogleSignInClient with the options specified by googleSignInOptions.
        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        // Set the click listeners
        binding.customRegisterButton.setBackgroundColor(resources.getColor(R.color.white))
        binding.customRegisterButton.setTextColor(resources.getColor(R.color.black))
        binding.googleLoginButton.setOnClickListener {
            googleLogin()
        }

        binding.fbLoginButton.setOnClickListener {
            fbLogin()
        }

        binding.customRegisterButton.setOnClickListener {
            action =
                LoginScreenFragmentDirections.actionLoginScreenFragmentToRegisterScreenFragment()
            findNavController().navigate(action)
        }

        var stayLoggedIn = false
        binding.stayLoggedIn.setOnClickListener {
            stayLoggedIn = binding.stayLoggedIn.isChecked
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


        return binding.root
    }

    private fun action() {
        val action : NavDirections = if (!allCheck)
            LoginScreenFragmentDirections.actionLoginScreenFragmentToUserBudgetDetailsFragment()
        else
            LoginScreenFragmentDirections.actionLoginScreenFragmentToMainScreenFragment()

        findNavController().navigate(action)
    }

    private fun googleLogin() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, SIGN_IN_CODE)
    }

    private fun fbLogin() {
        callBackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(
                callBackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Toast.makeText(activity, "Login Cancelled", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: FacebookException) {
                        Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess(result: LoginResult) {
                        handleFBLogin(result)
                    }

                }
            )
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleFBLogin(result: LoginResult) {
        val credentials = FacebookAuthProvider.getCredential(result.accessToken.token)
        GlobalScope.launch(Dispatchers.IO) {
            val auth = firebaseAuth.signInWithCredential(credentials)
            val allCheck: Boolean = sharedPreferences.getBoolean("allCheck", false)
            auth.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(activity, "Login Successful", Toast.LENGTH_SHORT).show()
                    val user = auth.result?.user
                } else {
                    Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
            withContext(Dispatchers.Main) {

                sharedPreferences.edit().putBoolean("isRegistered", true).apply()
                action()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        GlobalScope.launch(Dispatchers.IO) {

            val auth = firebaseAuth.signInWithCredential(credentials).addOnSuccessListener {
                Toast.makeText(context, "Google sign in success 🎉", Toast.LENGTH_SHORT)
                    .show()

                val user = it.user

                if (user != null) {
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    sharedPreferences.edit().putBoolean("isRegistered", true).apply()
                    action()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign in failed ☹", Toast.LENGTH_SHORT)
                    .show()
            }
            super.onActivityResult(requestCode, resultCode, data)
        } else
            callBackManager.onActivityResult(requestCode, resultCode, data)
    }
}