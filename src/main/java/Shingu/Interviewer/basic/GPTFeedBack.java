package Shingu.Interviewer.basic;

import Shingu.Interviewer.controller.CustomBotController;
import Shingu.Interviewer.dto.ChatGPTResponse;
import Shingu.Interviewer.tool.GetClientIP;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

@WebServlet(name = "GPTFeedBack", value = "/GPTFeedBack")
public class GPTFeedBack extends HttpServlet {
    @Autowired
    CustomBotController customBotController;

    private HashMap<String, Integer> ipMap = new HashMap<>();

    //00시 지나면 Map 초기화
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetIpMap() {
        ipMap.clear();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String temp = (String) req.getSession().getAttribute("lang");
        if (temp == null) {
            res.sendRedirect("/basic/index.jsp");
            return;
        }

        String[] questions = (String[]) req.getSession().getAttribute("questions");
        String[] answers = (String[]) req.getSession().getAttribute("answers");
        String lang = (String) req.getSession().getAttribute("lang");

        //IP
        String ipAddress = GetClientIP.getClientIP(req);
        Integer useCount = ipMap.containsKey(ipAddress) ? ipMap.get(ipAddress) : 0;
        if (useCount == 0) ipMap.put(ipAddress, useCount);

        if (req.getSession().getAttribute("loginEmail") != null) {
            if (useCount >= 6) {
                res.sendRedirect("basic/index.jsp");
                return;
            }
        } else if (useCount >= 3) {
            res.sendRedirect("basic/index.jsp");
            return;
        }

        String exampleEng =
        """
        {
          "FeedBack 1": "Insufficient: The information required for the answer is missing (explain what is missing).",
          "FeedBack 2": "Perfect: The answer perfectly addresses the question."
          "FeedBack 3": "Incorrect: The answer has a problem. (explain what is wrong and provide the correct information)."
        }
        """;

        String exampleKor =
        """
        {
            "FeedBack 1": "불충분: 답변에 필요한 정보가 누락됨 (누락된 내용을 설명).",
            "FeedBack 2": "완벽: 답변이 질문을 완벽하게 다룸."
            "FeedBack 3": "부정확: 답변에 문제가 있음. (틀린 부분을 설명하고 올바른 정보를 제공)."
            "FeedBack 4": "불충분 + 부정확: 답변에 필요한 정보가 누락되었고 틀린 부분이 있음. (누락된 내용을 설명하고 올바른 정보를 제공)."
        }
        """;

        String prompt = """
        As an interviewer, you must judge the accuracy and integrity of the answers.
        Evaluate the questions and answers based on 'Perfect', 'Insufficient', or 'Incorrect'.
        The judgments can be mixed. For example, if the content is both insufficient and incorrect, judge it as 'Insufficient + Incorrect'.
        Then, explain the reasons for your judgment.
        This is an interview, a conversation.
        The response language is [언어].
                        
        - 'Incorrect': Explain which part is incorrect and why it is wrong, and provide the correct information.
        - 'Insufficient': Explain what content is missing and provide a specific example of how to make it perfect.
                          
                        
        There are [개수] questions and answers, so generate [개수] pieces of feedback.
        
        Don't write your answers in feedback as they are. Just write where you need to explain.
        
        Make sure to match each Question and Answers index number
                        
        Questions:
        [질문]
                        
        Answers:
        [답변]
                        
        Respond in JSON format:
        {
          "FeedBack 1": "'Judgment' + 'Reason' format",
          "FeedBack 2": "'Judgment' + 'Reason' format",
          ...
        }
               
        Example:
        [예시]
        """
                .replace("[개수]", questions.length + "")
                .replace("[질문]", Arrays.toString(questions))
                .replace("[답변]", Arrays.toString(answers))
                .replace("[언어]", lang)
                .replace("Perfect", lang.equals("Korean") ? "완벽" : "Perfect")
                .replace("Insufficient", lang.equals("Korean") ? "불충분" : "Insufficient")
                .replace("Incorrect", lang.equals("Korean") ? "부정확" : "Incorrect")
                .replace("[예시]", lang.equals("Korean") ? exampleKor : exampleEng);

        log(prompt);

        //GPT API에 응답 호출
        ChatGPTResponse gptResponse = customBotController.chat(prompt);

        String s = gptResponse.getChoices().get(0).getMessage().getContent();


        log(s);
        log(ipAddress);

        //웹에 보내기 테스트
        JSONObject sendJson = new JSONObject(s);
        String[] feedbacks = new String[questions.length];

        for (int i = 1; i <= questions.length; i++) {
            try {
                if (sendJson.has("FeedBack " + i)) {
                    feedbacks[i - 1] = sendJson.getString("FeedBack " + i);
                } else if (sendJson.has("피드백 " + i)) {
                    feedbacks[i - 1] = sendJson.getString("피드백 " + i);
                } else if (sendJson.has(String.valueOf(i))){
                    feedbacks[i - 1] = sendJson.getString(String.valueOf(i));  // i 값을 문자열로 변환하여 할당
                }
            } catch (Exception e) {
                feedbacks[i - 1] = "서비스 이용에 불편을 드려 죄송합니다. 통신과정에 문제가 발생하였습니다.";
            }
        }

        req.getSession().setAttribute("feedbacks", feedbacks);

        res.sendRedirect("/save/questions");
    }
}
