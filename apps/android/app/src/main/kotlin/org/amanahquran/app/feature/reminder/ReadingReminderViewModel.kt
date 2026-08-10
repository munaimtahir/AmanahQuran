package org.amanahquran.app.feature.reminder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.DayOfWeek
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ReminderSettings
import org.amanahquran.app.core.repository.ReminderSettingsRepository
import org.amanahquran.app.core.repository.reminderSettingsRepository

data class ReadingReminderUiState(
    val isLoading: Boolean = true,
    val settings: ReminderSettings = ReminderSettings(),
    val notificationPermissionDenied: Boolean = false,
)

class ReadingReminderViewModel(
    private val appContext: Context,
    private val repository: ReminderSettingsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingReminderUiState())
    val uiState: StateFlow<ReadingReminderUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            repository.settings.collectLatest { settings ->
                _uiState.update { it.copy(isLoading = false, settings = settings) }
            }
        }
    }

    /** Called after the system permission dialog resolves (or immediately if none was needed). */
    fun setEnabled(enabled: Boolean, permissionGranted: Boolean) {
        viewModelScope.launch(dispatcher) {
            if (enabled && !permissionGranted) {
                _uiState.update { it.copy(notificationPermissionDenied = true) }
                return@launch
            }
            _uiState.update { it.copy(notificationPermissionDenied = false) }
            applyChange { it.copy(enabled = enabled) }
        }
    }

    fun setTime(hour: Int, minute: Int) {
        viewModelScope.launch(dispatcher) { applyChange { it.copy(hour = hour, minute = minute) } }
    }

    fun setRepeatDays(days: Set<DayOfWeek>) {
        viewModelScope.launch(dispatcher) { applyChange { it.copy(repeatDays = days) } }
    }

    fun setSmartReminderEnabled(enabled: Boolean) {
        viewModelScope.launch(dispatcher) { applyChange { it.copy(smartReminderEnabled = enabled) } }
    }

    private suspend fun applyChange(transform: (ReminderSettings) -> ReminderSettings) {
        repository.update(transform)
        ReminderScheduler.reschedule(appContext, repository.getSettings())
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val appContext = context.applicationContext
                return ReadingReminderViewModel(appContext, reminderSettingsRepository(appContext)) as T
            }
        }
    }
}
