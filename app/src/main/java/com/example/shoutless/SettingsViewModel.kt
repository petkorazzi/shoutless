package com.example.shoutless

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

// Represents the entire UI state for the Settings screen
data class SettingsUiState(
    val defaultFontSize: Float = 30f,
    val maxFontSize: Float = 150f,
    val forceBrightness: Boolean = false,
    val forceLandscape: Boolean = false,
    // Clapback labels
    val clapback1Label: String = "yeah",
    val clapback2Label: String = "nah",
    val clapback3Label: String = "ty",
    val clapback4Label: String = "brb",
    // Clapback hidden messages
    val clapback1Hidden: String = "yeah",
    val clapback2Hidden: String = "nah",
    val clapback3Hidden: String = "thank you",
    val clapback4Hidden: String = "be right back",
)

class SettingsViewModel(
    private val application: Application,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(loadSettingsFromPrefs())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    private val _randomTagline = MutableStateFlow("")
    val randomTagline: StateFlow<String> = _randomTagline.asStateFlow()

    init {
        loadRandomTagline()
    }

    private fun loadSettingsFromPrefs(): SettingsUiState {
        return SettingsUiState(
            defaultFontSize = sharedPreferences.getInt(SettingsActivity.KEY_DEFAULT_FONT_SIZE, 30).toFloat(),
            maxFontSize = sharedPreferences.getInt(SettingsActivity.KEY_MAX_FONT_SIZE, 150).toFloat(),
            forceBrightness = sharedPreferences.getBoolean(SettingsActivity.KEY_FORCE_BRIGHTNESS, false),
            forceLandscape = sharedPreferences.getBoolean(SettingsActivity.KEY_FORCE_LANDSCAPE, false),
            clapback1Label = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK1_LABEL, "yeah") ?: "yeah",
            clapback1Hidden = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK1_HIDDEN, "yeah") ?: "yeah",
            clapback2Label = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK2_LABEL, "nah") ?: "nah",
            clapback2Hidden = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK2_HIDDEN, "nah") ?: "nah",
            clapback3Label = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK3_LABEL, "ty") ?: "ty",
            clapback3Hidden = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK3_HIDDEN, "thank you") ?: "thank you",
            clapback4Label = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK4_LABEL, "brb") ?: "brb",
            clapback4Hidden = sharedPreferences.getString(SettingsActivity.KEY_CLAPBACK4_HIDDEN, "be right back") ?: "be right back",
        )
    }

    private fun loadRandomTagline() {
        val taglinesArray = application.resources.getStringArray(R.array.settings_taglines)
        if (taglinesArray.isNotEmpty()) {
            _randomTagline.value = taglinesArray[Random.nextInt(taglinesArray.size)]
        } else {
            _randomTagline.value = ""
        }
    }

    fun updateDefaultFontSize(newSize: Float) {
        viewModelScope.launch {
            val snappedValue = ((newSize / 5).roundToInt() * 5f).coerceIn(10f, 50f)
            _uiState.value = _uiState.value.copy(defaultFontSize = snappedValue)

            sharedPreferences.edit {
                putInt(SettingsActivity.KEY_DEFAULT_FONT_SIZE, snappedValue.roundToInt())
                if (snappedValue > _uiState.value.maxFontSize) {
                    _uiState.value = _uiState.value.copy(maxFontSize = snappedValue)
                    putInt(SettingsActivity.KEY_MAX_FONT_SIZE, snappedValue.roundToInt())
                }
            }
        }
    }

    fun updateMaxFontSize(newSize: Float) {
        viewModelScope.launch {
            val snappedValue = ((newSize / 5).roundToInt() * 5f).coerceIn(_uiState.value.defaultFontSize, 150f)
            _uiState.value = _uiState.value.copy(maxFontSize = snappedValue)
            sharedPreferences.edit { putInt(SettingsActivity.KEY_MAX_FONT_SIZE, snappedValue.roundToInt()) }
        }
    }

    fun updateForceBrightness(force: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(forceBrightness = force)
            sharedPreferences.edit { putBoolean(SettingsActivity.KEY_FORCE_BRIGHTNESS, force) }
        }
    }

    fun updateForceLandscape(force: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(forceLandscape = force)
            sharedPreferences.edit { putBoolean(SettingsActivity.KEY_FORCE_LANDSCAPE, force) }
        }
    }

    fun updateClapback1Label(label: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback1Label = label)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK1_LABEL, label) }
        }
    }

    fun updateClapback1Hidden(hidden: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback1Hidden = hidden)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK1_HIDDEN, hidden) }
        }
    }

    fun updateClapback2Label(label: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback2Label = label)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK2_LABEL, label) }
        }
    }

    fun updateClapback2Hidden(hidden: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback2Hidden = hidden)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK2_HIDDEN, hidden) }
        }
    }

    fun updateClapback3Label(label: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback3Label = label)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK3_LABEL, label) }
        }
    }

    fun updateClapback3Hidden(hidden: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback3Hidden = hidden)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK3_HIDDEN, hidden) }
        }
    }

    fun updateClapback4Label(label: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback4Label = label)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK4_LABEL, label) }
        }
    }

    fun updateClapback4Hidden(hidden: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(clapback4Hidden = hidden)
            sharedPreferences.edit { putString(SettingsActivity.KEY_CLAPBACK4_HIDDEN, hidden) }
        }
    }

    class Factory(private val application: Application, private val prefs: SharedPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(application, prefs) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
