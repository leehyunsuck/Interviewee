package Shingu.Interviewer;

import Shingu.Interviewer.tool.JobCompletionEncode;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import Shingu.Interviewer.servic.UserInfoService;

@Controller
@RequestMapping("/")
public class MainController {

    private final UserInfoService userInfoService;

    public MainController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        if (session.getAttribute("email") != null) model.addAttribute("email", session.getAttribute("email"));
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("login")
    public String login(Model model) {
        if (model.getAttribute("email") != null) {
            return "index";
        }
        return "login";
    }

    @PostMapping("login")
    public String main(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {


        String hashPassword = JobCompletionEncode.encode(email, password);
        if (!userInfoService.isEmailExists(email)) {
            model.addAttribute("errorMsg", "가입되지 않은 이메일 입니다.");
            return "login";
        }

        if (!userInfoService.isAccountExists(email, hashPassword)) {
            model.addAttribute("errorMsg", "옳바르지 않은 비밀번호 입니다.");
            return "login";
        }

        session.setAttribute("email", email);
        model.addAttribute("email", email);
        return "index";
    }
}