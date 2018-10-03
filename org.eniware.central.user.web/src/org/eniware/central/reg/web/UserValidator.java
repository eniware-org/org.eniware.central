/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import org.eniware.central.user.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for user registration.
 * 
 * @version $Id$
 */
@Component
@Qualifier("Registration")
public class UserValidator implements Validator {

	/**
	 * Validate a new registration.
	 * 
	 * @param reg the registration to validate
	 * @param context the message context
	 */
	public void validateStart(User reg, Errors errors) {
		if ( !StringUtils.hasText(reg.getEmail()) ) {
			errors.rejectValue("email", "registration.email.required", "Email is required.");
		} else if ( reg.getEmail().length() > 240 ) {
			errors.rejectValue("email", "registration.email.toolong", "Email value is too long.");
		}
		if ( !StringUtils.hasText(reg.getName()) ) {
			errors.rejectValue("name", "registration.name.required", "Name is required.");
		} else if ( reg.getName().length() > 128 ) {
			errors.rejectValue("name", "registration.name.toolong", "Name value is too long.");
		}
		if ( !StringUtils.hasText(reg.getPassword()) ) {
			errors.rejectValue("password", "registration.password.required", "Password is required.");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if ( errors.hasErrors() ) {
			// don't re-validate, which can happen during webflow since 
			// this implements Validator as well as follows naming conventions
			// for flow state validation
			return;
		}
		User reg = (User)target;
		validateStart(reg, errors);
	}
	
}
