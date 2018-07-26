package com.ybg.component.email.sendemail;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.ybg.component.email.EmailConstant;

public class Send163EmailImpl implements SendEmailInter {
	
	@Override
	public void sendMail(String toEmail, String title, String centent) throws Exception {
		Properties properties = new Properties();// 创建Properties对象
		properties.setProperty("mail.transport.protocol", "smtp");// 设置传输协议
		properties.put("mail.smtp.host", EmailConstant.SMTP163SERVER);// 设置发信邮箱的smtp地址
		properties.setProperty("mail.smtp.auth", "true"); // 验证
		Authenticator auth = new Authenticator() {
			
			// 创建传入身份验证信息的 Authenticator类
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EmailConstant.getEmailaccount(), EmailConstant.getEmailpwd());
			}
		}; // 使用验证，创建一个Authenticator
		Session session = Session.getDefaultInstance(properties, auth);// 根据Properties，Authenticator创建Session
		Message message = new MimeMessage(session);// Message存储发送的电子邮件信息
		message.setFrom(new InternetAddress(EmailConstant.getEmailaccount()));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));// 设置收信邮箱
		// 指定邮箱内容及ContentType和编码方式
		message.setContent(centent, "text/html;charset=utf-8");
		message.setSubject(title);// 设置主题
		message.setSentDate(new Date());// 设置发信时间
		Transport.send(message);// 发送
		System.out.println("发送完毕！");
	}
}
