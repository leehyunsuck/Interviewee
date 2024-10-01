package Shingu.Interviewer.servic;

import Shingu.Interviewer.entity.UserInfo;
import Shingu.Interviewer.tool.HashEncode;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterService {
    private final UserInfoService userInfoService;
    private final SendMailService sendMailService;

    private Map<String, Integer> verificationCodes = new HashMap<>();

    private static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public RegisterService(UserInfoService userInfoService, SendMailService sendMailService) {
        this.userInfoService = userInfoService;
        this.sendMailService = sendMailService;
    }

    public String register(String email, String password, String checkPassword, Model model) {
        // 규칙확인 로직
        if (!password.equals(checkPassword)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "register";
        }

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
        if (!isValidEmail(email) || email.length() > 320) {
            model.addAttribute("email", email);
            model.addAttribute("invalidEmail", true);
            return "register";
        }

        //이미 가입한 이메일 확인 로직
        if (userInfoService.isEmailExists(email)) {
            model.addAttribute("email", email);
            model.addAttribute("alreadyEmail", true);
            return "register";
        }

        //위 두 조건 다 통과면 인증번호 전송
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        //인증번호는 service/SendMailService 에 구현해놨으니 호출해서 쓰면 됨
        String content = "귀하의 인증번호는 " + code + "입니다."; // 이메일 내용
        sendMailService.sendMail(email, "이메일 인증", content); // 메일 전송
        model.addAttribute("code", code);
        model.addAttribute("email", email);
        verificationCodes.put(email, code);
        return "register";

    }
    public boolean verifyCode(String email, int code) {
        Integer storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }
}
