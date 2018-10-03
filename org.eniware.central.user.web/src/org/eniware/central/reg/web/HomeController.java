/* ==================================================================
 *  Eniware Open Source:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.reg.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the secure home page.
 * 
 * @version 1.0
 */
@Controller
public class HomeController {

	@RequestMapping(value = "/sec/home", method = RequestMethod.GET)
	public String home() {
		return "sec/home";
	}

}
