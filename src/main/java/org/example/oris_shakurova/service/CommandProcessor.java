package org.example.oris_shakurova.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CommandProcessor {

    private static final String OPENWEATHER_API_KEY = System.getenv("OPENWEATHER_API_KEY");
    private static final String EXCHANGERATE_API_KEY = System.getenv("EXCHANGERATE_API_KEY");

    public static String process(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        String cmd = parts[0].toLowerCase();

        return switch (cmd) {
            case "list" -> """
                Доступные команды:
                - list
                - weather <город>
                - exchange <код валюты>
                - quit""";

            case "weather" -> {
                if (parts.length < 2) yield "Использование: weather Moscow";
                yield fetchWeather(parts[1]);
            }

            case "exchange" -> {
                if (parts.length < 2) yield "Использование: exchange USD";
                yield fetchExchange(parts[1].toUpperCase());
            }

            case "quit" -> "QUIT_SIGNAL";

            default -> "Неизвестная команда. Введите 'list' для справки.";
        };
    }

    private static String fetchWeather(String city) {
        if (OPENWEATHER_API_KEY == null || OPENWEATHER_API_KEY.isEmpty()) {
            return "Ошибка: переменная OPENWEATHER_API_KEY не задана";
        }
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity +
                    "&appid=" + OPENWEATHER_API_KEY + "&units=metric&lang=ru";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                double temp = extractValue(json, "\"temp\":");
                String desc = extractString(json, "\"description\":\"", "\"");
                return String.format("Погода в %s: %.1f°C, %s", city, temp, desc);
            } else if (response.statusCode() == 401) {
                return "Ошибка: неверный API-ключ OpenWeather";
            } else {
                return "Город не найден.";
            }
        } catch (Exception e) {
            return "Ошибка при получении погоды: " + e.getMessage();
        }
    }

    private static String fetchExchange(String code) {
        if (EXCHANGERATE_API_KEY == null || EXCHANGERATE_API_KEY.isEmpty()) {
            return "Ошибка: переменная EXCHANGERATE_API_KEY не задана";
        }
        if (!code.matches("[A-Z]{3}")) {
            return "Неверный формат валюты. Пример: USD, EUR";
        }

        try {
            String url = "https://v6.exchangerate-api.com/v6/" + EXCHANGERATE_API_KEY + "/latest/RUB";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Ошибка API: " + response.statusCode();
            }

            String json = response.body();

            if (json.contains("\"result\":\"error\"")) {
                String errorMsg = extractString(json, "\"error-type\":\"", "\"");
                return "Ошибка ExchangeRate-API: " + (errorMsg.isEmpty() ? "неизвестная" : errorMsg);
            }

            int idx = json.indexOf("\"" + code + "\":");
            if (idx == -1) {
                return "Валюта " + code + " не поддерживается.";
            }

            int start = idx + code.length() + 3;
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            double rubToX = Double.parseDouble(json.substring(start, end).trim());
            double xToRub = 1.0 / rubToX;

            return String.format("Курс %s к RUB: %.4f", code, xToRub);

        } catch (Exception e) {
            return "Ошибка при получении курса: " + e.getMessage();
        }
    }

    private static double extractValue(String json, String key) {
        int i = json.indexOf(key);
        if (i == -1) return 0.0;
        int start = i + key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        try {
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static String extractString(String json, String startMark, String endMark) {
        int i = json.indexOf(startMark);
        if (i == -1) return "";
        i += startMark.length();
        int j = json.indexOf(endMark, i);
        return j == -1 ? "" : json.substring(i, j);
    }
}