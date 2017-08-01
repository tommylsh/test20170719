package com.maxim.pos.common.util.mail;


import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSendUtil {

	public static boolean sendTextMail(MailSenderInfo sendInfo)throws Exception{
		Properties prop  = sendInfo.getProperties();
		MyAuthenticator auth = new MyAuthenticator(sendInfo.getUserName(),sendInfo.getPassword());
		//根据i需要发送的属性 创建会话对象 session
		Session session = Session.getDefaultInstance(prop,auth);
		//邮件消息实体
		Message mailMessage = new MimeMessage(session);
		
			//发件人
			Address from = new InternetAddress(sendInfo.getFromAddress());
			mailMessage.setFrom(from);
			//收件人
			Address to = new InternetAddress(sendInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO,to);
			//邮件标题
			mailMessage.setSubject(sendInfo.getSubject());
			//发送时间
			mailMessage.setSentDate(new Date());
			//邮件内容
			mailMessage.setText(sendInfo.getContent());
			//调用JavaX Mail的api发送邮件
			Transport.send(mailMessage);
		return true;
	}
	public static boolean sendSimpleEmail(String reciver,String subject,String content)throws Exception{
		MailSenderInfo sendInfo = new MailSenderInfo();
		sendInfo.setMailServerHost("smtp.163.com");
		sendInfo.setMailServerPort("25");
		sendInfo.setValidate(true);
		sendInfo.setUserName("17788953619@163.com");
		sendInfo.setPassword("sixstar79196052");
		sendInfo.setFromAddress("17788953619@163.com");
		sendInfo.setToAddress(reciver);
		sendInfo.setSubject(subject);
		sendInfo.setContent(content);
		boolean result = sendTextMail(sendInfo);
		return result;
	}
	public static void main(String [] args) throws Exception{
		MailSenderInfo sendInfo = new MailSenderInfo();
		sendInfo.setMailServerHost("smtp.qq.com");
		sendInfo.setMailServerPort("587");
		sendInfo.setValidate(true);
		sendInfo.setUserName("1355479511@qq.com");
//		ochmehxvnajbgjje
		sendInfo.setPassword("ochmehxvnajbgjje"); 
		sendInfo.setFromAddress("1355479511@qq.com");
		sendInfo.setToAddress("237069127@qq.com");
		sendInfo.setSubject("测试邮件!!");
		sendInfo.setContent("验证码测试邮件"); 
		sendTextMail(sendInfo);
		System.out.println("...");
	}
}
 