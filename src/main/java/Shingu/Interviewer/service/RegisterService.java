package Shingu.Interviewer.service;

import Shingu.Interviewer.entity.UserInfo;
import Shingu.Interviewer.tool.HashEncode;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterService {
    private final UserInfoService userInfoService;
    private final SendMailService sendMailService;

    private Map<String, Integer> verificationCodes = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
        model.addAttribute("email", email);
        model.addAttribute("action", "register");
        //이메일 유형 확인 로직
        if (!isValidEmail(email) || email.length() > 320) {
            model.addAttribute("errorMsg", "");
        } else if (userInfoService.isEmailExists(email)) {
            model.addAttribute("errorMsg", "이미 가입된 이메일 입니다.");
        } else if (!password.equals(checkPassword)) {
            model.addAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
        } else if (password.length() < 8 || password.length() > 16) {
            model.addAttribute("errorMsg", "비밀번호는 8자 이상 16자 이하로 입력해주세요.");
        } else if (verificationCodes.getOrDefault(email, 0) != 999999999) {
            model.addAttribute("errorMsg", "이메일 인증을 하지 않았습니다.");
        } else {
            verificationCodes.remove(email);

            // 규칙 다 지켜졌으면 DB에 정보 저장
            String hashPassword = HashEncode.encode(email, password);

            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(email);
            userInfo.setPassword(hashPassword);
            userInfoService.saveUser(userInfo);

            model.addAttribute("loggedInEmail", email);
            return "success_register";
        }
        return "register";
    }

    public  String sendMail(Model model, String email) {
        //이메일 유형 확인 로직
        if (!isValidEmail(email) || email.length() > 320) {
            model.addAttribute("errorMsg", "유효한 이메일 형식이 아닙니다");
            model.addAttribute("hrefValue", "register");
            return "error";
        }

        //이미 가입한 이메일 확인 로직
        if (userInfoService.isEmailExists(email)) {
            model.addAttribute("errorMsg", "이미 가입된 이메일 입니다.");
            model.addAttribute("hrefValue", "register");
            return "error";
        }
        model.addAttribute("action", "checkCode");
        model.addAttribute("email", email);

        Random random = new Random();
        int code = random.nextInt(900000) + 100000;

        String content = """
            <div class="container" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); padding: 20px;">
                <div class="header"; margin-bottom: 20px;">
                    <h2 style="color: #3D5AED; font-size: 24px; margin: 0;">Interviewee 인증코드</h2>
                </div>
                <div class="content" style="font-size: 18px; line-height: 1.6; margin-bottom: 20px; color: #333;">
                    <p>안녕하세요,</p>
                    <p>귀하의 인증번호는 <strong style="color: #3D5AED;">%s</strong>입니다.</p>
                    <p>인증번호를 입력하여 본인 확인을 완료해 주세요.</p>
                </div>
                <div class="footer" style="text-align: center; margin-top: 20px; font-size: 14px; color: #999;">
                    <p>감사합니다!</p>
                    <p><a href="https://interviewee.kro.kr" style="color: #3D5AED; text-decoration: none;">Interviewee</a></p>
                </div>
            </div>
            """.replace("%s", code + "");
        sendMailService.sendMail(email, "이메일 인증", content); // 메일 전송
        model.addAttribute("action", "checkCode");
        verificationCodes.put(email, code);

        // 10분뒤 인증정보 삭제
        scheduler.schedule(() -> verificationCodes.remove(email), 10, TimeUnit.MINUTES);

        return "register";
    }
    public String checkCode(String email, String code, Model model) {
        model.addAttribute("email", email);

        String storedCode = String.valueOf(verificationCodes.get(email));

        if (storedCode.equals(code)) {
            model.addAttribute("errorMsg", "");
            model.addAttribute("action", "register");
        } else {
            model.addAttribute("errorMsg", "인증번호가 일치하지 않습니다.");
            model.addAttribute("action", "checkCode");
        }

        verificationCodes.put(email, 999999999);

        return "register";
    }
}
