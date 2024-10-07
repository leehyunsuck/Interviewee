package Shingu.Interviewer.service;

import Shingu.Interviewer.controller.CustomBotController;
import Shingu.Interviewer.dto.ChatGPTResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {
    @Autowired
    CustomBotController customBotController;

    // GPT 응답 받기
    private JSONObject getResponse(String prompt) {
        ChatGPTResponse gptResponse = customBotController.chat(prompt);
        return new JSONObject(gptResponse.getChoices().get(0).getMessage().getContent());
    }

    // 질문 생성
    public String generateQuestions(String title) {
        JSONObject json = this.getResponse(title);

        return "chat";
    }

    // 보고서 생성
    public String generateReport(String title) {
        return "report";
    }
}
