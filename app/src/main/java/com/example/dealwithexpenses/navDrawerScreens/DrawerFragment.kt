package com.example.dealwithexpenses.navDrawerScreens

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import com.google.android.material.navigation.NavigationView

class DrawerFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!
        return inflater.inflate(R.layout.fragment_drawer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navController= Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
        val drawerLayout= requireActivity().findViewById<DrawerLayout>(R.id.fragment_drawer)
        val toolbar= requireActivity().findViewById<Toolbar>(R.id.toolbar2)
        val navigationView= requireActivity().findViewById<NavigationView>(R.id.navigation_view)

        NavigationUI.setupWithNavController(toolbar,navController,drawerLayout)
        navigationView.setupWithNavController(navController)

        val profileName= requireActivity().findViewById<TextView>(R.id.profileName)
        profileName.text= sharedPreferences.getString("username","")

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawer(navigationView)
            when(it.itemId){
                R.id.newTransaction -> {
                    //findNavController().navigate(DrawerFragmentDirections.actionDrawerFragmentToAddOrEditTransactionFragment(0,4))
                    Toast.makeText(requireContext(),"New Transaction",Toast.LENGTH_SHORT).show()
                    true
                }
//                R.id.myDetails -> {
//                    findNavController().navigate(DrawerFragmentDirections.actionDrawerFragmentToMyDetailsFragment())
//                    true
//                }
//                R.id.editMyDetail -> {
//                    findNavController().navigate(DrawerFragmentDirections.actionDrawerFragmentToEditMyDetailsFragment())
//                    true
//                }
//                R.id.aboutUs -> {
//                    findNavController().navigate(DrawerFragmentDirections.actionDrawerFragmentToAboutUsFragment())
//                    true
//                }
//                R.id.logout -> {
//                    val dialog= AlertDialog.Builder(requireContext())
//                        .setTitle("Logout")
//                        .setMessage("Are you sure you want to logout?")
//                        .setPositiveButton("Yes") { _, _ ->
//                            sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
//                            findNavController().navigate(DrawerFragmentDirections.actionDrawerFragmentToLoginScreenFragment())
//                        }
//
//                    true
//                }
                else -> false
            }
        }

    }
}


