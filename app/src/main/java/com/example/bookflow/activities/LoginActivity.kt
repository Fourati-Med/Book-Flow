package com.example.bookflow.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.bookflow.MainActivity
import com.example.bookflow.R
import com.example.bookflow.auth.AuthPreferences
import com.example.bookflow.databinding.ActivityLoginBinding
import com.example.bookflow.utils.LanguageManager
import com.example.bookflow.utils.SystemBarInsets

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthPreferences.isLoggedIn(this)) {
            openMainScreen()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.loginToolbar)
        SystemBarInsets.apply(this, binding.loginToolbar, binding.loginScrollView)

        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.openRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.auth_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_language -> {
            LanguageManager.showLanguageDialog(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun attemptLogin() {
        clearErrors()
        val email = binding.loginEmailEditText.text?.toString().orEmpty().trim()
        val password = binding.loginPasswordEditText.text?.toString().orEmpty()

        var isValid = true
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginEmailLayout.error = getString(R.string.invalid_email)
            isValid = false
        }
        if (password.isBlank()) {
            binding.loginPasswordLayout.error = getString(R.string.password_required)
            isValid = false
        }
        if (!isValid) return

        if (!AuthPreferences.hasAccount(this)) {
            binding.loginEmailLayout.error = getString(R.string.no_local_account)
            return
        }

        if (AuthPreferences.login(this, email, password)) {
            openMainScreen()
        } else {
            binding.loginPasswordLayout.error = getString(R.string.invalid_credentials)
        }
    }

    private fun clearErrors() {
        binding.loginEmailLayout.error = null
        binding.loginPasswordLayout.error = null
    }

    private fun openMainScreen() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}
