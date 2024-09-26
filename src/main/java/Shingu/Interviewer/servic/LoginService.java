package Shingu.Interviewer.servic;

import Shingu.Interviewer.tool.JobCompletionEncode;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class LoginService {
    private final UserInfoService userInfoService;

    public LoginService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public String login(String email, String password, Model model, HttpSession session) {
        // 가입 여부 확인
        if (!userInfoService.isEmailExists(email)) {
            model.addAttribute("errorMsg", "가입되지 않은 이메일 입니다.");
            return "login";
        }

        // 로그인 정보 일치 확인
        String hashPassword = JobCompletionEncode.encode(email, password);
        if (!userInfoService.isAccountExists(email, hashPassword)) {
            model.addAttribute("errorMsg", "옳바르지 않은 비밀번호 입니다.");
            return "login";
        }

        session.setAttribute("email", email);
        model.addAttribute("email", email);
        return "index";
    }
}
