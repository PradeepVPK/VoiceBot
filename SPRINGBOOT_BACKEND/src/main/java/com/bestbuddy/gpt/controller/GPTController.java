package com.bestbuddy.gpt.controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.bestbuddy.gpt.service.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200/")
public class GPTController {

    private final GPTService gptService;

    @Autowired
    public  GPTController(GPTService gptService) {
        this.gptService = gptService;
    }
    @PostMapping("/chat")
    public ResponseEntity<byte[]> chatWithGPT(@RequestBody Map<String, String> requestBody) {
         String prompt = requestBody.get("text");
         String response= gptService.chatWithGpt(prompt);
         String ttsInput=gptService.getJSON(response,"ChatGPT");
        System.out.println(ttsInput);
         byte[] speechResponse=gptService.ttsCloudLab(ttsInput);
//        JSONObject result=new JSONObject(speechresponse);
//        JSONObject resultobj= result.getJSONObject("result");
//        String audioURL=resultobj.toString();
       // System.out.println(speechresponse);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(speechResponse.length);
        headers.setContentDispositionFormData("filename", "audio.mp3"); // Set the filename for download

        return new ResponseEntity<>(speechResponse, headers, HttpStatus.OK);
    }


}
