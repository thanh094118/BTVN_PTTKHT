package com.example.myapplication.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class OnboardingState { Page1, Page2, Page3 }

class OnboardingViewModel : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState.Page1)
    val state = _state.asStateFlow()

    fun nextPage() {
        _state.value = when (_state.value) {
            OnboardingState.Page1 -> OnboardingState.Page2
            OnboardingState.Page2 -> OnboardingState.Page3
            OnboardingState.Page3 -> OnboardingState.Page1
        }
    }
}
