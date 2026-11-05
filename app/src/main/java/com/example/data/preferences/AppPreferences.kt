package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.i18n.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pastoral_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _currency = MutableStateFlow(prefs.getString("currency", "MGA") ?: "MGA")
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(prefs.getBoolean("pin_enabled", false))
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private fun loadLanguage(): AppLanguage {
        val code = prefs.getString("language_code", AppLanguage.MALAGASY.code)
        return AppLanguage.entries.find { it.code == code } ?: AppLanguage.MALAGASY
    }

    private fun loadThemeMode(): AppThemeMode {
        val name = prefs.getString("theme_mode", AppThemeMode.LIGHT.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.LIGHT.name)
        } catch (_: Exception) {
            AppThemeMode.LIGHT
        }
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("language_code", lang.code).apply()
        _language.value = lang
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun setCurrency(curr: String) {
        prefs.edit().putString("currency", curr).apply()
        _currency.value = curr
    }

    fun setPinCode(pin: String?) {
        if (pin.isNullOrBlank()) {
            prefs.edit().remove("pin_code").putBoolean("pin_enabled", false).apply()
            _isPinEnabled.value = false
        } else {
            prefs.edit().putString("pin_code", pin).putBoolean("pin_enabled", true).apply()
            _isPinEnabled.value = true
        }
    }

    fun getPinCode(): String? = prefs.getString("pin_code", null)

    fun checkPin(pin: String): Boolean {
        val stored = getPinCode()
        return stored == null || stored == pin
    }
}
