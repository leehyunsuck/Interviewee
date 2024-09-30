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
        // 규칙확인 로직

        // 규칙 다 지켜졌으면 DB에 정보 저장
        String hashPassword = HashEncode.encode(email, password);

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setPassword(hashPassword);
        userInfoService.saveUser(userInfo);
        model.addAttribute("loggedInEmail", email);
        return "success_register";
    }

    public  String registerSendMail(Model model, String email) {
        //이메일 유형 확인 로직

        //이미 가입한 이메일 확인 로직

        //위 두 조건 다 통과면 인증번호 전송
        //인증번호는 service/SendMailService 에 구현해놨으니 호출해서 쓰면 됨
        return "";
    }
}
