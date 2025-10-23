package org.example

import kotlinx.coroutines.runBlocking
import org.example.agents.Agent1
import org.example.agents.Agent2
import org.example.client.AiClient
import org.example.client.ZAIClient
import org.example.models.TaskInput
import java.util.*
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

fun main(): Unit = runBlocking {

    val apiToken = loadApiToken()
    if (apiToken.isNullOrBlank()) {
        println("ОШИБКА: API токен не найден!")
        println("Создайте файл config.properties с параметром zai.api.token")
        println("Получите токен на: https://z.ai")
        exitProcess(1)
    }

    val client: AiClient = ZAIClient(apiToken = apiToken)
    val agent1 = Agent1(aiClient = client)
    val agent2 = Agent2(aiClient = client)

    print("Введите ваш запрос: ")
    val userMessage = readln()

    val agent1InputTask = TaskInput(message = userMessage)
    val agent1Result = agent1.execute(taskInput = agent1InputTask)

    val agent2TaskInput = TaskInput(message = agent1Result.result.orEmpty())
    val agent2Result = agent2.execute(taskInput = agent2TaskInput)

    println(agent2Result.result)
}
