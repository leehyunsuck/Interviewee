package Shingu.Interviewer.basic;

import Shingu.Interviewer.controller.CustomBotController;
import Shingu.Interviewer.dto.ChatGPTResponse;
import Shingu.Interviewer.tool.GetClientIP;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "GPTCall", value = "/GPTCall")
public class GPTCall extends HttpServlet {
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

        String temp = req.getParameter("count");
        if (temp == null) {
            res.sendRedirect("basic/index.jsp");
            return;
        }
        //받는 파라미터 변수에 저장
        String keyword = req.getParameter("keyword");
        Integer count = Integer.parseInt(req.getParameter("count"));
        String lang = req.getParameter("lang");
        String notQuestion = req.getParameter("notQuestion").isEmpty() ? "" : req.getParameter("notQuestion");
        String diff = req.getParameter("diff").equals("true") ? "Advanced" : "Beginner and Intermediate";

        HttpSession session = req.getSession();
        if (keyword.isEmpty()) {
            session.setAttribute("mainErrorMsg", "키워드를 입력하지 않았습니다.");
            res.sendRedirect("basic/index.jsp");
            return;
        } else if (keyword.length() > 30) {
            session.setAttribute("mainErrorMsg", "키워드의 길이는 30 이하로 입력해주세요");
            res.sendRedirect("basic/index.jsp");
            return;
        } else if (notQuestion.length() > 100) {
            session.setAttribute("notQuestionsErrorMsg", "받지 않을 키워드 길이는 100 이하로 입력해주세요");
            res.sendRedirect("basic/index.jsp");
            return;
        }

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


        //프롬프트에 받은 파라미터값 넣기
        String prompt = """
                Your role : interviewer
                                
                Technical interview type : [키워드]
                                
                Question difficulty : [난이도]
                                
                Total Question Count : [개수]
                                
                Respond language : [언어]
                                
                Rule :
                - Interview questions only.
                - Final question assesses character, not just knowledge, in [키워드] field.
                                
                                
                Exclusion List :
                - [중복제거]
                                
                Respond in JSON format:
                {
                    "Question 1": "Question Content",
                    "Question 2": "Question Content",
                    ...
                    "Question [개수]": "Question Content"
                }
                """
                .replace("[키워드]", keyword)
                .replace("[난이도]", diff)
                .replace("[개수]", String.valueOf(count))
                .replace("[언어]", lang)
                .replace("[중복제거]", notQuestion);

        log(prompt);
        //GPT API에 응답 호출
        ChatGPTResponse gptResponse = customBotController.chat(prompt);

        ipMap.put(ipAddress, ipMap.get(ipAddress) + 1);

        String s = gptResponse.getChoices().get(0).getMessage().getContent();

        log(s);
        log(ipAddress);
        log(ipMap.get(ipAddress).toString());

        //웹에 보내기 테스트
        JSONObject sendJson = new JSONObject(s);
        String[] questions = new String[count];
        String[] answers = new String[count];

        for (int i = 1; i <= count; i++) {
            if (sendJson.has("Question " + i)) questions[i - 1] = sendJson.getString("Question " + i);
            else if (sendJson.has(i + "")) questions[i - 1] = sendJson.getString(String.valueOf(i));
            else answers[i - 1] = "Error 404 Question";
        }

        session.setAttribute("questions", questions);
        session.setAttribute("keyword", keyword);
        session.setAttribute("answers", answers);
        session.setAttribute("index", 0);
        session.setAttribute("lang", lang);

        res.sendRedirect("/basic/showQuestions.jsp");
    }
}
