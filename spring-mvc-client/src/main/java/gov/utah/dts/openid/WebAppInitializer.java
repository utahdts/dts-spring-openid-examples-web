package gov.utah.dts.openid;

import gov.utah.dts.openid.config.WebConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebAppInitializer implements WebApplicationInitializer {

	@Override

	public void onStartup(ServletContext container) {
		Logger.getLogger(WebAppInitializer.class.getName()).log(Level.INFO, "***** WebAppInitializer started");
		// Create the 'root' Spring application context
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(WebConfig.class);
		rootContext.getEnvironment().setActiveProfiles("jndi-context");

		// Manage the lifecycle of the root application context
		container.addListener(new ContextLoaderListener(rootContext));
		container.addListener(new RequestContextListener());
		//A "null" dispatcherType defaults to REQUEST, possibly should add error to security filter chain but has not been tested. so not turning it on yet
		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR);
		container.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"))
				.addMappingForUrlPatterns(dispatcherTypes, false, "/*");

		ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		dispatcher.addMapping("*.html");
		dispatcher.addMapping("*.json");
		dispatcher.addMapping("*.xml");

	}

}
