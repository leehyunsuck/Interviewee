package Shingu.Interviewer.basic.register;

import Shingu.Interviewer.servic.UserInfoService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "EmailSend", value = "/emailSendRegister")
public class EmailSendRegister extends HttpServlet {

    @Value("${spring.mail.username}")
    private String host;

    @Value("${spring.mail.password}")
    private String mailPassword;

    private static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private final UserInfoService userInfoService;

    public EmailSendRegister(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
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
        req.getSession().setAttribute("alreadyEmail", null);

        //이메일 패턴이 아니거나 이메일 주소가 말도 안되게 길 때 다시 작성하게 보내기
        if (!isValidEmail(email) || email.length() > 320) {
            req.getSession().setAttribute("email", email);
            req.getSession().setAttribute("invalidEmail", true);
            resp.sendRedirect("/basic/register.jsp");
            return;
        }

        //이미 있는 이메일인지 확인 하는 코드 부분
        if (userInfoService.isEmailExists(email)) {
            req.getSession().setAttribute("email", email);
            req.getSession().setAttribute("alreadyEmail", true);
            resp.sendRedirect("/basic/register.jsp");
            return;
        }
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;

        //이메일 전송 구현
        try {
            // 메일 서버 설정
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            // Gmail 인증
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(host, mailPassword);
                }
            };

            // 세션 생성
            Session session = Session.getInstance(properties, auth);

            // 이메일 생성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(host, "취업완 모의면접 인증코드 발신용"));  // 발신자 설정
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));  // 수신자 설정
            message.setSubject("취업완 회원가입 인증코드를 전송해드립니다.");  // 제목 설정

            // HTML 내용 생성
            String htmlContent = "<h1>취업완 (모의면접) 인증 코드</h1>"
                    + "<p>귀하의 인증 코드는 다음과 같습니다:</p>"
                    + "<p style=\"font-size:18px; color:blue;\">" + code + "</p>"
                    + "<p style=\"font-size:12px; color:red;\"> 해당 코드는 회원가입을 위한 정보이므로 타인에게 알려주지 마세요.";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            // 이메일 전송
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        req.getSession().setAttribute("email", email);
        req.getSession().setAttribute("code", code);
        resp.sendRedirect("/basic/register.jsp");
    }
}
