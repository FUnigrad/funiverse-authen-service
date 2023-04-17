package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender notificationSender;

    private final JavaMailSender servicesSender;

    @Override
    public void send(EmailServer server, String from, String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply.funiverse@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        if (EmailServer.NOTIFICATION.equals(server)) {
            notificationSender.send(message);
        } else {
            servicesSender.send(message);
        }
    }

    public void sendEmailResetPassword(Token token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = servicesSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        User user = token.getUser();
        helper.setFrom("noreply.funiverse@gmail.com", "%s via FUniverse".formatted(user.getWorkspace().getName()));
        helper.setTo(user.getPersonalMail());
        String subject = "%s is your Workspace account recovery code".formatted(token.getToken());
        String otp = """
                <td style="display:inline-block;margin-left:4px;margin-right:4px;text-align:center">
                    <span style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;
                    font-size:28px;font-weight:600;letter-spacing:-0.03em;line-height:36px;display:block">
                        %s
                    </span>
                </td>
                """;

        StringBuilder otpHtml = new StringBuilder();
        for (String s : token.getToken().split("")) {
            otpHtml.append(otp.formatted(s));
        }

        String buttonChangePasswordHtml = """
                <table style="background-color: #1b74e4; text-align: center; width: 100%">
                        <tr>
                          <td>
                            <a
                              href="https://"
                              style="color:#1b74e4;text-decoration:none;display:block" target="_blank"
                              data-saferedirecturl="https://www.google.com/url?q=https://">
                              <center><font size="3"><span
                                style="font-family:-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;white-space:nowrap;font-weight:bold;vertical-align:middle;font-family:-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-weight:bold;font-size:16px;color:#ffffff">Change&nbsp;password</span></font>
                              </center>
                            </a>
                          </td>
                        </tr>
                </table>
                """;
        String content = """
                <div>
                  <p>
                    Hi %s,
                  </p>
                  <p>
                    We received a request to reset your Workspace password.<br>
                    Enter the following password reset code:
                  </p>
                  <table
                         style="border-collapse:separate;background-color:#f3f4f6;text-align:center;padding:15px">
                    <tbody>
                    <tr>
                       %s
                    </tr>
                    </tbody>
                  </table>
                  <p>
                    Alternatively, you can directly change your password.
                  </p>
                  %s
                </div>
                """.formatted(user.getEduMail(), otpHtml.toString(), buttonChangePasswordHtml);
        helper.setSubject(subject);
        helper.setText(content, true);

        servicesSender.send(message);
    }

    public enum EmailServer {
        NOTIFICATION,
        SERVICES
    }
}