package ru.ermolnik.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mts.data.news.repository.NewsRepository
import ru.mts.data.utils.doOnError
import ru.mts.data.utils.doOnSuccess


class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _state: MutableStateFlow<NewsState> = MutableStateFlow(NewsState.Loading)
    val state = _state.asStateFlow()

    init {
        Log.d("rualty", "vm: start")
        viewModelScope.launch {
            fetchNews()
        }
    }

    suspend fun fetchNews(isForcedUpdate: Boolean = false) {
        repository.getNews(isForcedUpdate)
            .collect {
                Log.d("rualty", "vm: $it")
                it.doOnError { error ->
                    Log.d("rualty", "vm: onError: $error")
                    _state.emit(NewsState.Error(error))
                }
                it.doOnSuccess { news ->
                    Log.d("rualty", "vm: onSuccess = $news")
                    _state.emit(NewsState.Content(news))
                }
            }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.emit(NewsState.Loading)
            fetchNews(isForcedUpdate = true)
        }
    }
}
