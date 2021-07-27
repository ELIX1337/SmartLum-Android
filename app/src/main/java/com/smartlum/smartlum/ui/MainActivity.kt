package com.smartlum.smartlum.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartlum.smartlum.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController: NavController         = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        NavigationUI.setupWithNavController(navigationView, navController)
    }
}