package data_access.ai.gemini;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

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
    private static final int MAX_RETRIES = 3;

    public AiExtractorDataAccessObject(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    @Override
    public SyllabusParseResultData extractStructuredData(String syllabusText) {
        String prompt = buildPrompt(syllabusText);
        
        // Retry logic for handling inconsistent AI responses
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("Attempt " + attempt + " of " + MAX_RETRIES + " to extract data from Gemini...");
                
                String jsonResponse = callGeminiApi(prompt);
                SyllabusParseResultData result = parseGeminiResponse(jsonResponse);
                
                // Validate the response has required fields
                if (isValidResponse(result)) {
                    System.out.println("Successfully extracted and validated data");
                    return result;
                } else {
                    System.err.println("Warning: Response missing required fields, retrying...");
                    if (attempt == MAX_RETRIES) {
                        throw new RuntimeException("Failed to get valid response after " + MAX_RETRIES + " attempts");
                    }
                }
                
            } catch (IOException | InterruptedException e) {
                if (attempt == MAX_RETRIES) {
                    throw new RuntimeException("Failed to call Gemini API after " + MAX_RETRIES + " attempts", e);
                }
                System.err.println("API call failed, retrying... (" + e.getMessage() + ")");
            } catch (JsonSyntaxException e) {
                if (attempt == MAX_RETRIES) {
                    throw new RuntimeException("Failed to parse Gemini response as JSON after " + MAX_RETRIES + " attempts", e);
                }
                System.err.println("JSON parsing failed, retrying... (" + e.getMessage() + ")");
            } catch (RuntimeException e) {
                if (attempt == MAX_RETRIES) {
                    throw new RuntimeException("AI extraction failed after " + MAX_RETRIES + " attempts: " + e.getMessage(), e);
                }
                System.err.println("Extraction failed, retrying... (" + e.getMessage() + ")");
            }
            
            // Wait a bit before retrying
            try {
                Thread.sleep(1000 * attempt); // Exponential backoff
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting to retry", e);
            }
        }
        
        throw new RuntimeException("Unreachable code - all retries exhausted");
    }
    
    private boolean isValidResponse(SyllabusParseResultData result) {
        // Check if the result object exists
        if (result == null) {
            System.err.println("Validation failed: result is null");
            return false;
        }
        
        // Course code is required - without it we can't identify the course
        if (result.getCourseCode() == null || result.getCourseCode().trim().isEmpty()) {
            System.err.println("Validation failed: courseCode is missing");
            return false;
        }
        
        // Course name is required - must have a descriptive name for the course
        if (result.getCourseName() == null || result.getCourseName().trim().isEmpty()) {
            System.err.println("Validation failed: courseName is missing");
            return false;
        }
        
        // Assessments array must exist (can't be null)
        if (result.getAssessments() == null) {
            System.err.println("Validation failed: assessments array is null");
            return false;
        }
        
        // Must have at least one assessment - a syllabus with no assessments is not useful
        if (result.getAssessments().isEmpty()) {
            System.err.println("Validation failed: no assessments found");
            return false;
        }
        
        // All validations passed
        return true;
    }

    private String buildPrompt(String syllabusText) {
        return String.format(
            "Extract the following information from the syllabus text provided below.\n" +
            "Return ONLY a single, valid JSON object. Do not include any explanatory text before or after the JSON.\n\n" +
            "The JSON object must have these top-level keys: courseCode, courseName, term, instructor, assessments.\n\n" +
            "1.  \"courseCode\": (String) The course code (e.g., \"CS101\"). REQUIRED.\n" +
            "2.  \"courseName\": (String) The full name of the course (e.g., \"Introduction to Computer Science\"). REQUIRED.\n" +
            "3.  \"term\": (String) The academic term (e.g., \"Fall 2023\"). If not found, use null.\n" +
            "4.  \"instructor\": (String) The name of the course instructor (e.g., \"Dr. Jane Smith\"). If not found, use null.\n" +
            "5.  \"assessments\": An array of objects. Each object represents a single graded item. REQUIRED - must have at least one assessment.\n" +
            "    IMPORTANT: If the syllabus mentions repeated assessments (e.g., \"Tutorial Activities 1-5\" or \"5 Assignments\"), create a SEPARATE object for each instance.\n" +
            "    If the syllabus states that some are dropped (e.g., \"best 5 of 6 tutorials count\"), only create objects for the number that count (5 in this example) and calculate their weighting accordingly.\n" +
            "    Each assessment object must have these keys:\n" +
            "    - \"title\": (String) The name of the assessment (e.g., \"Assignment 1: Logic Puzzles\"). For repeated items, number them.\n" +
            "    - \"type\": (String) MUST BE ONE OF: TEST, ASSIGNMENT, EXAM, QUIZ, PROJECT, OTHER\n" +
            "    - \"weight\": (Double) The grade percentage as a decimal (e.g., 0.15 for 15%%). For repeated items, divide total weight equally.\n" +
            "    - \"dueDateIso\": (String) ISO-8601 string. If the syllabus only has month/day, infer the year from the term; if none, use the current year. Default time to 23:59:00 and output as ISO-8601 (e.g., 2025-10-15T23:59:00Z). If no date is present, use null.\n" +
            "    - \"notes\": (String) Any additional information. Use empty string if none.\n\n" +
            "CRITICAL: Return ONLY the JSON object. No markdown code blocks, no explanations, just the JSON.\n\n" +
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
        try {
            // Parse the outer API response wrapper
            JsonObject jsonObject = gson.fromJson(rawApiResponse, JsonObject.class);
            
            // Validate the root JSON object exists
            if (jsonObject == null) {
                throw new RuntimeException("Response is not valid JSON");
            }
            
            // Navigate to the "candidates" array - Gemini returns multiple candidate responses
            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates in response. Full response: " + rawApiResponse);
            }
            
            // Get the first (best) candidate response
            JsonObject candidate = candidates.get(0).getAsJsonObject();
            if (candidate == null) {
                throw new RuntimeException("First candidate is null");
            }
            
            // Navigate to the "content" object within the candidate
            JsonObject content = candidate.getAsJsonObject("content");
            if (content == null) {
                throw new RuntimeException("Content is null in candidate");
            }
            
            // Navigate to the "parts" array - content can have multiple parts
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("No parts in content");
            }
            
            // Get the first part (should contain our text response)
            JsonObject part = parts.get(0).getAsJsonObject();
            if (part == null) {
                throw new RuntimeException("First part is null");
            }
            
            // Extract the actual text content from the part
            JsonElement textElement = part.get("text");
            if (textElement == null || !textElement.isJsonPrimitive()) {
                throw new RuntimeException("Text element is missing or not a string");
            }
            
            String extracted = textElement.getAsString().trim();
            
            // Handle case where AI wraps JSON in markdown code blocks (```json ... ```)
            if (extracted.startsWith("```")) {
                System.out.println("Response wrapped in code blocks, extracting...");
                int start = extracted.indexOf('{');
                int end = extracted.lastIndexOf('}');
                if (start == -1 || end == -1 || end <= start) {
                    throw new RuntimeException("Could not find valid JSON in code block");
                }
                extracted = extracted.substring(start, end + 1).trim();
            }
            
            // Remove any leading/trailing text before first { and after last }
            int firstBrace = extracted.indexOf('{');
            int lastBrace = extracted.lastIndexOf('}');
            if (firstBrace > 0 || lastBrace < extracted.length() - 1) {
                System.out.println("Trimming extra text around JSON object...");
                if (firstBrace == -1 || lastBrace == -1) {
                    throw new RuntimeException("No valid JSON object found in response");
                }
                extracted = extracted.substring(firstBrace, lastBrace + 1);
            }
            
            // Log a preview of the extracted JSON for debugging
            System.out.println("Extracted JSON: " + extracted);
            
            // Parse the cleaned JSON into our DTO
            return gson.fromJson(extracted, SyllabusParseResultData.class);
            
        } catch (JsonSyntaxException e) {
            System.err.println("Raw response causing parse error: " + rawApiResponse);
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        } catch (NullPointerException e) {
            System.err.println("Unexpected null in response structure: " + rawApiResponse);
            throw new RuntimeException("Unexpected response structure from Gemini API", e);
        }
    }
}
