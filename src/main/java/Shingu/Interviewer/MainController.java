package Shingu.Interviewer;

import Shingu.Interviewer.entity.JobTitle;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@EnableScheduling
@RequestMapping("/")
public class MainController {
    @Autowired
    private JobTitleService jobTitleService;

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

    private final Map<String, Integer> reportGeneratecountMap = new HashMap<>();
    private final Map<String, Integer> questionGenerateCountMap = new HashMap<>();
    private final Map<String, Integer> ipMap = new HashMap<>();     // 유저IP : 요청 횟수
    private static final int MAX_REQUESTS_PER_MINUTE = 100;         // 분당 최대 요청 횟수
    @Scheduled(cron = "0 * * * * *")                                // 1분 마다 요청 횟수 Map 초기화
    public void resetIpMap() {
        ipMap.clear();
    }
    @Scheduled(cron = "0 0 0 * * *")
    public void restCountMap() {
        reportGeneratecountMap.clear();
        questionGenerateCountMap.clear();
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

    @GetMapping("logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate();
        model.asMap().clear();
        return "main";
    }

    @GetMapping("/mp3/{filename:.+}")   // 오디오 파일 반환
    public ResponseEntity<Resource> getAudioFile(@PathVariable String filename) {
        return getAudioService.getAudioFile(filename);
    }

    @PostMapping("interview")
    public String interviewPost(Model model, HttpServletRequest request) {
        // 질문 요청 맵 필요
        String email = (String) model.getAttribute("loggedInEmail");
        String address = GetClientIP.getClientIP(request);
        int count = questionGenerateCountMap.getOrDefault(address, 0);
        if (count > 5 && !email.equals("5talk2394ty76@gmail.com")) {
            model.addAttribute("errorMsg", "한 계정당 하루 최대 5번 사용 가능합니다.");
            model.addAttribute("hrefValue", "main");
            return "error";
        }
        questionGenerateCountMap.put(address, count + 1);

        List<String> questions = openAIService.generateQuestions(request, "", 0);

        if (questions.isEmpty()) {
            model.addAttribute("errorMsg", "질문을 생성하는 동안 에러가 발생했습니다.\n다시 시도해주세요");
            model.addAttribute("hrefValue", "resume");
            return "error";
        }

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
        int count = reportGeneratecountMap.getOrDefault(email, 0);
        if (count > 5 && !email.equals("5talk2394ty76@gmail.com")) {
            model.addAttribute("errorMsg", "한 계정당 하루 최대 5번 사용 가능합니다.");
            model.addAttribute("hrefValue", "main");
            return "error";
        }
        reportGeneratecountMap.put(email, count + 1);

        openAIService.generateReport(data, model, "", 0);  //비동기 로직
        return "report_send";
    }


    @Transactional
    @GetMapping(value = "resumeEntity", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<Map<String, Object>>> resumeEntity() {
        List<JobTitle> jobTitles = jobTitleService.getAllJobTitles();
        List<Map<String, Object>> mockJobData = new ArrayList<>();

        jobTitles.stream()
                .collect(Collectors.groupingBy(
                        jobTitle -> jobTitle.getCategory().getCategoryName(),
                        Collectors.toList()
                ))
                .forEach((categoryName, jobTitlesInCategory) -> {
                    Map<String, Object> jobData = new HashMap<>();
                    int id = jobTitlesInCategory.get(0).getCategory().getId().intValue();
                    jobData.put("id", id);
                    jobData.put("name", categoryName);
                    jobData.put("subOptions", jobTitlesInCategory.stream()
                            .map(JobTitle::getArmy)
                            .collect(Collectors.toList()));
                    mockJobData.add(jobData);
                });

        return ResponseEntity.ok(mockJobData); // mockJobData를 JSON 형식으로 반환
    }

    @GetMapping("resume")
    public String resume(Model model) {
        return "resume";
    }
}