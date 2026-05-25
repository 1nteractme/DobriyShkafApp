package com.interactme

import java.util.Properties

/// Метаданные приложения, сгенерированные Gradle во время сборки.
object AppInfo {
    val version: String = runCatching {
        val properties = Properties()
        AppInfo::class.java.getResourceAsStream("/app.properties")?.use(properties::load)
        properties.getProperty("app.version")
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: "dev"
}