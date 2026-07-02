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
import com.example.bookflow.databinding.ActivityRegisterBinding
import com.example.bookflow.utils.LanguageManager
import com.example.bookflow.utils.SystemBarInsets

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.registerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.registerToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        SystemBarInsets.apply(this, binding.registerToolbar, binding.registerScrollView)

        binding.registerButton.setOnClickListener { attemptRegistration() }
        binding.openLoginButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.auth_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressedDispatcher.onBackPressed()
            true
        }
        R.id.action_language -> {
            LanguageManager.showLanguageDialog(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun attemptRegistration() {
        clearErrors()
        val name = binding.registerNameEditText.text?.toString().orEmpty().trim()
        val email = binding.registerEmailEditText.text?.toString().orEmpty().trim()
        val password = binding.registerPasswordEditText.text?.toString().orEmpty()
        val confirmation = binding.registerConfirmPasswordEditText.text?.toString().orEmpty()

        var isValid = true
        if (name.length < 2) {
            binding.registerNameLayout.error = getString(R.string.invalid_name)
            isValid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registerEmailLayout.error = getString(R.string.invalid_email)
            isValid = false
        }
        if (password.length < 6) {
            binding.registerPasswordLayout.error = getString(R.string.password_too_short)
            isValid = false
        }
        if (confirmation != password) {
            binding.registerConfirmPasswordLayout.error = getString(R.string.passwords_do_not_match)
            isValid = false
        }
        if (!isValid) return

        AuthPreferences.register(this, name, email, password)
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun clearErrors() {
        binding.registerNameLayout.error = null
        binding.registerEmailLayout.error = null
        binding.registerPasswordLayout.error = null
        binding.registerConfirmPasswordLayout.error = null
    }
}
