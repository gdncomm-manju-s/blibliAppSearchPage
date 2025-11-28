package com.example.bliblihomepage.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.example.bliblihomepage.R
import com.example.bliblihomepage.util.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // Only set startDestination on first app launch
        if (savedInstanceState == null) {
            val graph = navController.navInflater.inflate(R.navigation.nav_graph)

            val user = SharedPrefManager.getEmail(this)

            graph.setStartDestination(
                if (user.isNullOrEmpty()) R.id.loginFragment else R.id.cartFragment
            )

            navController.graph = graph
        }
    }
}
