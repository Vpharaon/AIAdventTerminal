package org.example

import org.example.models.ModelResult
import java.util.Properties
import kotlin.system.exitProcess

fun loadApiToken(): String? {
    return try {
        val properties = Properties()
        val inputStream = object {}.javaClass.getResourceAsStream("/config.properties")

        if (inputStream != null) {
            properties.load(inputStream)
            properties.getProperty("huggingface.api.token")
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

fun main() {
    println("=".repeat(80))
    println("Сравнение моделей HuggingFace")
    println("=".repeat(80))
    println()

    // Получаем API токен из конфигурации
    val apiToken = loadApiToken()
    if (apiToken.isNullOrBlank()) {
        println("ОШИБКА: API токен не найден!")
        println("Создайте файл local.properties с параметром huggingface.api.token")
        println("Получите токен на: https://huggingface.co/settings/tokens")
        exitProcess(1)
    }

    // Запрашиваем запрос от пользователя
    println("Введите ваш запрос для моделей:")
    val userPrompt = readLine()?.trim()
    if (userPrompt.isNullOrBlank()) {
        println("ОШИБКА: Запрос не может быть пустым")
        exitProcess(1)
    }

    println()
    println("Запрос: $userPrompt")
    println()

    // Список моделей для сравнения (начало, середина, конец списка)
    // Используем открытые модели с поддержкой chat формата
    val models = listOf(
        "deepseek-ai/DeepSeek-V3.2-Exp:novita",
        "Qwen/QwQ-32B:nscale",
        "meta-llama/Meta-Llama-3-8B-Instruct",
        "mistralai/Mistral-7B-Instruct-v0.2:featherless-ai",
        "google/gemma-2-2b-it:nebius",
    )

    val client = HuggingFaceClient(apiToken)
    val results = mutableListOf<ModelResult>()

    // Вызываем каждую модель
    for (model in models) {
        println("-".repeat(80))
        println("Вызов модели: $model")
        println("-".repeat(80))

        val result = client.callModel(model, userPrompt)
        results.add(result)

        if (result.error != null) {
            println("ОШИБКА: ${result.error}")
        } else {
            println("Ответ получен за ${result.responseTimeMs} мс")
        }
        println()
    }

    // Выводим сравнительную таблицу
    printComparisonTable(results)

    // Выводим детальные ответы
    printDetailedResponses(results)
}

fun printComparisonTable(results: List<ModelResult>) {
    println("=".repeat(80))
    println("СРАВНИТЕЛЬНАЯ ТАБЛИЦА")
    println("=".repeat(80))
    println()

    // Заголовок таблицы
    println(String.format("%-35s | %10s | %10s | %10s | %10s",
        "Модель", "Время (мс)", "Вх.токены", "Вых.токены", "Стоимость"))
    println("-".repeat(80))

    // Данные
    for (result in results) {
        val modelShortName = result.modelName.split("/").lastOrNull() ?: result.modelName
        val cost = result.estimatedCost?.let { String.format("$%.6f", it) } ?: "Бесплатно"

        println(String.format("%-35s | %10d | %10d | %10d | %10s",
            modelShortName.take(35),
            result.responseTimeMs,
            result.inputTokens,
            result.outputTokens,
            cost
        ))
    }
    println()

    // Статистика
    val successfulResults = results.filter { it.error == null }
    if (successfulResults.isNotEmpty()) {
        val fastestModel = successfulResults.minByOrNull { it.responseTimeMs }
        val slowestModel = successfulResults.maxByOrNull { it.responseTimeMs }
        val mostTokens = successfulResults.maxByOrNull { it.totalTokens }

        println("СТАТИСТИКА:")
        println("  Самая быстрая модель: ${fastestModel?.modelName} (${fastestModel?.responseTimeMs} мс)")
        println("  Самая медленная модель: ${slowestModel?.modelName} (${slowestModel?.responseTimeMs} мс)")
        println("  Больше всего токенов: ${mostTokens?.modelName} (${mostTokens?.totalTokens} токенов)")

        val totalCost = successfulResults.mapNotNull { it.estimatedCost }.sum()
        if (totalCost > 0) {
            println("  Общая стоимость запросов: $${String.format("%.6f", totalCost)}")
        }
    }
    println()
}

fun printDetailedResponses(results: List<ModelResult>) {
    println("=".repeat(80))
    println("ДЕТАЛЬНЫЕ ОТВЕТЫ МОДЕЛЕЙ")
    println("=".repeat(80))
    println()

    for (result in results) {
        println("-".repeat(80))
        println("Модель: ${result.modelName}")
        println("-".repeat(80))
        println("Время ответа: ${result.responseTimeMs} мс")
        println("Токены: ${result.inputTokens} (вход) + ${result.outputTokens} (выход) = ${result.totalTokens} (всего)")

        if (result.estimatedCost != null) {
            println("Стоимость: $${String.format("%.6f", result.estimatedCost)}")
        } else {
            println("Стоимость: Бесплатно")
        }

        if (result.error != null) {
            println()
            println("❌ Ошибка: ${result.error}")
        } else {
            println()
            println("Ответ:")
            println(result.response)
        }
        println()
    }
}