package com.example.futbolapp.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private fun loadThemeMode(): ThemeMode {
        val savedMode = prefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        return ThemeMode.valueOf(savedMode ?: ThemeMode.SYSTEM.name)
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }
}
