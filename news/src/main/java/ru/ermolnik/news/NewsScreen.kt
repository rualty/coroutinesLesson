package ru.ermolnik.news

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.mts.data.news.repository.News

@Composable
fun NewsScreen(viewModel: NewsViewModel) {
    val state = viewModel.state.collectAsState()
    Log.d("rualty", "screen: $state")
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.value) {
            is NewsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }
            is NewsState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "Опс что то пошло не так, проверьте настройки интернета и попробуйте снова:",
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = (state.value as NewsState.Error).throwable.toString(),
                        color = Color.Gray,
                        modifier = Modifier
                            .wrapContentSize(),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.refresh() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Обновить",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            is NewsState.Content -> {
                LazyColumn {
                    (state.value as? NewsState.Content)?.newsList?.forEach {
                        item(key = it.id) {
                            drawNews(news = it)
                        }
                    }
                }
                Button(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "Обновить",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

}

@Composable
fun drawNews(news: News) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(8.dp)
        .border(BorderStroke(1.dp, Color.Gray))
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Text(
                text = news.name,
                modifier = Modifier.wrapContentSize(),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Text(
                text = news.description,
                modifier = Modifier.wrapContentSize(),
                textAlign = TextAlign.Left,
                color = Color.Gray
            )
        }
    }
}
