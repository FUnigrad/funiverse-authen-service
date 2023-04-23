package com.unigrad.funiverseauthenservice.service.impl;

import com.unigrad.funiverseauthenservice.entity.Role;
import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender notificationSender;

    private final JavaMailSender servicesSender;

    private final ResourceLoader resourceLoader;

    @Override
    public void send(EmailServer server, String from, String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = EmailServer.NOTIFICATION.equals(server) ? notificationSender.createMimeMessage() : servicesSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("noreply.funiverse@gmail.com", "%s via Funiverse".formatted(from));
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        if (EmailServer.NOTIFICATION.equals(server)) {
            notificationSender.send(message);
        } else {
            servicesSender.send(message);
        }
    }

    public void sendResetPasswordEmail(Token token) throws MessagingException, UnsupportedEncodingException {
        User user = token.getUser();

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

        send(EmailServer.SERVICES, user.getWorkspace().getName(), user.getPersonalMail(), subject, content);
    }

    @Override
    public void sendWelcomeEmail(Workspace workspace, User user, String password) throws IOException, MessagingException {
        String text = Role.WORKSPACE_ADMIN.equals(user.getRole()) ? WELCOME_ADMIN : WELCOME_USER;
        String content;
        if (Role.WORKSPACE_ADMIN.equals(user.getRole())) {
            content = String.format(text,
                    user.getPersonalMail(),
                    user.getPersonalMail(),
                    password,
                    user.getPersonalMail(),
                    user.getPersonalMail(),
                    user.getPersonalMail(),
                    user.getPersonalMail());
        } else {
            content = String.format(text,
                    user.getPersonalMail(),
                    user.getPersonalMail(),
                    workspace.getName(),
                    password,
                    user.getPersonalMail(),
                    user.getPersonalMail(),
                    user.getPersonalMail(),
                    user.getPersonalMail());
        }

        send(EmailServer.NOTIFICATION, workspace.getName(), user.getPersonalMail(), "Log into Workspace to connect to your University", content);
    }

    private final String WELCOME_USER = """
                <table border="0" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse:collapse">
                  <tbody>
                  <tr>
                    <td width="100%%" align="center">
                      <table border="0" cellspacing="0" cellpadding="0" align="center" style="border-collapse:collapse">
                        <tbody>
                        <tr>
                          <td width="1160" align="center">
                            <div style="max-width:580px;margin:0 auto" dir="ltr" bgcolor="#ffffff">
                              <table border="0" cellspacing="0" cellpadding="0" align="center"
                                     id="m_-8545930872533976427email_table"
                                     style="border-collapse:collapse;max-width:580px;margin:0 auto">
                                <tbody>
                                <tr>
                                  <td id="m_-8545930872533976427email_content" style="background-color:#f3f4f6">
                                    <table border="0" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse:collapse">
                                      <tbody>
                                      <tr>
                                        <td height="1" colspan="3" style="line-height:1px"><span
                                          style="color:#ffffff;font-size:1px;opacity:0">Let's get to work</span></td>
                                      </tr>
                                      <tr>
                                        <td width="100%%" colspan="3">
                                          <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                 style="border-collapse:collapse;margin:auto">
                                            <tbody>
                                            <tr>
                                              <td>
                                                <table border="0" cellspacing="0" cellpadding="0"
                                                       style="border-collapse:collapse;text-align:center;width:100%%">
                                                  <tbody>
                                                  <tr>
                                                    <td height="40" style="line-height:40px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td><a
                                                      href="http://funiverse.world/login?identifier=%s"
                                                      style="color:#1b74e4;text-decoration:none" target="_blank"
                                                      data-saferedirecturl="https://www.google.com/url?q=http://funiverse.world/login?identifier=%s"><img
                                                      width="250"
                                                      src="https://res.cloudinary.com/dhdwjnavi/image/upload/c_pad,b_auto:predominant,fl_preserve_transparency/v1681924902/logo-no-background_wuz0yx.jpg?_s=public-apps"
                                                      alt="FUniverse" style="border:0" class="CToWUd"
                                                      data-bit="iit"></a></td>
                                                  </tr>
                                                  <tr>
                                                    <td height="40" style="line-height:40px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                              </td>
                                            </tr>
                                            </tbody>
                                          </table>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td height="0" style="line-height:0px">&nbsp;</td>
                                      </tr>
                                      <tr>
                                        <td width="100%%">
                                          <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                 style="border-collapse:collapse">
                                            <tbody>
                                            <tr>
                                              <td>
                                                <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                       style="border-collapse:collapse;margin:auto;text-align:center">
                                                  <tbody>
                                                  <tr>
                                                    <td>
                                                      <table border="0" cellspacing="0" cellpadding="0"
                                                             style="border-collapse:collapse;max-width:580px;margin:0 auto;text-align:center;width:100%%">
                                                        <tbody>
                                                        <tr>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td width="100%%">
                                                            <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                                   style="border-collapse:collapse;background:#ffffff;text-align:center">
                                                              <tbody>
                                                              <tr>
                                                                <td height="50" style="line-height:50px" colspan="3">&nbsp;</td>
                                                              </tr>
                                                              <tr>
                                                                <td width="15" style="display:block;width:15px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                                <td>
                                                                  <table border="0" cellspacing="0" cellpadding="0"
                                                                         style="border-collapse:collapse">
                                                                    <tbody>
                                                                    <tr>
                                                                      <td><span
                                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:24px;font-weight:600;letter-spacing:-0.02em;line-height:32px;display:block">
                                                                        Hi, welcome to <span
                                                                        class="il">FUniverse</span></span></td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td height="25" style="line-height:25px" colspan="1">&nbsp;
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td><img
                                                                        src="https://res.cloudinary.com/dhdwjnavi/image/upload/c_pad,b_auto:predominant,fl_preserve_transparency/v1681925072/education_ym98f4.jpg?_s=public-apps"
                                                                        height="200px" style="border:0" class="CToWUd a6T"
                                                                        data-bit="iit" tabindex="0">
                                                                        <div class="a6S" dir="ltr"
                                                                             style="opacity: 0.01; left: 398.609px; top: 389.5px;">
                                                                          <div id=":1dv" class="T-I J-J5-Ji aQv T-I-ax7 L3 a5q"
                                                                               role="button" tabindex="0"
                                                                               aria-label="Tải xuống tệp đính kèm "
                                                                               jslog="91252; u014N:cOuCgd,Kr2w4b,xr6bB; 4:WyIjbXNnLWY6MTc2MDM0ODg4NjgzMzc5OTM3NiIsbnVsbCxbXV0."
                                                                               data-tooltip-class="a1V" data-tooltip="Tải xuống">
                                                                            <div class="akn">
                                                                              <div class="aSK J-J5-Ji aYr"></div>
                                                                            </div>
                                                                          </div>
                                                                        </div>
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td height="25" style="line-height:25px" colspan="1">&nbsp;
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td><span
                                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:18px;font-weight:300;line-height:26px;display:inline">
                                                                        FUniverse is the simple, secure and productive way to stay on top of what's happening at %s.
                                                                        Now that you've activated your account, log in to connect with your coworkers and find everything you need to get your job done.</span>
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td><span
                                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:18px;font-weight:300;line-height:26px;display:inline">
                                                                         Your password is %s</span>
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td height="25" style="line-height:25px" colspan="1">&nbsp;
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td>
                                                                        <center>
                                                                          <table border="0" cellspacing="0" cellpadding="0"
                                                                                 style="border-collapse:collapse">
                                                                            <tbody>
                                                                            <tr>
                                                                              <td align="center"
                                                                                  style="border-radius:6px;text-align:center;background-color:#F36527">
                                                                                <a
                                                                                  href="http://funiverse.world/login?identifier=%s"
                                                                                  style="color:#ffffff;text-decoration:none;text-align:center;font-size:17px;font-weight:600;line-height:140%%;border-radius:6px;padding:16px;border:1px solid #f36527;display:inline-block"
                                                                                  target="_blank"
                                                                                  data-saferedirecturl="https://www.google.com/url?q=http://funiverse.world/login?identifier=%s"><span
                                                                                  style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:18px;font-weight:300;line-height:26px;display:inline">Log into <span
                                                                                  class="il">FUniverse</span></span></a></td>
                                                                            </tr>
                                                                            </tbody>
                                                                          </table>
                                                                        </center>
                                                                      </td>
                                                                    </tr>
                                                                    </tbody>
                                                                  </table>
                                                                </td>
                                                                <td width="15" style="display:block;width:15px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                              </tr>
                                                              <tr>
                                                                <td height="50" style="line-height:50px" colspan="3">&nbsp;</td>
                                                              </tr>
                                                              </tbody>
                                                            </table>
                                                          </td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                        </tr>
                                                        </tbody>
                                                      </table>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                              </td>
                                            </tr>
                                            </tbody>
                                          </table>
                                          <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                 style="border-collapse:collapse;text-align:center">
                                            <tbody>
                                            <tr>
                                              <td height="50" style="line-height:50px" colspan="3">&nbsp;</td>
                                            </tr>
                                            <tr>
                                              <td width="40" style="display:block;width:40px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                              <td>
                                                <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                       style="border-collapse:collapse">
                                                  <tbody>
                                                  <tr>
                                                    <td>
                                                      <table border="0" cellspacing="0" cellpadding="0" align="center"
                                                             style="border-collapse:collapse">
                                                        <tbody>
                                                        <tr>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci4.googleusercontent.com/proxy/0lEKQqcbl-t0-Dh9k0xyST62JIOj9syC73Cp0Se_BDE1xPKPr56LQoaJwkuuE4hOzbBYEoLd4pE8pnqIiDUt7IaJtfJDTR5yWZBQQ1ijgQ=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/yu/r/QxSS3mev3ss.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci4.googleusercontent.com/proxy/Hni1T5oL5oRf5ku2YIWc-y6KZQe8W9EvEd8Yw3zKkQzGbummaJL5zgxRxaV9-5hYzmvj0W13fsjROOF24xaeN6pccY5J724LiIz6ATw5ow=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/y1/r/D7_E3RgzGLd.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci3.googleusercontent.com/proxy/8RwwVJR_aszQxaUoDHmN9MWg5rrUqJzyjtL-EooJxqLPqwlQePOUDCxnLuEj4zWaxHSh2c09USMAJT0puW_LeKclZZVE5RYNS4D2qLaDfg=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/y9/r/Bfs_fsszTf2.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci3.googleusercontent.com/proxy/OHGbij8e8KEJK6a8DRf9yiNeo4UuyiA9hmhITr7HpqKeKmR9x3rx3E12FcViHTkuJbfz6sH-WZf64nmW0PaZI6JkXexoJ6dU6k5IHKl_Ng=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/ys/r/1kIyqfjyT7D.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                        </tr>
                                                        </tbody>
                                                      </table>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="25" style="line-height:25px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:14px;font-weight:300;line-height:22px;color:#455766;display:inline">
                                                        FUnigrad, FPT University Da Nang</span>
                                                      </div>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:14px;font-weight:300;line-height:22px;color:#455766;display:inline">This message was sent to <a
                                                        href="mailto:%s"
                                                        style="color:inherit;text-decoration:none;font-weight:600"
                                                        target="_blank"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:inherit;font-weight:inherit;line-height:inherit;color:#4326c4;display:inline">
                                                        %s</span></a></span>
                                                      </div>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"></div>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"><a
                                                        href="http://funiverse.world/"
                                                        style="color:#1b74e4;text-decoration:none" target="_blank"
                                                        data-saferedirecturl="https://www.google.com/url?q=http://funiverse.world/"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:14px;font-weight:300;line-height:22px;color:#4326c4;display:inline;font-weight:600"><span
                                                        class="il">funiverse</span>.world </span></a></div>
                                                    </td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                              </td>
                                              <td width="40" style="display:block;width:40px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                            </tr>
                                            <tr>
                                              <td height="25" style="line-height:25px" colspan="3">&nbsp;</td>
                                            </tr>
                                            <tr>
                                              <td width="40" style="display:block;width:40px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                              <td height="25px" style="border-top:solid #dee4e9 1px"></td>
                                              <td width="20" style="display:block;width:20px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                            </tr>
                                            </tbody>
                                          </table>
                                        </td>
                                      </tr>
                        
                                </tr>
                                </tbody>
                              </table>
                            </div>
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  </tbody>
                </table>
            """;

    private final String WELCOME_ADMIN = """
                <table border="0" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse:collapse">
                  <tbody>
                  <tr>
                    <td width="100%%" align="center">
                      <table border="0" cellspacing="0" cellpadding="0" align="center" style="border-collapse:collapse">
                        <tbody>
                        <tr>
                          <td width="1160" align="center">
                            <div style="max-width:580px;margin:0 auto" dir="ltr" bgcolor="#ffffff">
                              <table border="0" cellspacing="0" cellpadding="0" align="center"
                                     id="m_-8545930872533976427email_table"
                                     style="border-collapse:collapse;max-width:580px;margin:0 auto">
                                <tbody>
                                <tr>
                                  <td id="m_-8545930872533976427email_content" style="background-color:#f3f4f6">
                                    <table border="0" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse:collapse">
                                      <tbody>
                                      <tr>
                                        <td height="1" colspan="3" style="line-height:1px"><span
                                          style="color:#ffffff;font-size:1px;opacity:0">Let's get to work</span></td>
                                      </tr>
                                      <tr>
                                        <td width="100%%" colspan="3">
                                          <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                 style="border-collapse:collapse;margin:auto">
                                            <tbody>
                                            <tr>
                                              <td>
                                                <table border="0" cellspacing="0" cellpadding="0"
                                                       style="border-collapse:collapse;text-align:center;width:100%%">
                                                  <tbody>
                                                  <tr>
                                                    <td height="40" style="line-height:40px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td><a
                                                      href="http://funiverse.world/login?identifier=%s"
                                                      style="color:#1b74e4;text-decoration:none" target="_blank"
                                                      data-saferedirecturl="https://www.google.com/url?q=http://funiverse.world/login?identifier=%s"><img
                                                      width="250"
                                                      src="https://res.cloudinary.com/dhdwjnavi/image/upload/c_pad,b_auto:predominant,fl_preserve_transparency/v1681924902/logo-no-background_wuz0yx.jpg?_s=public-apps"
                                                      alt="FUniverse" style="border:0" class="CToWUd"
                                                      data-bit="iit"></a></td>
                                                  </tr>
                                                  <tr>
                                                    <td height="40" style="line-height:40px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                              </td>
                                            </tr>
                                            </tbody>
                                          </table>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td height="0" style="line-height:0px">&nbsp;</td>
                                      </tr>
                                      <tr>
                                        <td width="100%%">
                                          <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                 style="border-collapse:collapse">
                                            <tbody>
                                            <tr>
                                              <td>
                                                <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                       style="border-collapse:collapse;margin:auto;text-align:center">
                                                  <tbody>
                                                  <tr>
                                                    <td>
                                                      <table border="0" cellspacing="0" cellpadding="0"
                                                             style="border-collapse:collapse;max-width:580px;margin:0 auto;text-align:center;width:100%%">
                                                        <tbody>
                                                        <tr>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td width="100%%">
                                                            <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                                   style="border-collapse:collapse;background:#ffffff;text-align:center">
                                                              <tbody>
                                                              <tr>
                                                                <td height="50" style="line-height:50px" colspan="3">&nbsp;</td>
                                                              </tr>
                                                              <tr>
                                                                <td width="15" style="display:block;width:15px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                                <td>
                                                                  <table border="0" cellspacing="0" cellpadding="0"
                                                                         style="border-collapse:collapse">
                                                                    <tbody>
                                                                    <tr>
                                                                      <td><span
                                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:24px;font-weight:600;letter-spacing:-0.02em;line-height:32px;display:block">
                                                                        Hi, welcome to <span
                                                                        class="il">FUniverse</span></span></td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td height="25" style="line-height:25px" colspan="1">&nbsp;
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td><img
                                                                        src="https://res.cloudinary.com/dhdwjnavi/image/upload/c_pad,b_auto:predominant,fl_preserve_transparency/v1681925072/education_ym98f4.jpg?_s=public-apps"
                                                                        height="200px" style="border:0" class="CToWUd a6T"
                                                                        data-bit="iit" tabindex="0">
                                                                        <div class="a6S" dir="ltr"
                                                                             style="opacity: 0.01; left: 398.609px; top: 389.5px;">
                                                                          <div id=":1dv" class="T-I J-J5-Ji aQv T-I-ax7 L3 a5q"
                                                                               role="button" tabindex="0"
                                                                               aria-label="Tải xuống tệp đính kèm "
                                                                               jslog="91252; u014N:cOuCgd,Kr2w4b,xr6bB; 4:WyIjbXNnLWY6MTc2MDM0ODg4NjgzMzc5OTM3NiIsbnVsbCxbXV0."
                                                                               data-tooltip-class="a1V" data-tooltip="Tải xuống">
                                                                            <div class="akn">
                                                                              <div class="aSK J-J5-Ji aYr"></div>
                                                                            </div>
                                                                          </div>
                                                                        </div>
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td height="25" style="line-height:25px" colspan="1">&nbsp;
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td><span
                                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:18px;font-weight:300;line-height:26px;display:inline">
                                                                        Your workspace is nearly complete, just a few steps remain to get started. Kindly log in and complete the configuration process.</span>
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td><span
                                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:18px;font-weight:300;line-height:26px;display:inline">
                                                                         Your password is %s</span>
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td height="25" style="line-height:25px" colspan="1">&nbsp;
                                                                      </td>
                                                                    </tr>
                                                                    <tr>
                                                                      <td>
                                                                        <center>
                                                                          <table border="0" cellspacing="0" cellpadding="0"
                                                                                 style="border-collapse:collapse">
                                                                            <tbody>
                                                                            <tr>
                                                                              <td align="center"
                                                                                  style="border-radius:6px;text-align:center;background-color:#F36527">
                                                                                <a
                                                                                  href="http://funiverse.world/login?identifier=%s"
                                                                                  style="color:#ffffff;text-decoration:none;text-align:center;font-size:17px;font-weight:600;line-height:140%%;border-radius:6px;padding:16px;border:1px solid #f36527;display:inline-block"
                                                                                  target="_blank"
                                                                                  data-saferedirecturl="https://www.google.com/url?q=http://funiverse.world/login?identifier=%s"><span
                                                                                  style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:18px;font-weight:300;line-height:26px;display:inline">Log into <span
                                                                                  class="il">FUniverse</span></span></a></td>
                                                                            </tr>
                                                                            </tbody>
                                                                          </table>
                                                                        </center>
                                                                      </td>
                                                                    </tr>
                                                                    </tbody>
                                                                  </table>
                                                                </td>
                                                                <td width="15" style="display:block;width:15px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                              </tr>
                                                              <tr>
                                                                <td height="50" style="line-height:50px" colspan="3">&nbsp;</td>
                                                              </tr>
                                                              </tbody>
                                                            </table>
                                                          </td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                        </tr>
                                                        </tbody>
                                                      </table>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                              </td>
                                            </tr>
                                            </tbody>
                                          </table>
                                          <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                 style="border-collapse:collapse;text-align:center">
                                            <tbody>
                                            <tr>
                                              <td height="50" style="line-height:50px" colspan="3">&nbsp;</td>
                                            </tr>
                                            <tr>
                                              <td width="40" style="display:block;width:40px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                              <td>
                                                <table border="0" width="100%%" cellspacing="0" cellpadding="0"
                                                       style="border-collapse:collapse">
                                                  <tbody>
                                                  <tr>
                                                    <td>
                                                      <table border="0" cellspacing="0" cellpadding="0" align="center"
                                                             style="border-collapse:collapse">
                                                        <tbody>
                                                        <tr>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci4.googleusercontent.com/proxy/0lEKQqcbl-t0-Dh9k0xyST62JIOj9syC73Cp0Se_BDE1xPKPr56LQoaJwkuuE4hOzbBYEoLd4pE8pnqIiDUt7IaJtfJDTR5yWZBQQ1ijgQ=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/yu/r/QxSS3mev3ss.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci4.googleusercontent.com/proxy/Hni1T5oL5oRf5ku2YIWc-y6KZQe8W9EvEd8Yw3zKkQzGbummaJL5zgxRxaV9-5hYzmvj0W13fsjROOF24xaeN6pccY5J724LiIz6ATw5ow=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/y1/r/D7_E3RgzGLd.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci3.googleusercontent.com/proxy/8RwwVJR_aszQxaUoDHmN9MWg5rrUqJzyjtL-EooJxqLPqwlQePOUDCxnLuEj4zWaxHSh2c09USMAJT0puW_LeKclZZVE5RYNS4D2qLaDfg=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/y9/r/Bfs_fsszTf2.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                          <td width="10" style="display:block;width:10px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                                          <td><a style="color:#1b74e4;text-decoration:none" target="_blank">
                                                            <img
                                                              width="36" height="36"
                                                              src="https://ci3.googleusercontent.com/proxy/OHGbij8e8KEJK6a8DRf9yiNeo4UuyiA9hmhITr7HpqKeKmR9x3rx3E12FcViHTkuJbfz6sH-WZf64nmW0PaZI6JkXexoJ6dU6k5IHKl_Ng=s0-d-e1-ft#https://static.xx.fbcdn.net/rsrc.php/v3/ys/r/1kIyqfjyT7D.png"
                                                              style="border:0" class="CToWUd" data-bit="iit"></a></td>
                                                        </tr>
                                                        </tbody>
                                                      </table>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="25" style="line-height:25px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:14px;font-weight:300;line-height:22px;color:#455766;display:inline">
                                                        FUnigrad, FPT University Da Nang</span>
                                                      </div>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:14px;font-weight:300;line-height:22px;color:#455766;display:inline">This message was sent to <a
                                                        href="mailto:%s"
                                                        style="color:inherit;text-decoration:none;font-weight:600"
                                                        target="_blank"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:inherit;font-weight:inherit;line-height:inherit;color:#4326c4;display:inline">
                                                        %s</span></a></span>
                                                      </div>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"></div>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td height="10" style="line-height:10px" colspan="1">&nbsp;</td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <div style="text-align: center;"><a
                                                        href="http://funiverse.world/"
                                                        style="color:#1b74e4;text-decoration:none" target="_blank"
                                                        data-saferedirecturl="https://www.google.com/url?q=http://funiverse.world/"><span
                                                        style="font-family:SF Pro Text,-apple-system,BlincMacSystemFont,Helvetica Neue,Helvetica,Lucida Grande,tahoma,verdana,arial,sans-serif;font-size:14px;font-weight:300;line-height:22px;color:#4326c4;display:inline;font-weight:600"><span
                                                        class="il">funiverse</span>.world </span></a></div>
                                                    </td>
                                                  </tr>
                                                  </tbody>
                                                </table>
                                              </td>
                                              <td width="40" style="display:block;width:40px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                            </tr>
                                            <tr>
                                              <td height="25" style="line-height:25px" colspan="3">&nbsp;</td>
                                            </tr>
                                            <tr>
                                              <td width="40" style="display:block;width:40px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                              <td height="25px" style="border-top:solid #dee4e9 1px"></td>
                                              <td width="20" style="display:block;width:20px" colspan="1">&nbsp;&nbsp;&nbsp;</td>
                                            </tr>
                                            </tbody>
                                          </table>
                                        </td>
                                      </tr>
                                </tr>
                                </tbody>
                              </table>
                            </div>
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  </tbody>
                </table>
            """;

    public enum EmailServer {
        NOTIFICATION, SERVICES
    }
}