package com.example.dealwithexpenses.authentication

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment

class GuestLogin {

    companion object {
        private lateinit var sharedPreferences: SharedPreferences

        fun login(fragment: Fragment) {
            sharedPreferences =
                fragment.requireActivity()!!.getSharedPreferences("user_auth", Context.MODE_PRIVATE)

            sharedPreferences.edit().putString("user_id", "abcdefghijklmnopqrstuvwxyz12").apply()
//            sharedPreferences.edit().putString("user_name", "Guest").apply()
//            sharedPreferences.edit().putString("user_email", "guest@makemybudget.com").apply()

            sharedPreferences.edit().putBoolean("isGuest", true).apply()
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
            sharedPreferences.edit().putBoolean("isRegistered", true).apply()
            Navigate.action(fragment)
        }
    }

}