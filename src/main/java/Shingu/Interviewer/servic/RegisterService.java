package Shingu.Interviewer.servic;

import Shingu.Interviewer.entity.UserInfo;
import Shingu.Interviewer.tool.HashEncode;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class RegisterService {
    private final UserInfoService userInfoService;

    public RegisterService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public String register(String email, String password, String checkPassword, Model model) {
        // 다음 확인사항 안지켜진거면 클라이언트 조작
        if (password.length() < 8 || password.length() > 16) {
        }

        if (!password.equals(checkPassword)) {
        }



        String hashPassword = HashEncode.encode(email, password);

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setPassword(hashPassword);
        userInfoService.saveUser(userInfo);
        model.addAttribute("loggedInEmail", email);
        return "registerSuccess";
    }

    public  String registerSendMail(Model model, String email) {
        //이메일 유형 확인
        if (userInfoService.isEmailExists(email)) {
            model.addAttribute("errorMsg", "이미 가입된 이메일 입니다.");
            return "register";
        }

        return "";
    }
}
