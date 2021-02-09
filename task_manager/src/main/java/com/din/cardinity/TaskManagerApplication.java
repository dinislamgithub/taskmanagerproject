package com.din.cardinity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.din.cardinity.model.Role;
import com.din.cardinity.model.User;
import com.din.cardinity.repository.UserRepository;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.service.contexts.SecurityContext;

@SpringBootApplication
@EnableSwagger2
@Configuration
public class TaskManagerApplication extends SpringBootServletInitializer {

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	public void initUsers() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		final String adminPassword = "admin";
		final String userPassword = "user";

		final Set<Role> adminRoles = new HashSet<>();
		adminRoles.add(new Role(1, "ADMIN", new Date(), true));

		final Set<Role> userRoles = new HashSet<>();
		userRoles.add(new Role(2, "USER", new Date(), true));

		List<User> users = Stream.of(
				new User(1, "admin", encoder.encode(adminPassword), "admin@gmail.com", "025698547", true, new Date(),
						"", adminRoles),
				new User(2, "user", encoder.encode(userPassword), "user@gmail.com", "025698548", true, new Date(), "",
						userRoles))
				.collect(Collectors.toList());

		if (userRepository.findAll().size() == 0) {
			userRepository.saveAll(users);
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(TaskManagerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringApplication.class);
	}

	//this portion is User_Role based spring security and jwt authentication
	public static final String AUTHORIZATION_HEADER = "Authorization";

	private ApiInfo apiInfo() {
		return new ApiInfo("My REST API", "Some custom description of API.", "1.0", "Terms of service",
				new Contact("admin", "", "admin@gmail.com"), "License of API", "API license URL",
				Collections.emptyList());
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.securityContexts(Arrays.asList(securityContext())).securitySchemes(Arrays.asList(apiKey())).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
	}

	private ApiKey apiKey() {
		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}

}
