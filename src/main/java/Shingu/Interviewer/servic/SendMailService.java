package Shingu.Interviewer.servic;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Service
public class SendMailService {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    public boolean sendMail(String to, String title, String content) {
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
                    return new PasswordAuthentication(username, password);
                }
            };

            // 세션 생성
            Session session = Session.getInstance(properties, auth);

            // 이메일 생성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Interviewer Admin"));  // 발신자 설정
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));  // 수신자 설정
            message.setSubject(title);  // 제목 설정
            message.setContent(content, "text/html; charset=utf-8");

            // 이메일 전송
            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
