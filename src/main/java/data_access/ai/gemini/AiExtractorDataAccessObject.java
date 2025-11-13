package data_access.ai.gemini;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import use_case.dto.SyllabusParseResultData;
import use_case.port.outgoing.AiExtractionDataAccessInterface;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class AiExtractorDataAccessObject implements AiExtractionDataAccessInterface {
    
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public AiExtractorDataAccessObject(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    @Override
    public SyllabusParseResultData extractStructuredData(String syllabusText) {
        try {
            String prompt = buildPrompt(syllabusText);

            String jsonResponse;
            try {
                jsonResponse = callGeminiApi(prompt);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to call Gemini API", e);
            }
            
            return parseGeminiResponse(jsonResponse);
        } catch (RuntimeException e) {
            throw new RuntimeException("AI extraction failed: " + e.getMessage(), e);
        }
        
    }
    
    private String buildPrompt(String syllabusText) {
        return String.format(
            "Extract the following information from the syllabus text provided below.\n" +
            "Return ONLY a single, valid JSON object. Do not include any explanatory text before or after the JSON.\n\n" +
            "The JSON object must have two top-level keys: \"assessments\" and \"weightComponents\".\n\n" +
            "1.  \"assessments\": An array of objects. Each object represents a single graded item (like an assignment, exam, or quiz) and must have these keys:\n" +
            "    - \"title\": (String) The name of the assessment (e.g., \"Assignment 1: Logic Puzzles\").\n" +
            "    - \"type\": (String) The type of the assessment (MUST BE ONE OF THE FOLLOWING: 'TEST','ASSIGNMENT','EXAM','QUIZ','PROJECT','OTHER').\n" +
            "    - \"weight\": (Double) The grade percentage as a decimal (e.g., 0.15 for 15%%).\n" +
            "    - \"dueDate\": (String) The due date in ISO 8601 format (e.g., \"2025-12-15T23:59:00Z\"). If no date is found, use null.\n\n" +
            "2.  \"weightComponents\": An array of objects describing the grade breakdown. Each object must have these keys:\n" +
            "    - \"componentName\": (String) The name of the category (e.g., \"Assignments\", \"Midterm Exam\").\n" +
            "    - \"weight\": (Double) The total weight of this category as a decimal (e.g., 0.40 for 40%%).\n\n" +
            "Here is the syllabus text:\n" +
            "---\n" +
            "%s",
            syllabusText);
    }

    private String callGeminiApi(String prompt) throws IOException, InterruptedException {
        String apiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + this.apiKey;

        Map<String, Object> requestBodyMap = Map.of(
                "contents", new Map[]{
                        Map.of(
                                "parts", new Map[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        String requestBody = gson.toJson(requestBodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send request and get response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Check for any network or API errors
        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    private SyllabusParseResultData parseGeminiResponse(String rawApiResponse) {
        JsonObject jsonObject = gson.fromJson(rawApiResponse, JsonObject.class);

        String extracted = jsonObject.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString()
                .trim();

        if (extracted.startsWith("```")) {
            int start = extracted.indexOf('{');
            int end = extracted.lastIndexOf('}');
            extracted = extracted.substring(start, end + 1).trim();
        }

        return gson.fromJson(extracted, SyllabusParseResultData.class);
    }
}
