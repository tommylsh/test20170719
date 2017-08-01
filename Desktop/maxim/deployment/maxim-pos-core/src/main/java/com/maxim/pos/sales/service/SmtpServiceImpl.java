package com.maxim.pos.sales.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.TaskJobExceptionDetail;
import com.maxim.pos.common.enumeration.ExceptionDetailStatus;
import com.maxim.pos.common.enumeration.Severity;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.TaskJobExceptionDetailService;
import com.maxim.pos.common.util.LogUtils;

@Service("smtpService")
@Transactional
public class SmtpServiceImpl implements SmtpService {

	@Autowired
	private ApplicationSettingService applicationSettingService; 
	
	@Autowired 
	private TaskJobExceptionDetailService taskJobExceptionDetailService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private String  mailSenderAddress;


	@Override
	public boolean sendMail(Logger logger) {
		boolean result = false;
		try {
			

			  
			ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("ALERT_EMAIL");
			List<TaskJobExceptionDetail> list = taskJobExceptionDetailService.findTaskJobExeptionDetailByStatusAndSeverity(ExceptionDetailStatus.P, Severity.ERROR);
			
			LogUtils.printLog(logger, "sendMail {} {}", list.size(), applicationSetting);
			if (list.size() > 0 && applicationSetting != null)
			{
				LogUtils.printLog(logger, "sendMail {} {}", list.size(), applicationSetting.getCodeValue());

				if (applicationSetting.getCodeValue()!= null)
				{
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					String subject = "Email Alert at :" + df.format(new Date());
	//String a = new String();
//				MailSenderInfo mailSenderInfo = new MailSenderInfo(); 
//				String info = applicationSetting.getCodeValue();
//				String[] arr = info.split(";");
//				String host = "",port = "",username = "",password = "",toAddress = "";
//				for (String str : arr) {
//					String temp = str.split("=")[0];
//					String value = str.split("=")[1];
//					if (temp.equals("Host")) {
//						host = value;
//					} else if(temp.equalsIgnoreCase("Port")) {
//						port = value;
//					} else if(temp.equalsIgnoreCase("UserName")) {
//						username = value;
//					} else if(temp.equalsIgnoreCase("Password")) {
//						password = value;
//					} else if(temp.equalsIgnoreCase("ToAddress")) {
//						toAddress = value;
//					}
//				}
//				mailSenderInfo.setMailServerHost(host);
//				mailSenderInfo.setMailServerPort(port);
//				mailSenderInfo.setValidate(true);
//				mailSenderInfo.setUserName(username);
////				ochmehxvnajbgjje
//				mailSenderInfo.setPassword(password); 
//				mailSenderInfo.setFromAddress(username);
//				mailSenderInfo.setToAddress(toAddress);
//				mailSenderInfo.setSubject("TaskJobExceptionDetail");
				StringBuffer sb = new StringBuffer();
				for (TaskJobExceptionDetail taskExceptionDetail : list) {
					sb.append("ID :").append(taskExceptionDetail.getTaskJobLog().getId()).append("\n");
					sb.append("Branch :").append(taskExceptionDetail.getTaskJobLog().getBranchCode()).append("\n");
					sb.append("Job :").append(taskExceptionDetail.getTaskJobLog().getPollSchemeType()).append("\n");
					if (StringUtils.isNotBlank(taskExceptionDetail.getSource()) && StringUtils.isNotBlank(taskExceptionDetail.getDestination()))
					{
						sb.append("Source[").append(taskExceptionDetail.getSource()).append("] -> [").append(taskExceptionDetail.getDestination()).append("]\n");
					}
					sb.append("Time :").append(df.format(taskExceptionDetail.getCreateTime())).append("\n");
					if (taskExceptionDetail.getSeverity() <= 2 )
					{
						sb.append(taskExceptionDetail.getExceptionContent()+"\n");
						taskExceptionDetail.setStatus(ExceptionDetailStatus.S);
					}
					else
					{
						taskExceptionDetail.setStatus(ExceptionDetailStatus.D);
					}
				}
//				mailSenderInfo.setContent(sb.toString()); 

				MimeMessage message = mailSender.createMimeMessage();

				 message.setFrom(mailSenderAddress);
				 message.setSubject(subject);
				 message.setText(sb.toString());
				String[] emails = applicationSetting.getCodeValue().split(";");
				{
					for (String email :emails)
					{
						message.addRecipients(RecipientType.TO, email);
//						 SimpleMailMessage message = new SimpleMailMessage();
//						 message.setTo(emai);
//						  
//							LogUtils.printLog(logger, "sendMail {} {}", emai, sb.toString());
//							 mailSender.send(message);
					}
				}
				 mailSender.send(message);

//				result = MailSendUtil.sendTextMail(mailSenderInfo);
			}
//			if(result){
				for (TaskJobExceptionDetail taskExceptionDetail : list) {
//					taskExceptionDetail.setStatus(ExceptionDetailStatus.S);
					taskJobExceptionDetailService.update(taskExceptionDetail);
				}
//			}
				LogUtils.printLog(logger, "sendMail done");
			}
		} catch (Exception e) {
			LogUtils.printException(logger, "send email result is {}", e);
		}
		return result;
	}

}
