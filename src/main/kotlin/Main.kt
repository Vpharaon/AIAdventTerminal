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
            properties.getProperty("zai.api.token")
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

fun main() {
    println("=".repeat(80))
    println()

    // Получаем API токен из конфигурации
    val apiToken = loadApiToken()
    if (apiToken.isNullOrBlank()) {
        println("ОШИБКА: API токен не найден!")
        println("Создайте файл config.properties с параметром zai.api.token")
        println("Получите токен на: https://z.ai")
        exitProcess(1)
    }

    // Список запросов для тестирования
    val prompts = listOf(
        "Реши уравнение 2x + 6 = 14",
//        "5y - 3 = 2y + 9",
        "Если у Маши 3 яблока, а у Пети в 4 раза больше — сколько у Пети?",
//        "В магазине: 3 ручки по 15₽ и один блокнот — общая сумма 75₽. Сколько стоит блокнот?",
        "У отца и сына вместе 36 лет. Отец в 4 раза старше сына. Сколько лет каждому?",
//        "Дан алгоритм: for i in 1..5 sum += i. Какой результат?",
        "Опиши, что делает следующий код: if (x > 0) x = x * 2 else x = x - 1",
//        "Почему голосование вслепую может уменьшать предвзятость?",
        "Алиса и Боб находятся в городе с часовым поясом X, у Алисы сейчас 3 PM, сколько у Боба...",
//        "Найди ошибку в рассуждениях: \"Если все A — B; C — A; Следовательно, C — B.\""
    )

    println("Список запросов для тестирования:")
    prompts.forEachIndexed { index, prompt ->
        println("${index + 1}. $prompt")
    }
    println()

    // Модель для использования
    val model = "glm-4.5-flash"
    val client = ZAIClient(apiToken)
    val results = mutableListOf<ModelResult>()

    // Для каждого запроса отправляем два варианта
    for ((index, prompt) in prompts.withIndex()) {
        println("=".repeat(80))
        println("Запрос ${index + 1}: $prompt")
        println("=".repeat(80))

        // 1. Запрос без изменений
        println("-".repeat(80))
        println("Вариант 1: Обычный запрос")
        println("-".repeat(80))

        val result1 = client.callModel(model, prompt)
        results.add(result1)

        if (result1.error != null) {
            println("ОШИБКА: ${result1.error}")
        } else {
            println("Ответ получен за ${result1.responseTimeMs} мс")
        }

        // 2. Запрос с добавлением "Решай пошагово"
        println("-".repeat(80))
        println("Вариант 2: С указанием 'Решай пошагово'")
        println("-".repeat(80))

        val promptWithSteps = "$prompt. Решай пошагово"
        val result2 = client.callModel(model, promptWithSteps)
        results.add(result2)

        if (result2.error != null) {
            println("ОШИБКА: ${result2.error}")
        } else {
            println("Ответ получен за ${result2.responseTimeMs} мс")
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
    println(String.format("%-5s | %10s | %10s | %10s",
        "№", "Время (мс)", "Вх.токены", "Вых.токены"))
    println("-".repeat(80))

    // Данные
    results.forEachIndexed { index, result ->
        println(String.format("%-5d | %10d | %10d | %10d",
            index + 1,
            result.responseTimeMs,
            result.inputTokens,
            result.outputTokens
        ))
    }
    println()
}

fun formatResponse(text: String): String {
    // Убираем начальные и конечные переводы строк
    var formatted = text.trim()

    // Заменяем LaTeX формулы на более читаемый вид
    // Inline формулы \\(...\\) заменяем на простой вид
    formatted = formatted.replace(Regex("""\\?\\\\\(([^)]+)\\?\\\\\)""")) { matchResult ->
        " ${matchResult.groupValues[1]} "
    }

    // Block формулы \\[...\\] заменяем с отступами
    formatted = formatted.replace(Regex("""\\?\\\\\[([^\]]+)\\?\\\\\]""")) { matchResult ->
        "\n    ${matchResult.groupValues[1]}\n"
    }

    // Улучшаем отображение заголовков ###
    formatted = formatted.replace(Regex("""^### (.+)$""", RegexOption.MULTILINE)) { matchResult ->
        "\n┌─ ${matchResult.groupValues[1]} ─┐"
    }

    // Улучшаем отображение жирного текста **текст**
    formatted = formatted.replace(Regex("""\*\*(.+?)\*\*""")) { matchResult ->
        "【${matchResult.groupValues[1]}】"
    }

    return formatted
}

fun printDetailedResponses(results: List<ModelResult>) {
    println("=".repeat(80))
    println("ДЕТАЛЬНЫЕ ОТВЕТЫ")
    println("=".repeat(80))
    println()

    results.forEachIndexed { index, result ->
        println("-".repeat(80))
        println("Ответ ${index + 1}")
        println("-".repeat(80))
        println("Время ответа: ${result.responseTimeMs} мс")
        println("Токены: ${result.inputTokens} (вход) + ${result.outputTokens} (выход) = ${result.totalTokens} (всего)")

        if (result.error != null) {
            println()
            println("❌ Ошибка: ${result.error}")
        } else {
            println()
            println("Ответ:")
            println()
            val formattedResponse = formatResponse(result.response)
            println(formattedResponse)
        }
        println()
    }
}