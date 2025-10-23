package org.example.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.models.ChatMessage
import org.example.models.ChatRequest
import org.example.models.ChatResponse

class ZAIClient(private val apiToken: String): AiClient {

    private val baseUrl = "https://api.z.ai/api/paas/v4"

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }

        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $apiToken")
        }
    }

    /**
     * Отправляет запрос к API чата асинхронно
     * @param chatRequest запрос с моделью, сообщениями и параметрами
     * @return ответ от API с результатами
     */
    override suspend fun sendChatRequest(chatRequest: ChatRequest): ChatResponse {
        return httpClient.post("$baseUrl/chat/completions") {
            contentType(ContentType.Application.Json)
            setBody(chatRequest)
        }.body()
    }
}
