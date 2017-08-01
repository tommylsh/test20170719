package com.maxim.pos.common.web.faces.aspect;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.maxim.pos.common.entity.AuditLog;
import com.maxim.pos.common.exception.PosException;
import com.maxim.pos.common.service.AuditLogService;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.pos.security.enumeration.AuditAction;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.utils.FacesMessageHelper;
import com.maxim.web.faces.utils.FacesUtils;

@Component
@Aspect
public class MessageAspect {

    @Autowired
    private FacesMessageHelper facesMessageHelper;

    @Autowired
    private AuditLogService auditLogService;

    @Pointcut("@annotation(com.maxim.web.faces.annotation.OperationMessage)")
    public void process() {
    }

    @SuppressWarnings("rawtypes")
    @Around("process()")
    public void processMessage(ProceedingJoinPoint pjp) throws Throwable {
        Class clazz = pjp.getSignature().getDeclaringType();

        for (Method method : clazz.getMethods())
            if (method.getName().equals(pjp.getSignature().getName())) {

                OperationMessage type = (OperationMessage) method.getAnnotation(OperationMessage.class);
                String operationName = type.operationName();
                String userId = UserDetailsService.getUserId();
                AuditAction auditAction = AuditAction.UPDATE;

                try {
                    if (OperationMessage.OperationType.SAVE.equals(type.type())) {
                        auditAction = AuditAction.INSERT;
                    } else if (OperationMessage.OperationType.DELETE.equals(type.type())) {
                        auditAction = AuditAction.DELETE;
                    } else if (OperationMessage.OperationType.UPDATE.equals(type.type())) {
                        auditAction = AuditAction.UPDATE;
                    }

                    pjp.proceed();
                    String operationMessage = null;
                    if (StringUtils.isNotEmpty(operationName)) {
                        operationMessage = operationName + " successfully";
                        AuditLog auditLog = new AuditLog(auditAction, operationMessage, userId);
                        auditLogService.saveAuditLog(auditLog);
                    }
                    
                    if (operationMessage == null) {
                        if (OperationMessage.OperationType.SAVE.equals(type.type())) {
                            facesMessageHelper.showSaveSuccessMessage();
                        } else if (OperationMessage.OperationType.DELETE.equals(type.type())) {
                            facesMessageHelper.showDeleteSuccessMessage();
                        } else if (OperationMessage.OperationType.UPDATE.equals(type.type())) {
                            facesMessageHelper.showUpdateSuccessMessage();
                        } else if (OperationMessage.OperationType.UPLOAD.equals(type.type())) {
                            facesMessageHelper.showUploadSuccessMessage();
                        }
                    } else {
                        facesMessageHelper.showInfoMessage(operationMessage);
                    }

                    break;
                } catch (Exception e) {
                    e.printStackTrace();

                    if (e instanceof DataIntegrityViolationException) {
                        facesMessageHelper.showErrorMessage(
                                "This record has maintained relationship with other data.");
                    }else {
                        String operationMessage = null;
                        if (StringUtils.isNotEmpty(operationName)) {
                            operationMessage = operationName + " unsuccessfully: " + e.getMessage();
                            AuditLog auditLog = new AuditLog(auditAction,
                                    operationMessage, userId);
                            auditLogService.saveAuditLog(auditLog);
                        }
                        
                        if (e instanceof PosException) {
                            facesMessageHelper.showErrorMessage(e.getMessage());
                        } else {
                            String message = e.getMessage();
                            if(message.contains("sql") || message.contains("Exception") || message.contains("connection")){
                            	facesMessageHelper.showErrorMessage("Connection Test Failed!");
                            }else {
	                            facesMessageHelper
	                                    .showErrorMessage("Sorry, operation failed, please contact the administrator.");
                            }
                        }
                    }

                    FacesUtils.validationFailed();
                }

                break;
            }
    }
}