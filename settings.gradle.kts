plugins {
    // Автоматический подбор JDK для Gradle toolchains.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "DobriyShkafApp"

// Ветка frontend/base подключает только frontend-модуль.
include("frontend")