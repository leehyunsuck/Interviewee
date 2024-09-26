package Shingu.Interviewer;

import Shingu.Interviewer.servic.LoginService;
import Shingu.Interviewer.tool.GetClientIP;
import Shingu.Interviewer.tool.JobCompletionEncode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Shingu.Interviewer.servic.UserInfoService;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Controller
@EnableScheduling
@RequestMapping("/")
public class MainController {
    private final LoginService loginService;
    public MainController(LoginService loginService) {
        this.loginService = loginService;
    }

    private Map<String, Integer> ipMap = new HashMap<>();       // 유저IP : 요청 횟수
    private static final int MAX_REQUESTS_PER_MINUTE = 100;     // 분당 최대 요청 횟수
    @Scheduled(cron = "0 * * * * *")                            // 1분 마다 요청 횟수 Map 초기화
    public void resetIpMap() {
        ipMap.clear();
    }

    // 공통 처리 부분
    @ModelAttribute
    public void commonAttributes(Model model, HttpSession session, HttpServletRequest request) {
        // 트래픽 공격 막기
        String ipAddress = GetClientIP.getClientIP(request);
        Integer requestCount = ipMap.getOrDefault(ipAddress, 0) + 1;
        if (requestCount > MAX_REQUESTS_PER_MINUTE) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "너무 많은 요청을 보내셨습니다. 서버 과부하 방지를 위해 1분뒤 다시 요청해주세요");
        ipMap.put(ipAddress, requestCount);

        // 세션에 email 정보 있으면 공통적으로 model에 담기
        if (session.getAttribute("email") != null) model.addAttribute("email", session.getAttribute("email"));
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("login")
    public String login(Model model) {
        if (model.getAttribute("email") != null) return "index";
        return "login";
    }
    @PostMapping("login")
    public String main(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        return loginService.login(email, password, model, session);
    }
}