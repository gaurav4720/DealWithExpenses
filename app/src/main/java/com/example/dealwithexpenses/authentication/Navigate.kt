package com.example.dealwithexpenses.authentication

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment

class Navigate {
    companion object {

        private lateinit var sharedPreferences: SharedPreferences

        fun action(fragment: Fragment) {
            sharedPreferences =
                fragment.requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)

            val allCheck = sharedPreferences.getBoolean("allCheck", false)
            val action: NavDirections = if (fragment is LoginScreenFragment) {
                if (!allCheck)
                    LoginScreenFragmentDirections.actionLoginScreenFragmentToUserBudgetDetailsFragment()
                else
                    LoginScreenFragmentDirections.actionLoginScreenFragmentToMainScreenFragment()
            } else {
                if (!allCheck)
                    RegisterScreenFragmentDirections.actionRegisterScreenFragmentToUserBudgetDetailsFragment()
                else
                    RegisterScreenFragmentDirections.actionRegisterScreenFragmentToMainScreenFragment()
            }

            NavHostFragment.findNavController(fragment).navigate(action)
        }
    }
}