package com.example.shoutless

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val text: StateFlow<String> = savedStateHandle.getStateFlow(TEXT_KEY, "")

    fun onTextChange(newText: String) {
        savedStateHandle[TEXT_KEY] = newText
    }

    companion object {
        private const val TEXT_KEY = "main_text"
    }
}
