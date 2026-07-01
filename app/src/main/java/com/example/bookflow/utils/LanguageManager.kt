package com.example.bookflow.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.bookflow.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object LanguageManager {

    fun showLanguageDialog(activity: AppCompatActivity) {
        val languages = arrayOf(
            activity.getString(R.string.language_french),
            activity.getString(R.string.language_english)
        )
        val currentTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val selectedIndex = if (currentTags.startsWith("en")) 1 else 0

        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(languages, selectedIndex) { dialog, index ->
                val languageTag = if (index == 1) "en" else "fr"
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(languageTag)
                )
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}