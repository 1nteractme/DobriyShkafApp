package com.iapp.iapp_messenger.test_client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;

/// Фабрика Json с поддержкой LocalDate.
public final class JsonFactory {

    /// Запрещает создание экземпляров фабрики.
    private JsonFactory() {}

    /// Создаёт Json, который сериализует даты в формате ISO-8601.
    public static Gson create() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        LocalDate.class,
                        (JsonSerializer<LocalDate>) (value, type, context) ->
                                value == null ? null : new JsonPrimitive(value.toString())
                )
                .registerTypeAdapter(
                        LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, context) ->
                                json == null || json.isJsonNull() ? null : LocalDate.parse(json.getAsString())
                )
                .create();
    }
}