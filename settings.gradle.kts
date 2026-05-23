plugins {
    // Автоматический подбор JDK для Gradle toolchains.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "DobriyShkafApp"

// Ветка backend/base подключает только backend-модуль.
include("backend")