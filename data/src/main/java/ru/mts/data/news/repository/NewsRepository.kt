package ru.mts.data.news.repository

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.mts.data.news.db.NewsLocalDataSource
import ru.mts.data.news.remote.NewsRemoteDataSource
import ru.mts.data.news.remote.toDomain
import ru.mts.data.utils.Result
import ru.mts.data.utils.doOnError
import ru.mts.data.utils.doOnSuccess
import ru.mts.data.utils.mapSuccess

class NewsRepository(
    private val newsLocalDataSource: NewsLocalDataSource,
    private val newsRemoteDataSource: NewsRemoteDataSource
) {
    suspend fun getNews(): Flow<Result<News, Throwable>> {
        Log.d("rualty", "repo: getNews()")
        return flow {
            newsRemoteDataSource.getNews()
                .also {
                    Log.d("rualty", "repo: getNews(): $it")
                }
                .mapSuccess { it.toDomain() }
                .doOnError { emit(Result.Error(it)) }
                .doOnSuccess {
                    emit(Result.Success(it))
                }
        }
    }

}
