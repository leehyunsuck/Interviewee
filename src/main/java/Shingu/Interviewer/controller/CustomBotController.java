package Shingu.Interviewer.controller;

import Shingu.Interviewer.dto.ChatGPTRequest;
import Shingu.Interviewer.dto.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//사용자의 요청을 처리하고 GPT API를 호출하여 응답을 반환
@RestController
//@RequestMapping("/bot")
@Service    //이거 임시로 객체 생성하는데 안되서 해보는중
public class CustomBotController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    public ChatGPTResponse chat(@RequestParam("prompt") String prompt) {
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        return template.postForObject(apiURL, request, ChatGPTResponse.class);
    }
}
