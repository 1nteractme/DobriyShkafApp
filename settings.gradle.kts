plugins {
    // Автоматический подбор JDK для Gradle toolchains.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "DobriyShkafApp"

// Два независимых модуля для параллельной работы frontend и backend разработчиков.
include("frontend", "backend")