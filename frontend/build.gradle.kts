plugins {
    // Kotlin-приложение для настольного frontend-клиента.
    kotlin("jvm")
    application
}

dependencies {
    // Gson используется для обмена JSON с backend.
    implementation("com.google.code.gson:gson:2.13.2")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
//    mainClass.set("org.example.MainKt")
    applicationName = "Dobriy Shkaf"
}

sourceSets {
    main {
        resources.srcDir("src/main/kotlin/resources")
    }
}

tasks.test {
    useJUnitPlatform()
}