package gov.utah.dts.openid;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import java.util.HashSet;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootMvcClientApplication.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
		if (rootAppContext != null) {
			setSessionTrackingMode(servletContext);
		} else {
			this.logger.debug("No ContextLoaderListener registered, as createRootApplicationContext() did not return an application context");
		}
	}

	/**
	 * This prevents semi colons from being added to URL's on new sessions.  Which get blocked by spring security and cause client weirdness and errors.
	 * Example: http://localhost:8080/fsms/css/main.css;jsessionid=9E9E8B791A5F91C1DE3642CB03D64022
	 *
	 * @param servletContext context
	 */
	private void setSessionTrackingMode(ServletContext servletContext) {
		HashSet<SessionTrackingMode> set = new HashSet<>();
		set.add(SessionTrackingMode.COOKIE);
		servletContext.setSessionTrackingModes(set);
	}

}
