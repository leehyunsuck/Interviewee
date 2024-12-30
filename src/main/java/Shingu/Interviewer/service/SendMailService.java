package Shingu.Interviewer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

@Service
public class SendMailService {

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendMail(String to, String title, String content) {
        try {
            // MimeMessage 객체 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            // 이메일 설정
            helper.setFrom(username, "Interviewer Admin"); // 발신자 이름 설정
            helper.setTo(to); // 수신자 이메일 주소
            helper.setSubject(title); // 이메일 제목
            helper.setText(content, true); // HTML 지원 (true 설정)

            // 이메일 전송
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            Logger logger = Logger.getLogger(SendMailService.class.getName());
            logger.warning("Failed to send email: " + e.getMessage());
        }
    }
}
