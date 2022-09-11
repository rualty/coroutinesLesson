package ru.mts.data.news.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import ru.mts.data.news.repository.News


class NewsDto {
    @Parcelize
    data class Request(@SerializedName("isForcedUpdate") val isForcedUpdate: Boolean) : Parcelable

    @Parcelize
    data class Response(
        @SerializedName("newsList") val newsList: List<News>
    ) : Parcelable

    @Parcelize
    data class News(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String
    ) : Parcelable
}

internal fun NewsDto.News.toDomain(): News {
    return News(this.id, this.name, this.description)
}