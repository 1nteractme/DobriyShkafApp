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
    mainClass.set("org.example.MainKt")
    applicationName = "Dobriy Shkaf"
}

tasks.jar {
    archiveFileName.set("frontend.jar")
}

val generatedResourcesDir = layout.buildDirectory.dir("generated/resources")

val generateAppProperties by tasks.registering {
    val outputFile = generatedResourcesDir.map { it.file("app.properties") }

    inputs.property("appVersion", project.version.toString())
    outputs.file(outputFile)

    doLast {
        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText("app.version=${project.version}\n")
        }
    }
}

sourceSets {
    main {
        resources.srcDir("src/main/kotlin/resources")
        resources.srcDir(generatedResourcesDir)
    }
}

tasks.processResources {
    dependsOn(generateAppProperties)
}

tasks.test {
    useJUnitPlatform()
}
