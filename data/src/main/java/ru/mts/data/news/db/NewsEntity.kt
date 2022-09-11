package ru.mts.data.news.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mts.data.news.repository.News

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String
) {
    companion object {
        fun fromNews(news: News) = NewsEntity(
            news.id,
            news.name,
            news.description
        )
    }
}

fun NewsEntity.toDomain() = News(this.id, this.name, this.description)