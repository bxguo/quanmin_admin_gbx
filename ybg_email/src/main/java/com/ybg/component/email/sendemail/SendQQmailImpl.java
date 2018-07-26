package com.ybg.component.email.sendemail;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.ybg.component.email.EmailConstant;

/** QQ发送邮件 **/
public class SendQQmailImpl implements SendEmailInter {
	
	@Override
	public void sendMail(String toEmail, String title, String centent) throws Exception {
		Properties properties = new Properties();
		properties.setProperty("mail.host", "smtp.qq.com");
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		Session session = Session.getInstance(properties);
		// session.setDebug(true);
		Message msg = new MimeMessage(session);
		msg.setSubject(title);
		msg.setText(centent);
		msg.setFrom(new InternetAddress(EmailConstant.getEmailaccount()));
		String[] allemail = toEmail.replaceAll("；", ";").split(";");
		Address[] address = new Address[allemail.length];
		for (int i = 0; i < allemail.length; i++) {
			address[i] = new InternetAddress(allemail[i]);
		}
		Transport transport = session.getTransport();
		transport.connect("smtp.qq.com", EmailConstant.getEmailaccount(), EmailConstant.getEmailpwd());
		transport.sendMessage(msg, address);
		transport.close();
		System.out.println("发送完毕！");
	}
}
