import java.util.Properties

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Загружаем локальные настройки
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

dependencies {
    // HTTP клиент для запросов к API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Корутины для асинхронных операций
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("org.example.MainKt")
}

// Создаем файл конфигурации с токеном
tasks.register("generateConfig") {
    doLast {
        val resourcesDir = file("src/main/resources")
        resourcesDir.mkdirs()

        val configFile = file("src/main/resources/config.properties")
        val token = localProperties.getProperty("huggingface.api.token", "")

        configFile.writeText("huggingface.api.token=$token\n")
    }
}

// Автоматически генерируем конфигурацию перед компиляцией
tasks.named("processResources") {
    dependsOn("generateConfig")
}