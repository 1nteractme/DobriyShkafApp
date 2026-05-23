package com.interactme.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import com.interactme.data.ApiResponse
import com.interactme.data.Family
import java.lang.reflect.Type
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.LocalDate

/// REST-клиент frontend-модуля для работы с семейными записями.
class FamilyApiClient(
    private val baseUrl: String = defaultBaseUrl(),
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build(),
    private val gson: Gson = createGson()
) {
    /// Загружает все семьи из backend.
    fun getAllFamilies(): List<Family> {
        val type = object : TypeToken<ApiResponse<List<Family>>>() {}.type
        return request("/all", "GET", null, type) ?: emptyList()
    }

    /// Создаёт новую семью через backend.
    fun createFamily(family: Family): Family {
        val type = object : TypeToken<ApiResponse<Family>>() {}.type
        return request("/create", "POST", gson.toJson(family), type)
            ?: error("Backend returned empty family")
    }

    /// Обновляет существующую семью через backend.
    fun updateFamily(family: Family): Family {
        val type = object : TypeToken<ApiResponse<Family>>() {}.type
        return request("/update", "POST", gson.toJson(family), type)
            ?: error("Backend returned empty family")
    }

    /// Удаляет семью по идентификатору.
    fun deleteFamily(id: Long) {
        val type = object : TypeToken<ApiResponse<String>>() {}.type
        request<String>("/delete/$id", "POST", "", type)
    }

    private fun <T> request(
        path: String,
        method: String,
        jsonBody: String?,
        responseType: Type): T?
    {
        val bodyPublisher = if (jsonBody == null) HttpRequest.BodyPublishers.noBody() else HttpRequest.BodyPublishers.ofString(jsonBody)

        val request = HttpRequest.newBuilder(URI.create(baseUrl.trimEnd('/') + path))
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .method(method, bodyPublisher)
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299)
            error("HTTP ${response.statusCode()}: ${response.body()}")

        val apiResponse = gson.fromJson<ApiResponse<T>>(response.body(), responseType)
        apiResponse.error?.takeIf { it.isNotBlank() }?.let { error(it) }
        return apiResponse.data
    }

    companion object {
        /// Возвращает адрес backend из системного свойства, переменной окружения или значения по умолчанию.
        fun defaultBaseUrl(): String =
            System.getProperty("dapabase.api.base")
                ?: System.getenv("DAPABASE_API_BASE")
                ?: "http://185.204.0.88:8082/api/families-admin"

        /// Настраивает Gson для корректной сериализации LocalDate.
        fun createGson(): Gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonSerializer<LocalDate> { value, _, _ ->
                    if (value == null) null else JsonPrimitive(value.toString())
                }
            )
            .registerTypeAdapter(
                LocalDate::class.java,
                JsonDeserializer { json, _, _ ->
                    json?.takeUnless { it.isJsonNull }?.asString?.takeIf { it.isNotBlank() }?.let(LocalDate::parse)
                }
            )
            .create()
    }
}