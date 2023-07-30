package com.bestbuddy.gpt.service;


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GPTService {


    private static final String GPT_API_URL = "https://open-ai21.p.rapidapi.com/conversation";
    private static final String RAPID_API_KEY_GPT = "caadb00afdmsh36cd95a51945ca7p1fa837jsnb48876348d7b";
    private static final String RAPID_API_HOST_GPT = "open-ai21.p.rapidapi.com";


    private static final String TTS_API_URL = "https://text-to-speech27.p.rapidapi.com/speech";
    private static final String RAPID_API_KEY_CL = "caadb00afdmsh36cd95a51945ca7p1fa837jsnb48876348d7b";
    private static final String RAPID_API_HOST_CL = "text-to-speech27.p.rapidapi.com";


    public String chatWithGpt(String prompt) {
        try {
            String requestBody = "{\n" +
                    "    \"messages\": [\n" +
                    "        {\n" +
                    "            \"role\": \"user\",\n" +
                    "            \"content\": \"" + prompt + "\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"max_token\": 500,\n" +
                    "    \"temperature\": 1,\n" +
                    "    \"web_access\": false\n" +
                    "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GPT_API_URL))
                    .header("content-type", "application/json")
                    .header("X-RapidAPI-Key", RAPID_API_KEY_GPT)
                    .header("X-RapidAPI-Host", RAPID_API_HOST_GPT)
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                return response.body();
            } else {
                return "Error from ChatGPT API: " + response.body();
            }
        } catch (IOException | InterruptedException e) {
            return "Error processing the prompt: " + e.getMessage();
        }
    }
    public byte[] ttsCloudLab(String prompt) {
        try {
            // Assuming 'prompt' contains the user input to be processed by ChatGPT
            String encodedText = URLEncoder.encode(prompt, "UTF-8");
            String ttsRequestBody = "?text=" + encodedText + "&lang=en-us";

            HttpRequest ttsRequest = HttpRequest.newBuilder()
                    .uri(URI.create(TTS_API_URL + ttsRequestBody))
                    .header("X-RapidAPI-Key", RAPID_API_KEY_CL)
                    .header("X-RapidAPI-Host", RAPID_API_HOST_CL)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> ttsResponse = client.send(ttsRequest, HttpResponse.BodyHandlers.ofByteArray());
            System.out.println("Text-to-Speech API Response Code: " + ttsResponse.statusCode());
            System.out.println("Text-to-Speech API Response Body: " + ttsResponse.body());

            if (ttsResponse.statusCode() == HttpStatus.OK.value()) {
                return ttsResponse.body();
            } else {
                // You may handle other HTTP status codes here if needed
                throw new IOException("Error from Text-to-Speech API: " + ttsResponse.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            // Handle exceptions as needed
            e.printStackTrace();
            return null;
        }
    }



    public String getJSON(String prompt,String key) {
        try {
            JSONObject gptResponse=new JSONObject(prompt);
            return gptResponse.getString(key);
        } catch (JSONException e) {
            // Handle exceptions as needed
            e.printStackTrace();
            return null;
        }

    }
}