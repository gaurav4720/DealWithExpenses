package com.example.dealwithexpenses.initialScreens

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.databinding.FragmentFrontScreenBinding

@Suppress("DEPRECATION")
class FrontScreenFragment : Fragment() {


    private lateinit var binding: FragmentFrontScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPreferences = activity?.getSharedPreferences("user_auth", 0)!!
        binding = FragmentFrontScreenBinding.inflate(inflater, container, false)
        val isRegistered: Boolean = sharedPreferences.getBoolean("isRegistered", false)
        val isLoggedIn: Boolean = sharedPreferences.getBoolean("isLoggedIn", false)
        val allCheck: Boolean = sharedPreferences.getBoolean("allCheck", false)

        var action: NavDirections = FrontScreenFragmentDirections.actionFrontScreenFragmentToLoginScreenFragment()

        val handler = Handler()
        handler.postDelayed({
            findNavController().navigate(action)
        }, 5000)

        return binding.root
    }


}