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
    // Ktor HTTP клиент
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-client-logging:2.3.7")

    // JSON сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Корутины для асинхронных операций
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Логирование
    implementation("ch.qos.logback:logback-classic:1.4.14")

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
        val token = localProperties.getProperty("zai.api.token", "")

        configFile.writeText("zai.api.token=$token\n")
    }
}

// Автоматически генерируем конфигурацию перед компиляцией
tasks.named("processResources") {
    dependsOn("generateConfig")
}