package com.hongikgrad;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
public class HongikGradApplication {

	@GetMapping("/")
	public String hello() {
		return "Hello World!";
	}

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

}
