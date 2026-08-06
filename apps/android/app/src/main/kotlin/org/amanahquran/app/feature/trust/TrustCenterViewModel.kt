package org.amanahquran.app.feature.trust

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.repository.TrustCenterRepository
import org.amanahquran.app.core.repository.TrustCenterUiState
import org.amanahquran.app.core.repository.trustCenterRepository

class TrustCenterViewModel(
    private val trustCenterRepository: TrustCenterRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TrustCenterUiState())
    val uiState: StateFlow<TrustCenterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            _uiState.value = trustCenterRepository.loadTrustCenterUiState()
        }
    }

    fun verifyNow() {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isVerifying = true, verificationError = null) }
            runCatching { trustCenterRepository.verifyPackagedContent() }
                .onSuccess { results ->
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            verificationResults = results,
                            verificationCheckedAt = System.currentTimeMillis(),
                            verificationError = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            verificationError = throwable.message ?: "Unable to verify packaged content offline.",
                        )
                    }
                }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TrustCenterViewModel(
                    trustCenterRepository = trustCenterRepository(context),
                ) as T
            }
        }
    }
}
