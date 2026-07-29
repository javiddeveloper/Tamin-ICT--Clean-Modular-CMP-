package com.tamin.taminhamrah.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModelMVI<I, S, E> : ViewModel() {

    protected abstract fun createInitialState(): S

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<S> = _state.asStateFlow()

    private val _events = MutableSharedFlow<E>()
    val events: SharedFlow<E> = _events.asSharedFlow()

    private val intents = Channel<I>(0)

    init {
        viewModelScope.launch {
            intents.consumeAsFlow().collectLatest { intent ->
                handleIntent(intent)
            }
        }
    }

    fun processIntent(intent: I) {
        intents.trySend(intent)
    }

    protected fun setState(reducer: S.() -> S) {
        _state.value = _state.value.reducer()
    }

    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    protected abstract suspend fun handleIntent(intent: I)
}

