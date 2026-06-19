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
import kotlinx.coroutines.launch
import org.amanahquran.app.core.database.AmanahContentDatabaseProvider
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

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = AmanahContentDatabaseProvider.getDatabase(context)
                return TrustCenterViewModel(
                    trustCenterRepository = trustCenterRepository(
                        context = context,
                        contentSourceDao = database.contentSourceDao(),
                        contentValidationDao = database.contentValidationDao(),
                    ),
                ) as T
            }
        }
    }
}
