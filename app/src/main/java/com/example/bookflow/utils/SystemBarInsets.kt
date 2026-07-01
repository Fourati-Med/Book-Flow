package com.example.bookflow.utils

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.color.MaterialColors

object SystemBarInsets {

    /**
     * Place correctement la barre d'outils sous la barre d'état Android 15+
     * et protège le bas de l'écran de la barre de navigation système.
     */
    fun apply(
        activity: AppCompatActivity,
        topInsetView: View,
        bottomInsetView: View? = null
    ) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        activity.window.statusBarColor = Color.TRANSPARENT
        activity.window.navigationBarColor = Color.TRANSPARENT

        val controller = WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView
        )
        val topColor = MaterialColors.getColor(
            topInsetView,
            com.google.android.material.R.attr.colorPrimary
        )
        controller.isAppearanceLightStatusBars = ColorUtils.calculateLuminance(topColor) > 0.5

        bottomInsetView?.let { bottomView ->
            val bottomColor = MaterialColors.getColor(
                bottomView,
                com.google.android.material.R.attr.colorSurface
            )
            controller.isAppearanceLightNavigationBars =
                ColorUtils.calculateLuminance(bottomColor) > 0.5
        }

        val initialTopPadding = topInsetView.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(topInsetView) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = initialTopPadding + statusBar.top)
            insets
        }

        bottomInsetView?.let { target ->
            val initialBottomPadding = target.paddingBottom
            ViewCompat.setOnApplyWindowInsetsListener(target) { view, insets ->
                val navigationBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                view.updatePadding(bottom = initialBottomPadding + navigationBar.bottom)
                insets
            }
        }

        ViewCompat.requestApplyInsets(topInsetView)
        bottomInsetView?.let { ViewCompat.requestApplyInsets(it) }
    }
}