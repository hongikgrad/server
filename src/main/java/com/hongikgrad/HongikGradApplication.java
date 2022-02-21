package com.hongikgrad;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.EntityManager;

@EnableJpaAuditing
@SpringBootApplication
@Slf4j
public class HongikGradApplication {

	public static void main(String[] args) {
		SpringApplication.run(HongikGradApplication.class, args);
		log.warn("SERVER ON");
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
						.allowedOrigins("https://localhost:3000", "https://hongikgrad.com")
						.allowedMethods("*")
						.allowedHeaders("*")
						.allowCredentials(true)
						.maxAge(3600);
			}
		};
	}

	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
		return (serverFactory) -> serverFactory.addContextCustomizers(
				(context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
	}

}
