package Shingu.Interviewer.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


//GPT API에 전달할 요청을 정의 (사용할 모델 이름과 사용자의 프롬프트가 포함됨)
@Data
public class ChatGPTRequest {

    private String model;
    private List<Message> messages;

    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }
}
