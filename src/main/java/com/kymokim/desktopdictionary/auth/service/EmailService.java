package com.kymokim.desktopdictionary.auth.service;

import com.kymokim.desktopdictionary.common.service.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${app.mail.from:noreply@example.com}")
    private String fromAddress;

    private int verificationCode;

    public boolean verifyEmail(String email, String verificationCode) {
        if (redisUtil.getData(verificationCode) == null) {
            return false;
        } else if (redisUtil.getData(verificationCode).equals(email)) {
            return true;
        } else {
            return false;
        }
    }

    public void makeRandomNumber() {
        Random random = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(random.nextInt(10));
        }

        verificationCode = Integer.parseInt(randomNumber);
    }

    public String writeEmail(String email) {
        makeRandomNumber();
        String setFrom = fromAddress;
        String toMail = email;
        String title = "Desktop Dictionary 가입 인증 메일입니다.";
        String content =
                "안녕하세요 Desktop Dictionary입니다."
                        + "<br><br>"
                        + "인증 번호는 " + verificationCode + "입니다."
                        + "<br>"
                        + "인증번호를 입력해주시면 인증이 완료됩니다.";
        sendEmail(setFrom, toMail, title, content);
        return Integer.toString(verificationCode);
    }

    public void sendEmail(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        redisUtil.setDataExpire(Integer.toString(verificationCode), toMail, 60 * 5L);
    }
}
