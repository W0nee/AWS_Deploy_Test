package com.ducks.goodsduck.commons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CommonsApplication {

	private static final String PROPERTIES =
			"spring.config.location=" +
<<<<<<< HEAD
					"classpath:/application.yml," +
					"classpath:/application-db.yml," +
					"classpath:/application-oauth2.yml";

	public static void main(String[] args) {

=======
			"classpath:/application.yml," +
			"classpath:/application-db.yml," +
			"classpath:/application-oauth2.yml";

	public static void main(String[] args) {

//		SpringApplication.run(CommonsApplication.class, args);
>>>>>>> baf33038316f11c98cca7fc007f0ffbf59bb7433
		new SpringApplicationBuilder(CommonsApplication.class)
				.properties(PROPERTIES)
				.run(args);
	}
}
