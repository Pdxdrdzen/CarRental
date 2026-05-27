package com.carrental.gui;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    // dla prostych klas: ApiClient.get("/vehicles/1", VehicleDto.class)
    public static <T> T get(String path, Class<T> type) throws Exception {
        return get(path, MAPPER.getTypeFactory().constructType(type));
    }

    // dla kolekcji: ApiClient.get("/vehicles", ApiClient.listOf(Map.class))
    public static <T> T get(String path, JavaType type) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new RuntimeException("GET " + path + " returned " + res.statusCode());
        return MAPPER.readValue(res.body(), type);
    }

    public static <T> T post(String path, Object body, Class<T> responseType) throws Exception {
        String json = MAPPER.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 400)
            throw new RuntimeException("POST " + path + " returned " + res.statusCode() + ": " + res.body());
        return MAPPER.readValue(res.body(), responseType);
    }

    public static void delete(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 400)
            throw new RuntimeException("DELETE " + path + " returned " + res.statusCode());
    }

    // helper: ApiClient.listOf(Map.class)
    public static JavaType listOf(Class<?> elementType) {
        return MAPPER.getTypeFactory().constructCollectionType(java.util.List.class, elementType);
    }

    public static ObjectMapper mapper() { return MAPPER; }
}