plugins {
    // Общие версии плагинов для всех модулей проекта.
    kotlin("jvm") version "2.3.10" apply false
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    // Общие координаты и репозитории для backend и frontend.
    group = "org.example"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}