package com.maxim.pos.common.web.faces.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "dateValidator")
public class DateValidator implements Validator{

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIViewRoot root = context.getViewRoot();
        UIInput inputfromDate = (UIInput) root.findComponent("form:fromDate");
        Date fromDate = (Date) inputfromDate.getValue();
        
        UIInput inputtoDate = (UIInput) root.findComponent("form:toDate");
        Date toDate = (Date) inputtoDate.getValue();
        
        if(toDate.before(fromDate)){
            FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Validation failed", "Date To could not before Date From!");
            throw new ValidatorException(fmsg);
        }
    }

}
