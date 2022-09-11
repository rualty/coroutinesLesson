package ru.mts.data.news.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import ru.mts.data.news.db.NewsLocalDataSource
import ru.mts.data.news.db.toDomain
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
    suspend fun getNews(isForcedUpdate: Boolean = false): Flow<Result<List<News>, Throwable>> {
        Log.d("rualty", "repo: getNews()")
        return flow {
            if (isForcedUpdate) {
                getFromRemote(isForcedUpdate, this)
            } else {
                getFromDb(this)
            }
        }
    }

    suspend fun getFromDb(flow: FlowCollector<Result<List<News>, Throwable>>) {
        newsLocalDataSource.getNews()
            .also {
                Log.d("rualty", "repo: db.getNews(): $it")
            }
            .mapSuccess { it.map { it.toDomain() } }
            .doOnError { getFromRemote(false, flow) }
            .doOnSuccess {
                if (it.isNotEmpty()) flow.emit(Result.Success(it))
                else getFromRemote(false, flow)
            }

    }

    suspend fun getFromRemote(isForcedUpdate: Boolean, flow: FlowCollector<Result<List<News>, Throwable>>) {
        newsRemoteDataSource.getNews(isForcedUpdate)
            .also {
                Log.d("rualty", "repo: remote.getNews(): $it")
            }
            .mapSuccess { it.newsList.map { it.toDomain() } }
            .doOnError { flow.emit(Result.Error(it)) }
            .doOnSuccess {
                flow.emit(Result.Success(it))
                newsLocalDataSource.saveNews(it)
            }

    }
}
