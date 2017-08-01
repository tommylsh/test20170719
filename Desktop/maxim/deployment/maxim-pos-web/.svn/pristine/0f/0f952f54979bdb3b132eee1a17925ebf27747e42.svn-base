package com.maxim.pos.common.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.maxim.i18n.MessageSource;
import com.maxim.pos.common.util.EncryptPasswordUtils;
import com.maxim.pos.security.entity.User;
import com.maxim.pos.security.service.UserService;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public static final Logger logger = LoggerFactory.getLogger(LoginAuthenticationFilter.class);

	public static final String POST = "POST";
	public static final String USERNAME = "j_username";
	public static final String PASSWORD = "j_password";
	public static final String VALIDATE_CODE = "validateCode";

	private UserService userService;
	private MessageSource messageSource;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		if (!request.getMethod().equals(POST)) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String userId = obtainUsername(request);
		String password = obtainPassword(request);
		userId = userId.trim();

		logger.info("Attempting to login with username {}", userId);

		User user = userService.findUserByUserId(userId);
		
		if (user == null) {
			throw new BadCredentialsException(
					messageSource.getMessage("security.userNotExist", new Object[] { userId }));
		}

		if (password == null) {
			throw new BadCredentialsException(messageSource.getMessage("security.passwordIncorrect", null));
		}

		
		String encryptedPassword = user.getPassword();
		
        if (!EncryptPasswordUtils.isValidated(encryptedPassword, password)) {
			logger.info("passwordIncorrect user.getPassword() is not equals");

			if (getFailureHandler() instanceof AuthenticationFailureHandler) {
				((AuthenticationFailureHandler) getFailureHandler()).saveLastUsername(request, userId);
				((AuthenticationFailureHandler) getFailureHandler()).saveLastPassword(request, password);
			}
			throw new BadCredentialsException(messageSource.getMessage("security.passwordIncorrect", null));
		}

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userId,
				encryptedPassword);

		setDetails(request, authRequest);

		return getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		Object obj = request.getParameter(USERNAME);
		return null == obj ? "" : obj.toString();
	}

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		Object obj = request.getParameter(PASSWORD);
		return null == obj ? "" : obj.toString();
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
