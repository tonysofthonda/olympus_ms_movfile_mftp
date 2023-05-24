package com.honda.olympus.ms.movfile_mftp;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


@SpringBootApplication
@PropertySource(value = "classpath:properties.xml", ignoreResourceNotFound = false)
public class Application 
{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	
	@PostConstruct
	public void init() {
		TimeZone.setDefault( TimeZone.getTimeZone("America/Mexico_City") );
	}
	
	
	@Bean
	protected MessageSource messageSource() {
	    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	    messageSource.setBasename("classpath:validation");
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	}
	
	
	@Bean
	protected LocalValidatorFactoryBean getValidator() {
	    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
	    bean.setValidationMessageSource(messageSource());
	    return bean;
	}

}
