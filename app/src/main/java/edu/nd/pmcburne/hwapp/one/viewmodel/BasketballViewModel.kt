package edu.nd.pmcburne.hwapp.one.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.Game
import edu.nd.pmcburne.hwapp.one.repository.BasketballRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BasketballViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BasketballRepository(application)
    
    private val _selectedDate = MutableStateFlow(getTodayDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()
    
    private val _selectedGender = MutableStateFlow("men")
    val selectedGender: StateFlow<String> = _selectedGender.asStateFlow()
    
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadGames()
    }
    
    fun setDate(date: String) {
        _selectedDate.value = date
        loadGames()
    }
    
    fun toggleGender() {
        _selectedGender.value = if (_selectedGender.value == "men") "women" else "men"
        loadGames()
    }
    
    fun refresh() {
        loadGames()
    }
    
    private fun loadGames() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val gamesList = repository.getGames(_selectedDate.value, _selectedGender.value)
                _games.value = gamesList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
