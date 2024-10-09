package Shingu.Interviewer.basic.updatePassword;

import Shingu.Interviewer.service.SendMailService;
import Shingu.Interviewer.service.UserInfoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "EmailSendUpdatePassword", value = "/emailSendUpdatePassword")
public class EmailSendUpdatePassword extends HttpServlet {

    @Value("${spring.mail.username}")
    private String host;

    @Value("${spring.mail.password}")
    private String mailPassword;
    private static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private final UserInfoService userInfoService;
    private final SendMailService sendMailService;

    public EmailSendUpdatePassword(UserInfoService userInfoService, SendMailService sendMailService) {
        this.userInfoService = userInfoService;
        this.sendMailService = sendMailService;
    }

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.service(req, resp);
        String email = req.getParameter("email");

        req.getSession().setAttribute("invalidEmail", null);
        req.getSession().setAttribute("notFoundEmail", null);

        //이메일 패턴이 아니거나 이메일 주소가 말도 안되게 길 때 다시 작성하게 보내기
        if (!isValidEmail(email) || email.length() > 320) {
            req.getSession().setAttribute("email", email);
            req.getSession().setAttribute("invalidEmail", true);
            resp.sendRedirect("/basic/findAccount.jsp");
            return;
        }

        //이미 있는 이메일인지 확인 하는 코드 부분
        if (!userInfoService.isEmailExists(email)) {
            req.getSession().setAttribute("email", email);
            req.getSession().setAttribute("notFoundEmail", true);
            resp.sendRedirect("/basic/findAccount.jsp");
            return;
        }

        Random random = new Random();
        int code = random.nextInt(900000) + 100000;

        String title = "Interviewer 비밀번호 변경 인증코드 전송";
        String htmlContent =
                """
                <h1> Interviewer 인증 코드 </h1>
                <p style="font-size:12px; color:red;"> 해당 코드는 비밀번호 변경을 위한 정보이므로 타인에게 알려주지 마세요. </p>
                <p> [인증코드] </p>
                """.replace("[인증코드]", String.valueOf(code));

//        if (sendMailService.sendMail(email, title, htmlContent)) {
//
//        } else {
//            //error
//        }

        req.getSession().setAttribute("email", email);
        req.getSession().setAttribute("code", code);
        resp.sendRedirect("/basic/findAccount.jsp");
    }
}
