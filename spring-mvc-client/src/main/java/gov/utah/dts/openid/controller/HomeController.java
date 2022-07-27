package gov.utah.dts.openid.controller;

import gov.utah.dts.openid.Constants;
import gov.utah.dts.openid.service.AppUserDetails;
import gov.utah.dts.openid.service.SecurityUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * The Class HomeController.
 */
@Controller
public class HomeController {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(HomeController.class);

	/** The security util. */
	@Autowired
	private SecurityUtil securityUtil;

	/**
	 * Gets the grid.
	 *
	 * @param model the model
	 * @return the grid
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String getGrid(Model model) {
		AppUserDetails user = securityUtil.getActiveUser();
		if (user == null){
			return "error";
		}

		if (securityUtil.hasRole(Constants.ROLE_PUBLIC, user))  {
			return "noRollAccess";
		}

		model.addAttribute("user",user);
		return "grid";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String get403(Model model) {
		return "noRollAccess";
	}


}
