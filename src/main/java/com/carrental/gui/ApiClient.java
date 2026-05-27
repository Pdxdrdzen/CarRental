package com.carrental.gui;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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
    public static <T> T postParams(String path, Map<String, String> params, Class<T> responseType) throws Exception {
        StringBuilder url = new StringBuilder(BASE_URL + path + "?");
        params.forEach((k, v) -> url.append(k).append("=")
                .append(java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8)).append("&"));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .header("Authorization", "Bearer " + SessionManager.getToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) throw new RuntimeException(response.body());
        return mapper.readValue(response.body(), responseType);
    }
    public static <T> T patch(String path, Object body, Class<T> responseType) throws Exception {
        String bodyStr = body != null ? mapper.writeValueAsString(body) : "";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + SessionManager.getToken())
                .method("PATCH", body != null
                        ? HttpRequest.BodyPublishers.ofString(bodyStr)
                        : HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) throw new RuntimeException(response.body());
        return mapper.readValue(response.body(), responseType);
    }

    // helper: ApiClient.listOf(Map.class)
    public static JavaType listOf(Class<?> elementType) {
        return MAPPER.getTypeFactory().constructCollectionType(java.util.List.class, elementType);
    }

    public static ObjectMapper mapper() { return MAPPER; }
}