package gov.utah.dts.openid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MarshallingView;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "gov.utah.dts.openid")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/version.html").setViewName("version");
//		registry.addViewController("/error.htm").setViewName("error");
	}

	@Bean
	public BeanNameViewResolver beanViewResolver() {
		BeanNameViewResolver resolver = new BeanNameViewResolver();
		resolver.setOrder(0);
		return resolver;
	}

	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setOrder(3);
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	@Bean
	public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setContentNegotiationManager(manager);
		resolver.setOrder(1);

		List<View> defaultViews = new ArrayList<>();
		defaultViews.add(new MappingJackson2JsonView());
		defaultViews.add(marshallingView());
		resolver.setDefaultViews(defaultViews);

		List<ViewResolver> viewResolvers = new ArrayList<>();
		viewResolvers.add(getInternalResourceViewResolver());
		viewResolvers.add(beanViewResolver());
		resolver.setViewResolvers(viewResolvers);
		return resolver;
	}


	@Bean
	public MarshallingView marshallingView() {
		XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();
		xstreamMarshaller.setAutodetectAnnotations(true);
		return new MarshallingView(xstreamMarshaller);
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(createXmlHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter());

		WebMvcConfigurer.super.configureMessageConverters(converters);
	}

	private HttpMessageConverter<Object> createXmlHttpMessageConverter() {
		MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();

		XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();
		xmlConverter.setMarshaller(xstreamMarshaller);
		xmlConverter.setUnmarshaller(xstreamMarshaller);

		return xmlConverter;
	}

	/* equivalents for <mvc:resources/> tags */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/error.htm").addResourceLocations("/error.htm");
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/img/**").addResourceLocations("/img/");
		registry.addResourceHandler("/images/**").addResourceLocations("/images/");
		registry.addResourceHandler("/ico/**").addResourceLocations("/ico/");
	}

}
