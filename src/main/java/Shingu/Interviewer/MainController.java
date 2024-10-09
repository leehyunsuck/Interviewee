package Shingu.Interviewer;

import Shingu.Interviewer.service.*;
import Shingu.Interviewer.tool.GetClientIP;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Controller
@EnableScheduling
@RequestMapping("/")
public class MainController {
    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private CreateVoiceService createVoiceService;

    @Autowired  // Audio 반환 서비스
    private GetAudioService getAudioService;

    @Autowired  // 회원가입 서비스
    private RegisterService registerService;

    @Autowired  // 로그인 서비스
    private LoginService loginService;

    private final Map<String, Integer> countMap = new HashMap<>();
    private final Map<String, Integer> ipMap = new HashMap<>();     // 유저IP : 요청 횟수
    private static final int MAX_REQUESTS_PER_MINUTE = 100;         // 분당 최대 요청 횟수
    @Scheduled(cron = "0 * * * * *")                                // 1분 마다 요청 횟수 Map 초기화
    public void resetIpMap() {
        ipMap.clear();
    }
    @Scheduled(cron = "0 0 0 * * *")
    public void restCountMap() {
        countMap.clear();
    }

    // 공통 처리 부분
    @ModelAttribute
    public void commonAttributes(Model model, HttpSession session, HttpServletRequest request) {
        // 트래픽 공격 막기
        String ipAddress = GetClientIP.getClientIP(request);
        int requestCount = ipMap.getOrDefault(ipAddress, 0) + 1;
        if (requestCount > MAX_REQUESTS_PER_MINUTE) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "너무 많은 요청을 보내셨습니다. 서버 과부하 방지를 위해 1분뒤 다시 요청해주세요");
        ipMap.put(ipAddress, requestCount);

        // 세션에 email 정보 있으면 공통적으로 model에 담기, 없으면 세션 초기화
        if (session.getAttribute("loggedInEmail") != null) model.addAttribute("loggedInEmail", session.getAttribute("loggedInEmail"));
        else session.invalidate();
    }

    @GetMapping
    public String index() {
        return "main";
    }

    @GetMapping("login")
    public String login(Model model) {
        if (model.getAttribute("loggedInEmail") != null) return "main";
        return "login";
    }
    @PostMapping("login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        return loginService.login(email, password, model, session);
    }

    @GetMapping("register")
    public String register(Model model, HttpSession session) {
        if (model.getAttribute("loggedInEmail") != null) return "main";
        model.addAttribute("action", "sendMail");
        return "register";
    }
    @PostMapping("sendMail")
    public String sendMail(@RequestParam("email") String email, Model model) {
        return registerService.sendMail(model, email);
    }
    @PostMapping("checkCode")
    public String checkCode(@RequestParam("email") String email, @RequestParam("code") String code, Model model) {
        return registerService.checkCode(email, code, model);
    }
    @PostMapping("register")
    public String register(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("password2") String password2, Model model) {
        return registerService.register(email, password, password2, model);
    }

    @GetMapping("success_register")
    public String success_register(Model model) {
        return "success_register";
    }

    @GetMapping("main")
    public String main(Model model) {
        return "main";
    }

    @GetMapping("/mp3/{filename:.+}")   // 오디오 파일 반환
    public ResponseEntity<Resource> getAudioFile(@PathVariable String filename) {
        return getAudioService.getAudioFile(filename);
    }

    @GetMapping("interview")
    public String interview(Model model, HttpServletRequest request) {
        List<String> questions = Arrays.asList("사용해본 프로그래밍 언어 중 가장 자신 있는 언어는 무엇이며, 그 언어를 사용해 해결한 문제나 프로젝트 경험을 설명해 주세요.",
                "최근에 배운 새로운 기술이나 도구는 무엇이며, 이를 어떻게 실무에 적용할 계획인가요?",
                "복잡한 문제를 해결하기 위해 사용한 알고리즘이나 자료 구조에 대해 설명해 주세요.",
                "팀 프로젝트에서의 협업 경험을 공유해 주시고, 코드 리뷰나 Git을 사용한 버전 관리 경험이 있나요?",
                "백엔드(또는 프론트엔드) 개발에서 가장 중요한 요소는 무엇이라고 생각하며, 그 이유는 무엇인가요?"
        );
        model.addAttribute("questions", questions);
        model.addAttribute("audioFiles", createVoiceService.createVoice(questions, GetClientIP.getClientIP(request)));

        return "interview";
    }

    @PostMapping("finish_interview")
    public String finishInterview(@RequestParam Map<String, String> data, Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String answersJson = data.get("answers");
        String questionsJson = data.get("questions");

        List<String> answers = objectMapper.readValue(answersJson, new TypeReference<>() {});
        List<String> questions = objectMapper.readValue(questionsJson, new TypeReference<>() {});

        model.addAttribute("answers", answers);
        model.addAttribute("questions", questions);
        return "finish_interview";
    }

    @PostMapping("generateReport")
    public String generateReport(@RequestParam Map<String, String> data, Model model) throws JsonProcessingException {
        // 보고서 받을 수 없는 상황인데 네트워크 조작으로 뚫은 경우 막기
        if (model.getAttribute("loggedInEmail") == null) {
            model.addAttribute("errorMsg", "옳바른 접근이 아닙니다.");
            model.addAttribute("hrefValue", "login");
            return "error";
        }

        String email = (String) model.getAttribute("loggedInEmail");
        int count = countMap.getOrDefault(email, 0);
        if (count > 5 && !email.equals("5talk2394ty76@gmail.com")) {
            model.addAttribute("errorMsg", "한 계정당 하루 최대 5번 사용 가능합니다.");
            model.addAttribute("hrefValue", "main");
            return "error";
        }
        countMap.put(email, count + 1);

        openAIService.generateReport(data, model, "", 0);  //비동기 로직
        return "report_send";
    }
}