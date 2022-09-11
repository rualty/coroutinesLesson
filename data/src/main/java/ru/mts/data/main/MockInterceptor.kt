package ru.mts.data.main

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.asResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import ru.mts.data.news.remote.NewsDto
import ru.mts.data.utils.ResourcesReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL

class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri().toString()
        val bodyString = chain.request().body?.string()
        Log.d("rualty", "interceptor: ${chain.request().body?.string()}")
        val body =
            MockResponse().setBodyFromFile(
                when {
                    uri.endsWith("api/v1/sample") ->
                        if (bodyString.equals("{\"isForcedUpdate\":true}")) "assets/news_forced.json"
                        else "assets/news.json"
                    else -> ""
                }

            ).getBody()
            ?.asResponseBody("application/json".toMediaTypeOrNull())

        return chain.proceed(chain.request())
            .newBuilder()
            .code(200)
            .protocol(Protocol.HTTP_2)
            .body(
                body
            )
            .addHeader("content-type", "application/json")
            .build()
    }
}

fun RequestBody?.string(): String {
    try {
        val buffer = Buffer()
        if (this != null)
            this.writeTo(buffer)
        else
            return ""
        return buffer.readUtf8()
    } catch (e: IOException) {
        return "cannot convert JsonBody To String"
    }
}

fun MockResponse.setBodyFromFile(filName: String): MockResponse {
    val text = ResourcesReader.readText(filName)
    setBody(text)
    return this
}

