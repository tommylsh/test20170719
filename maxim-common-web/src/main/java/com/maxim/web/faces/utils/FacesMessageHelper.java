package com.maxim.web.faces.utils;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maxim.i18n.MessageSource;

@Component("facesMessageHelper")
public class FacesMessageHelper {

	private static final String TITLE = "common.info";

	@Autowired
	private MessageSource messageSource;

	public void showUpdateSuccessMessage() {
		showMessage(FacesMessage.SEVERITY_INFO, messageSource.getMessage("common.updateSuccess"));
	}

	public void showSaveSuccessMessage() {
		showMessage(FacesMessage.SEVERITY_INFO, messageSource.getMessage("common.saveSuccess"));
	}

	public void showDeleteSuccessMessage() {
		showMessage(FacesMessage.SEVERITY_INFO, messageSource.getMessage("common.deleteSuccess"));
	}
	public void showUploadSuccessMessage() {
        showMessage(FacesMessage.SEVERITY_INFO, messageSource.getMessage("common.uploadSuccess"));
    }

	public void showInfoMessage(String message) {
		showMessage(FacesMessage.SEVERITY_INFO, message);
	}

	public void showWarnMessage(String message) {
		showMessage(FacesMessage.SEVERITY_WARN, message);
	}

	public void showErrorMessage(String message) {
		showMessage(FacesMessage.SEVERITY_ERROR, message);
	}

	public void showMessage(Severity level, String message) {
		FacesUtils.getInstance().addMessage(null,
				new FacesMessage(level, messageSource.getMessage(TITLE), message));
	}

}
