package org.example

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.models.*
import java.util.concurrent.TimeUnit

class ZAIClient(private val apiToken: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(200, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun callModel(modelName: String, prompt: String): ModelResult {
        val startTime = System.currentTimeMillis()

        return try {
            val response = makeApiRequest(modelName, prompt)
            val responseTime = System.currentTimeMillis() - startTime
            parseResponse(modelName, response, responseTime, prompt)
        } catch (e: Exception) {
            val responseTime = System.currentTimeMillis() - startTime
            createErrorResult(modelName, responseTime, prompt, "Exception: ${e.message}")
        }
    }

    private fun makeApiRequest(modelName: String, prompt: String): String {
        val requestBody = ZAIRequest(
            model = modelName,
            messages = listOf(Message(role = "user", content = prompt)),
            max_tokens = 4096,
            temperature = 0.5,
            stream = false
        )

        val jsonBody = json.encodeToString(ZAIRequest.serializer(), requestBody)

        val request = Request.Builder()
            .url("https://api.z.ai/api/paas/v4/chat/completions")
            .addHeader("Authorization", "Bearer $apiToken")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                throw ApiException("HTTP ${response.code}: $responseBody")
            }

            return responseBody
        }
    }

    private fun parseResponse(
        modelName: String,
        responseBody: String,
        responseTime: Long,
        prompt: String
    ): ModelResult {
        val parsedResponse = try {
            json.decodeFromString<ZAIResponse>(responseBody)
        } catch (e: Exception) {
            return createErrorResult(
                modelName,
                responseTime,
                prompt,
                "Parse error: ${e.message}\nResponse: $responseBody"
            )
        }

        if (parsedResponse.error != null) {
            return createErrorResult(modelName, responseTime, prompt, parsedResponse.error)
        }

        val messageData = parsedResponse.choices?.firstOrNull()?.message
        val generatedText = messageData?.content ?: ""

        val inputTokens = parsedResponse.usage?.prompt_tokens ?: estimateTokens(prompt)
        val outputTokens = parsedResponse.usage?.completion_tokens ?: estimateTokens(generatedText)
        val totalTokens = parsedResponse.usage?.total_tokens ?: (inputTokens + outputTokens)

        return ModelResult(
            modelName = modelName,
            response = generatedText,
            responseTimeMs = responseTime,
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            totalTokens = totalTokens,
            estimatedCost = estimateCost(modelName, inputTokens, outputTokens)
        )
    }

    private fun createErrorResult(
        modelName: String,
        responseTime: Long,
        prompt: String,
        errorMessage: String
    ): ModelResult {
        val inputTokens = estimateTokens(prompt)
        return ModelResult(
            modelName = modelName,
            response = "",
            responseTimeMs = responseTime,
            inputTokens = inputTokens,
            outputTokens = 0,
            totalTokens = inputTokens,
            estimatedCost = null,
            error = errorMessage
        )
    }

    private fun estimateTokens(text: String): Int {
        return (text.length / 4).coerceAtLeast(1)
    }

    private fun estimateCost(modelName: String, inputTokens: Int, outputTokens: Int): Double? {
        return when {
            modelName.contains("gpt", ignoreCase = true) -> {
                (inputTokens * 0.0001 + outputTokens * 0.0002) / 1000
            }
            else -> null
        }
    }

    private class ApiException(message: String) : Exception(message)
}