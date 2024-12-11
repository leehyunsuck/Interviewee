package Shingu.Interviewer.service;

import Shingu.Interviewer.controller.CustomBotController;
import Shingu.Interviewer.dto.ChatGPTResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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
    public List<String> generateQuestions(HttpServletRequest request, String error, int count) {
        if (count > 3) return null;

        String prompt = """
            첨부한 이력서 내용 기반으로 면접 질문을 5개 생성하세요.
            
            아래 규칙과 반환 개수, 반환 형식을 지키지 않을 시 에러가 발생하기에 꼭 지켜야합니다.
            반환하기 전 다시 한번 검토하세요.
            
            규칙:
            - 5개의 질문을 반환하세요.
            
            반환 개수:
            - 총 5개
            
            반환 형식:
            {
                "1" : "질문 내용",
                "2" : "질문 내용",
                "3" : "질문 내용",
                "4" : "질문 내용",
                "5" : "질문 내용"
            }
            
            이력서 :
            
            """;

        List<String> questions = new ArrayList<>();

        try {
            // 필수 데이터 받기
            String projectName = request.getParameter("project-name");
            String projectPart = request.getParameter("project-part");
            String projectSkill = request.getParameter("project-skill");
            String projectContribution = request.getParameter("project-contribution");
            String projectContent = request.getParameter("project-content");

            // 선택적 데이터 받기 (없을 수 있음)
            String project2Name = request.getParameter("project2-name");
            String project2Part = request.getParameter("project2-part");
            String project2Skill = request.getParameter("project2-skill");
            String project2Contribution = request.getParameter("project2-contribution");
            String project2Content = request.getParameter("project2-content");

            String[] coverLetterTitles = request.getParameterValues("cover-letter-title");
            String[] coverLetterDetails = request.getParameterValues("cover-letter-detail");

            // selected-job
            String[] selectedJobs = request.getParameterValues("selected-job");
            String selectedJob = "";
            if (selectedJobs != null) for (int i = 0; i < selectedJobs.length; i++) selectedJob += selectedJobs[i];

            StringBuilder promptBuilder = new StringBuilder(prompt);
            promptBuilder.append("[선택 직군] : ").append(selectedJob).append("\n");
            if (projectName != null && !projectName.isEmpty()) {
                promptBuilder.append("[포트폴리오]").append("\n");
                promptBuilder.append("  - 이름 : ").append(projectName).append("\n");
                promptBuilder.append("  - 맡은 역할 : ").append(projectPart).append("\n");
                promptBuilder.append("  - 사용 스킬 : ").append(projectSkill).append("\n");
                promptBuilder.append("  - 기여도 : ").append(projectContribution).append("\n");
                promptBuilder.append("  - 프로젝트 내용 : ").append(projectContent).append("\n");
            }
            if (project2Name != null) {
                promptBuilder.append("[포트폴리오]").append("\n");
                promptBuilder.append("  - 이름 : ").append(project2Name).append("\n");
                promptBuilder.append("  - 맡은 역할 : ").append(project2Part).append("\n");
                promptBuilder.append("  - 사용 스킬 : ").append(project2Skill).append("\n");
                promptBuilder.append("  - 기여도 : ").append(project2Contribution).append("\n");
                promptBuilder.append("  - 프로젝트 내용2 : ").append(project2Content).append("\n");
            }
            promptBuilder.append("[자기소개서]").append("\n");
            for (int i = 0; i < coverLetterTitles.length; i++) {
                promptBuilder.append(" - 제목 : " +coverLetterTitles[i]).append("\n");
                if (coverLetterDetails.length > i) promptBuilder.append(" - 내용 : " + coverLetterDetails[i]).append("\n");
            }

            if (!error.isEmpty()) promptBuilder.append("\n **에러 : ").append(error).append("\n");

            //System.out.println(promptBuilder.toString());
            // 질문 받기
            String response = getResponse(promptBuilder.toString());

            JSONObject jsonResponse = new JSONObject(response);
            String[] keys = JSONObject.getNames(jsonResponse);

            if (keys.length != 5) throw new Exception("질문의 개수가 5개가 넘어오지 않았습니다.");

            for (int i = 0; i < keys.length; i++) questions.add(jsonResponse.getString(keys[i]));

            return questions;
        } catch (Exception e) {
            Logger logger = Logger.getLogger("OpenAIService");
            logger.warning("Error generating report: " + e.getMessage());
            this.generateQuestions(request, e.getMessage(), count + 1);
        }
        return null;
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
            - 질문과 답변이 순서대로 일치하지 않을 경우 피드백을 제공하지 마세요. 하지만 답변이 비어 있거나 성의 없는 경우, 순서와 상관없이 경고성 피드백을 제공하세요.
            - 서론으로 '몇 번째 응답은...'과 같은 표현을 사용하지 마세요.
            - 답변이 성의 없을 경우, 그 이유와 함께 개선 사항을 포함한 구체적인 피드백을 제공하세요.
            - 질문과 답변의 연관성이 없거나 답변이 입력되지 않으면, "피드백 불가능"이라는 경고성 메시지만 반환하세요.
            - 답변이 아무 문자나 작성된 경우, 경고성 메시지를 반환하세요.
            - 각 피드백은 2-3 문장으로 간결하고 구체적으로 작성하세요.
            - 질문에 대한 답변을 제대로 했을 경우 어떤점이 좋았는지 작성하세요.
            
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

            // 하단 내용 추가
            String footer = """
                    <div class="footer" style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e0e0e0; font-size: 12px; color: #999;">
                            © 2024 모의 면접 보고서
                    </div>
                    """;
            reportBuilder.append(footer);

            sendMailService.sendMail((String) model.getAttribute("loggedInEmail"), "Interviewee 보고서", reportBuilder.toString());
        } catch (Exception e) {
            Logger logger = Logger.getLogger("OpenAIService");
            logger.warning("Error generating report: " + e.getMessage());
            this.generateReport(data, model, e.getMessage(), repeat);
        }
    }
}