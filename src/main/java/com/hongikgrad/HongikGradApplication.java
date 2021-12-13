package com.hongikgrad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableJpaAuditing
@SpringBootApplication
public class HongikGradApplication {

	public static void main(String[] args) {
		SpringApplication.run(HongikGradApplication.class, args);
		System.out.println(" _______  _______  _______           _______  _______    _______ _________ _______  _______ _________\n" +
				"(  ____ \\(  ____ \\(  ____ )|\\     /|(  ____ \\(  ____ )  (  ____ \\\\__   __/(  ___  )(  ____ )\\__   __/\n" +
				"| (    \\/| (    \\/| (    )|| )   ( || (    \\/| (    )|  | (    \\/   ) (   | (   ) || (    )|   ) (   \n" +
				"| (_____ | (__    | (____)|| |   | || (__    | (____)|  | (_____    | |   | (___) || (____)|   | |   \n" +
				"(_____  )|  __)   |     __)( (   ) )|  __)   |     __)  (_____  )   | |   |  ___  ||     __)   | |   \n" +
				"      ) || (      | (\\ (    \\ \\_/ / | (      | (\\ (           ) |   | |   | (   ) || (\\ (      | |   \n" +
				"/\\____) || (____/\\| ) \\ \\__  \\   /  | (____/\\| ) \\ \\__  /\\____) |   | |   | )   ( || ) \\ \\__   | |   \n" +
				"\\_______)(_______/|/   \\__/   \\_/   (_______/|/   \\__/  \\_______)   )_(   |/     \\||/   \\__/   )_(   \n" +
				"                                                                                                     ");
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("https://localhost:3000", "https://hongik-grad.cf", "https://hongikgrad.cf")
						.allowedMethods("*")
						.allowedHeaders("*")
						.allowCredentials(true)
						.maxAge(3600);
			}
		};
	}
}
