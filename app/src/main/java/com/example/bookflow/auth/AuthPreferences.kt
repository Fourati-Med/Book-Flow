package com.example.bookflow.auth

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

/**
 * Authentification locale destinée à la démonstration du projet.
 * Le mot de passe n'est jamais enregistré en clair : seul un hash salé est conservé.
 */
object AuthPreferences {

    private const val PREFS_NAME = "book_flow_auth"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD_HASH = "password_hash"
    private const val KEY_PASSWORD_SALT = "password_salt"
    private const val KEY_LOGGED_IN = "logged_in"

    fun register(context: Context, name: String, email: String, password: String) {
        val normalizedEmail = email.trim().lowercase(Locale.ROOT)
        val saltBytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val passwordHash = hashPassword(password, saltBytes)

        preferences(context).edit()
            .putString(KEY_NAME, name.trim())
            .putString(KEY_EMAIL, normalizedEmail)
            .putString(KEY_PASSWORD_SALT, salt)
            .putString(KEY_PASSWORD_HASH, passwordHash)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun login(context: Context, email: String, password: String): Boolean {
        val prefs = preferences(context)
        val savedEmail = prefs.getString(KEY_EMAIL, null) ?: return false
        val savedSalt = prefs.getString(KEY_PASSWORD_SALT, null) ?: return false
        val savedHash = prefs.getString(KEY_PASSWORD_HASH, null) ?: return false
        val normalizedEmail = email.trim().lowercase(Locale.ROOT)
        val saltBytes = Base64.decode(savedSalt, Base64.NO_WRAP)
        val candidateHash = hashPassword(password, saltBytes)

        val matches = savedEmail == normalizedEmail &&
                MessageDigest.isEqual(
                    savedHash.toByteArray(Charsets.UTF_8),
                    candidateHash.toByteArray(Charsets.UTF_8)
                )

        if (matches) {
            prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
        }
        return matches
    }

    fun hasAccount(context: Context): Boolean =
        preferences(context).contains(KEY_EMAIL)

    fun isLoggedIn(context: Context): Boolean =
        preferences(context).getBoolean(KEY_LOGGED_IN, false)

    fun logout(context: Context) {
        preferences(context).edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun displayName(context: Context): String =
        preferences(context).getString(KEY_NAME, "").orEmpty()

    private fun preferences(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun hashPassword(password: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}