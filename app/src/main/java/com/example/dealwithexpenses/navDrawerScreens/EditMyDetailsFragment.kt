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
import com.example.dealwithexpenses.databinding.FragmentEditMyDetailsBinding

class EditMyDetailsFragment : Fragment() {
    private lateinit var binding: FragmentEditMyDetailsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initialising binding and shared preferences
        binding = FragmentEditMyDetailsBinding.inflate(inflater, container, false)
        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        binding.usernameE.setText(sharedPreferences.getString("username", ""))
        binding.budgetE.setText(sharedPreferences.getString("budget", ""))
        binding.incomeE.setText(sharedPreferences.getString("income", ""))

        //if the user clicks the button save
        binding.saveButtonE.setOnClickListener {

            //if there is nothing written in username column
            if (binding.usernameE.text.isEmpty()) {
                //the warning should be visible about it being empty
                binding.warningUsernameE.visibility =
                    View.VISIBLE
            }

            //if there is nothing written in budget column
            else if (binding.budgetE.text.isEmpty()) {
                //the warning should be visible about it being empty
                binding.warningBudgetE.visibility = View.VISIBLE
            } else {
                //both the warnings should no more be visible, as both the fields are filled.
                binding.warningUsernameE.visibility = View.GONE
                binding.warningBudgetE.visibility = View.GONE

                //feeding the data of username and budget in the shared preferences
                val editor = sharedPreferences.edit()
                editor.putString("username", binding.usernameE.text.toString())
                editor.putString("budget", binding.budgetE.text.toString())

                //default value of income is 0 if not filled
                //it is assumed that the user isn't earning
                if (binding.incomeE.text.isEmpty()) {
                    editor.putString("income", "0")
                } else {
                    //if filled, data is fed into the shared preferences
                    editor.putString("income", binding.incomeE.text.toString())
                }

                //after all changes, editor is applied to make a permanent change in shared preferences
                editor.apply()

                //Then the user is directed to the main screen fragment
                findNavController().navigate(EditMyDetailsFragmentDirections.actionEditMyDetailsFragmentToMainScreenFragment(0))
            }
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(EditMyDetailsFragmentDirections.actionEditMyDetailsFragmentToMainScreenFragment(0))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        return binding.root
    }
}