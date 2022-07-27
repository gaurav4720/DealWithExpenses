package com.example.dealwithexpenses.navDrawerScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {
    private lateinit var binding: FragmentAboutUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        ("This is an app to help you remember about all your transactions. \n" +
                "As soon as you log in, you are asked to enter your username, budget and income. \n" +
                "After it, you are directed to a screen where you can have a view on your profits and transactions according to the various pie charts and bar graphs. \n" +
                "In recent transactions tab, you can view your transactions in detail, where as in history we can see the transactions according to different months and years. \n" +
                "In addition, a plus button is included every where in the app, which when clicked will direct you to a screen where you can add different transactions you have completed or you have to complete. "
        ).also { binding.aboutApp.text = it }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        return binding.root
    }
}