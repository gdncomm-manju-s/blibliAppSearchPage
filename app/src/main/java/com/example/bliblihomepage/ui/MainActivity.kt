package com.example.bliblihomepage.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.bliblihomepage.R
import com.example.bliblihomepage.util.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            val navController = navHostFragment.navController

            val inflater = navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_graph)

            val user = SharedPrefManager.getEmail(this)

            graph.setStartDestination(
                if (user.isNullOrEmpty()) R.id.loginFragment else R.id.cartFragment
            )

            navController.graph = graph
        }
    }
}



