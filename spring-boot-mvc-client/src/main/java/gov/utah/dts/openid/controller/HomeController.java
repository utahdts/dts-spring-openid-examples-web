package gov.utah.dts.openid.controller;

import gov.utah.dts.openid.service.AppUserDetails;
import gov.utah.dts.openid.service.SecurityUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * The Class HomeController.
 */
@Controller
@Slf4j
public class HomeController {

	/** The security util. */
	@Autowired
	private SecurityUtil securityUtil;

	/**
	 * Gets the homepage.
	 *
	 * @param model the model
	 * @return the dashboard
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String getGrid(Model model) {
		AppUserDetails user = securityUtil.getActiveUser();
		model.addAttribute("user",user);
		return "dashboard";
	}

	/**
	 * Secure area
	 *
	 * @param model the model
	 * @return the dashboard
	 */
	@RequestMapping(value = "/secure.html", method = RequestMethod.GET)
	public String getSecurePage(Model model) {
		AppUserDetails user = securityUtil.getActiveUser();
		model.addAttribute("user",user);
		return "admin";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String get403(Model model) {
		return "noRollAccess";
	}
	
}
