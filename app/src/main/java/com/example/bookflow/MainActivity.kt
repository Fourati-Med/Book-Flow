package com.example.bookflow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookflow.adapters.BookAdapter
import com.example.bookflow.databinding.ActivityMainBinding
import com.example.bookflow.models.Book
import com.example.bookflow.viewmodels.HomeViewModel

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Configuration De la toolbar
        setSupportActionBar(binding.toolbar)

        // Rècpérer le NavController
        val navController = findNavController(R.id.nav_host_fragment)
        // Lier la BottomNav au NavController
        binding.bottomNavigation.setupWithNavController(navController)

        // Configuration l'appBar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.libraryFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)


    }
}