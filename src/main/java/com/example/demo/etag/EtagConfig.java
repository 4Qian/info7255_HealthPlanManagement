package com.example.demo.etag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import javax.servlet.Filter;

// https://stackoverflow.com/questions/40950005/how-to-embbed-etags-into-crud-api-in-spring-boot
@EnableWebMvc
@Configuration
public class EtagConfig {

    @Bean
    public Filter etagFilter() {
        return new ShallowEtagHeaderFilter();
    }

    // https://stackoverflow.com/questions/40950005/how-to-embbed-etags-into-crud-api-in-spring-boot
//	@Bean
//	public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
//
//		System.out.println(new ShallowEtagHeaderFilter());
//		return new ShallowEtagHeaderFilter();
//	}
}