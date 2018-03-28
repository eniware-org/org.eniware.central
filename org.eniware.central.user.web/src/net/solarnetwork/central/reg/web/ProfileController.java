/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package net.solarnetwork.central.reg.web;

import net.solarnetwork.central.security.SecurityUser;
import net.solarnetwork.central.security.SecurityUtils;

import org.eniware.central.user.biz.RegistrationBiz;
import org.eniware.central.user.biz.UserBiz;
import org.eniware.central.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for user profile management.
 * 
 * @version 1.0
 */
@Controller
@RequestMapping("/sec/profile")
public class ProfileController {

	@Autowired
	private UserBiz userBiz;

	@Autowired
	private RegistrationBiz registrationBiz;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView viewProfile() {
		SecurityUser user = SecurityUtils.getCurrentUser();
		User u = userBiz.getUser(user.getUserId());
		return new ModelAndView("profile/view", "user", u);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editProfile() {
		SecurityUser user = SecurityUtils.getCurrentUser();
		User u = userBiz.getUser(user.getUserId());
		return new ModelAndView("profile/form", "user", u);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveProfile(User user) {
		User u = registrationBiz.updateUser(user);
		u.setPassword(RegistrationBiz.DO_NOT_CHANGE_VALUE);
		ModelAndView mv = new ModelAndView("profile/view", "user", u);
		mv.addObject(WebConstants.MODEL_KEY_STATUS_MSG, "user.profile.saved");
		return mv;
	}

	public void setUserBiz(UserBiz userBiz) {
		this.userBiz = userBiz;
	}

	public void setRegistrationBiz(RegistrationBiz registrationBiz) {
		this.registrationBiz = registrationBiz;
	}

}
