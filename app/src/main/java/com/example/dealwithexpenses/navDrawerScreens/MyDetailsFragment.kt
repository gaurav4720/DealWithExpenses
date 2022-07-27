package com.example.dealwithexpenses.navDrawerScreens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentMyDetailsBinding
import com.example.dealwithexpenses.mainScreen.transactionLibrary.TransactionDetailFragmentDirections

class MyDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyDetailsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyDetailsBinding.inflate(inflater, container, false)
        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        binding.usernameD.text = sharedPreferences.getString("username", "")
        binding.budgetD.text= sharedPreferences.getString("budget", "")
        binding.incomeD.text= sharedPreferences.getString("income", "")
        if(sharedPreferences.getString("income", "") == "0"){
            "Not Updated Yet".also { binding.incomeD.text = it }
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        return binding.root
    }

}