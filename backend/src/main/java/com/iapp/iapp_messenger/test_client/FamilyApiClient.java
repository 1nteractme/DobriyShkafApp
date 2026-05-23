package com.iapp.iapp_messenger.test_client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iapp.iapp_messenger.dao.dto.ApiResponse;
import com.iapp.iapp_messenger.dao.hibernate.Family;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/// Тестовый REST-клиент для ручной проверки точек доступа семей.
public class FamilyApiClient {

    private static final String BASE_URL =
            "http://185.204.0.88:8082/api/families-admin";

    private static final MediaType JSON =
            MediaType.get("application/json");

    private final OkHttpClient client = new OkHttpClient();

    private final Gson gson = JsonFactory.create();

    /// Получить всю таблицу.
    public List<Family> getAllFamilies() throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/all")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            assert response.body() != null;
            String json = response.body().string();

            Type type = new TypeToken<ApiResponse<List<Family>>>() {}.getType();

            ApiResponse<List<Family>> result =
                    gson.fromJson(json, type);

            return result.getData();
        }
    }

    /// Получить семью по id.
    public Family getFamily(long id) throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/" + id)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            assert response.body() != null;
            String json = response.body().string();

            Type type = new TypeToken<ApiResponse<Family>>() {}.getType();

            ApiResponse<Family> result =
                    gson.fromJson(json, type);

            return result.getData();
        }
    }

    /// Создать семью.
    public Family createFamily(Family family) throws IOException {

        String bodyJson = gson.toJson(family);

        RequestBody body = RequestBody.create(bodyJson, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            assert response.body() != null;
            String json = response.body().string();

            Type type = new TypeToken<ApiResponse<Family>>() {}.getType();

            ApiResponse<Family> result =
                    gson.fromJson(json, type);

            return result.getData();
        }
    }

    /// Обновить семью.
    public Family updateFamily(Family family) throws IOException {

        String bodyJson = gson.toJson(family);

        RequestBody body = RequestBody.create(bodyJson, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/update")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            assert response.body() != null;
            String json = response.body().string();

            Type type = new TypeToken<ApiResponse<Family>>() {}.getType();

            ApiResponse<Family> result =
                    gson.fromJson(json, type);

            return result.getData();
        }
    }

    /// Полностью перезаписать таблицу.
    public void rewriteAll(List<Family> families) throws IOException {

        String bodyJson = gson.toJson(families);

        RequestBody body = RequestBody.create(bodyJson, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/rewrite-all")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            assert response.body() != null;
            String json = response.body().string();

            System.out.println(json);
        }
    }

    /// Удалить семью.
    public void deleteFamily(long id) throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/delete/" + id)
                .post(RequestBody.create(new byte[0]))
                .build();

        try (Response response = client.newCall(request).execute()) {

            assert response.body() != null;
            String json = response.body().string();

            System.out.println(json);
        }
    }
}