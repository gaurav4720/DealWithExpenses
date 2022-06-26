package com.example.dealwithexpenses.initialScreens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentUserBudgetDetailsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserBudgetDetailsFragment : Fragment() {
    private lateinit var binding: FragmentUserBudgetDetailsBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentUserBudgetDetailsBinding.inflate(inflater,container,false)
        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        binding.saveButton.setOnClickListener {
            if(binding.username.text.isEmpty()){
                binding.warningUsername.visibility= View.VISIBLE
            }
            else if(binding.budget.text.isEmpty()){
                binding.warningBudget.visibility= View.VISIBLE
            }
            else{
                binding.warningUsername.visibility= View.GONE
                binding.warningBudget.visibility= View.GONE
                val editor= sharedPreferences.edit()
                editor.putString("username",binding.username.text.toString())
                editor.putString("budget",binding.budget.text.toString())
                if(binding.income.text.isEmpty()){
                    editor.putString("income","0")
                }
                else{
                    editor.putString("income",binding.income.text.toString())
                }
                editor.putBoolean("allCheck",true)
                editor.apply()
                findNavController().navigate(UserBudgetDetailsFragmentDirections.actionUserBudgetDetailsFragmentToMainScreenFragment())
            }
        }
        return binding.root
    }

        fun SharedPreferences.saveHashMap(key: String, obj: HashMap<Int, Int>) {
            val editor = this.edit()
            val gson = Gson()
            val json = gson.toJson(obj)
            editor.putString(key, json)
            editor.apply()
        }

        fun SharedPreferences.getHashMap(key: String): HashMap<Int, Int> {
            val gson = Gson()
            val json = this.getString(key, "")
            val type = object : TypeToken<HashMap<Int, Int>>() {}.type
            return gson.fromJson(json, type)
        }
}