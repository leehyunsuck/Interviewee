package Shingu.Interviewer.service;

import Shingu.Interviewer.controller.CustomBotController;
import Shingu.Interviewer.dto.ChatGPTResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class OpenAIService {
    @Autowired
    CustomBotController customBotController;

    @Autowired
    SendMailService sendMailService;

    // GPT 응답 받기
    private String getResponse(String prompt) {
        ChatGPTResponse gptResponse = customBotController.chat(prompt);
        return gptResponse.getChoices().get(0).getMessage().getContent();
    }

    // 질문 생성
    public String generateQuestions(String title) {
        return "chat";
    }

    @Async // 비동기로 보고서 생성 및 메일 전송
    public void generateReport(@RequestParam Map<String, String> data, Model model, String error, int repeat) {
        if (repeat++ > 2) {
            sendMailService.sendMail((String) model.getAttribute("loggedInEmail"), "Interviewee 보고서", "보고서 생성 과정에서 에러 발생이 지속적으로 발생하여 보고서 생성이 불가능합니다.\n서비스 과정에 불편을 드려서 죄송합니다.");
            return;
        }
        String prompt = """
            면접 질문과 답변에 대한 면접 전문가의 피드백을 반환하세요.
            
            규칙:
            - 피드백 첫 부분에 '몇 번째 응답은...' 과 같은 서론을 작성하지 마세요.
            - 답변이 너무 성의없으면 주의 주는 내용을 포함해 주세요.
            - 질문과 답변의 연관성이 없거나, 답변이 입력되어있지 않으면 칭찬 내용 없이 피드백 불가능 하다는 내용을 반환하세요.
            - 질문에 대한 답변이 좋거나 틀리면 그 이유를 포함해 주세요.
            - 해당 순서에 맞지 않은 질문과 답변에 대한 피드백은 참견하지 마세요.
            
            반환 개수:
            - 총 5개
            
            반환 형식:
            {
                "1" : "피드백 내용",
                "2" : "피드백 내용",
                "3" : "피드백 내용",
                "4" : "피드백 내용",
                "5" : "피드백 내용"
            }
            
            위 규칙과 반환 개수, 반환 형식을 지키지 않을 시 에러가 발생하기에 꼭 지켜야합니다.
            반환하기 전 다시 한번 검토하세요.
            """;

        if (!error.isEmpty()) prompt += "\n" + "다음과 같은 에러가 발생했었으니 검토 똑바로 해줘.\n" + error;

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 문자열에서 List<String>으로 변환
            String answersJson = data.get("answers");
            String questionsJson = data.get("questions");

            List<String> answers = objectMapper.readValue(answersJson, new TypeReference<>() {});
            List<String> questions = objectMapper.readValue(questionsJson, new TypeReference<>() {});

            StringBuilder promptBuilder = new StringBuilder(prompt);
            for (int i = 0; i < 5; i++) {
                promptBuilder.append("질문 : ").append(questions.get(i)).append("\n");
                promptBuilder.append("답변 : ").append(answers.get(i)).append("\n\n");
            }
            String response = getResponse(promptBuilder.toString());

            JSONObject jsonResponse = new JSONObject(response);
            String[] keys = JSONObject.getNames(jsonResponse);

            String header = """
                <div class="header" style="display: flex; align-items: center; padding-bottom: 20px; border-bottom: 3px solid #3D5AED; padding: 20px;">
                    <h1 style="font-size: 24px; color: #3D5AED; margin: 0;">Interviewee</h1>
                </div>
                <br>
                """;
            String frame = """
                
                <div class="question-block" style="margin-bottom: 30px;">
                    <div class="question-title" style="font-size: 18px; font-weight: bold; color: #3D5AED; margin-bottom: 10px;">[질문]</div>
                    <div class="answer" style="background-color: #f1f4ff; padding: 15px; border-left: 5px solid #6B7FE7; margin-bottom: 10px;">
                        <strong>답변:</strong> [답변]
                    </div>
                    <div class="feedback" style="background-color: #e7edff; padding: 15px; border-left: 5px solid #6B7FE7; margin-bottom: 10px;">
                        <strong>피드백:</strong> [피드백]
                    </div>
                </div>
                
                """;
            StringBuilder reportBuilder = new StringBuilder(header);

            if (keys.length != 5) {
                this.generateReport(data, model, "피드백 개수가 부족합니다. 피드백은 총 5가지가 넘어와야합니다.", repeat);
                return;
            }

            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                String value = jsonResponse.getString(key);
                String question = questions.get(i);
                String answer = answers.get(i);

                reportBuilder.append(frame.replace("[질문]", question)
                        .replace("[답변]", answer)
                        .replace("[피드백]", value));
            }

            sendMailService.sendMail((String) model.getAttribute("loggedInEmail"), "Interviewee 보고서", reportBuilder.toString());
        } catch (Exception e) {
            Logger logger = Logger.getLogger("OpenAIService");
            logger.warning("Error generating report: " + e.getMessage());
            this.generateReport(data, model, e.getMessage(), repeat);
        }
    }
}